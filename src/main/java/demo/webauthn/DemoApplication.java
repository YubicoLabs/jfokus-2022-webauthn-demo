// Copyright 2022 Yubico AB
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package demo.webauthn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yubico.util.Either;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import demo.webauthn.data.AssertionRequestWrapper;
import demo.webauthn.data.AssertionResponse;
import demo.webauthn.data.RegistrationRequest;
import demo.webauthn.data.RegistrationResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemoApplication {

  private static final SecureRandom random = new SecureRandom();

  private final Cache<ByteArray, AssertionRequestWrapper> assertRequestStorage;
  private final Cache<ByteArray, RegistrationRequest> registerRequestStorage;
  private final Database database;
  private final SessionManager sessions = new SessionManager();

  private final ObjectMapper jsonMapper =
      new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
          .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
          .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
          .setBase64Variant(Base64Variants.MODIFIED_FOR_URL)
          .registerModule(new Jdk8Module())
          .registerModule(new JavaTimeModule())
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

  private final RelyingParty rp;

  public DemoApplication() {
    this(new Database(), newCache(), newCache(), Config.rpIdentity, Config.origins);
  }

  public DemoApplication(
      Database database,
      Cache<ByteArray, RegistrationRequest> registerRequestStorage,
      Cache<ByteArray, AssertionRequestWrapper> assertRequestStorage,
      RelyingPartyIdentity rpIdentity,
      Set<String> origins) {
    this.database = database;
    this.registerRequestStorage = registerRequestStorage;
    this.assertRequestStorage = assertRequestStorage;

    rp =
        RelyingParty.builder()
            .identity(rpIdentity)
            .credentialRepository(this.database)
            .origins(origins)
            .build();
  }

  private static ByteArray generateRandom(int length) {
    byte[] bytes = new byte[length];
    random.nextBytes(bytes);
    return new ByteArray(bytes);
  }

  private static <K, V> Cache<K, V> newCache() {
    return CacheBuilder.newBuilder()
        .maximumSize(100)
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .build();
  }

  @Value
  public static class CreateUserResult {
    boolean success = true;
    UserId userId;
    Username username;
    @JsonIgnore SessionToken sessionToken;
  }

  public Either<String, CreateUserResult> createUser(Username username, Secret<String> newPassword)
      throws ExecutionException {
    if (database.userExists(username)) {
      return Either.left("User already exists: " + username.getUsername());
    } else {
      final UserId userId = database.createUser(username, PasswordBcrypt.hash(newPassword));
      return Either.right(new CreateUserResult(userId, username, sessions.createSession(userId)));
    }
  }

  public Optional<UserId> checkUserPassword(Username username, Secret<String> password) {
    return database.checkUserPassword(username, password);
  }

  public boolean userHasWebauthnCredentials(UserId userId) {
    return database.userHasWebauthnCredentials(userId);
  }

  public SessionToken createSession(UserId userId) throws ExecutionException {
    return sessions.createSession(userId);
  }

  @Value
  public static class GetSessionResponse {
    boolean success = true;
    UserId userId;
    Username username;
    Set<WebauthnCredentialView> credentials;
  }

  public Optional<GetSessionResponse> getSession(SessionToken sessionToken) {
    return sessions
        .getSession(sessionToken)
        .map(
            userId ->
                new GetSessionResponse(
                    userId,
                    database.getUsername(userId),
                    database.getUserWebauthnCredentials(userId)));
  }

  public void deleteSession(Optional<SessionToken> sessionToken) {
    sessions.endSession(sessionToken);
  }

  public boolean deleteCredential(SessionToken sessionToken, CredentialId credentialId) {
    Optional<UserId> userId = sessions.getSession(sessionToken);
    if (userId.isPresent()) {
      database.deleteWebauthnCredential(userId.get(), credentialId);
      return true;
    } else {
      return false;
    }
  }

  public Either<String, RegistrationRequest> startRegistration(SessionToken sessionToken) {
    log.trace("startRegistration session: {}", sessionToken);

    Optional<UserId> userId = sessions.getSession(sessionToken);
    if (userId.isPresent()) {
      final Optional<UserIdentity> userIdentity = database.getUserIdentity(userId.get());
      if (userIdentity.isPresent()) {
        RegistrationRequest request =
            new RegistrationRequest(
                generateRandom(32),
                rp.startRegistration(
                    StartRegistrationOptions.builder()
                        .user(userIdentity.get())
                        .authenticatorSelection(AuthenticatorSelectionCriteria.builder().build())
                        .build()));
        registerRequestStorage.put(request.getRequestId(), request);
        return Either.right(request);
      } else {
        return Either.left("No such user: " + userId.get());
      }
    } else {
      return Either.left("No such session: " + sessionToken);
    }
  }

  @Value
  public static class SuccessfulRegistrationResult {
    boolean success = true;
  }

  public Either<List<String>, SuccessfulRegistrationResult> finishRegistration(
      String responseJson, SessionToken sessionToken) {
    log.trace("finishRegistration responseJson: {}", responseJson);
    RegistrationResponse response = null;
    try {
      response = jsonMapper.readValue(responseJson, RegistrationResponse.class);
    } catch (IOException e) {
      log.error("JSON error in finishRegistration; responseJson: {}", responseJson, e);
      return Either.left(
          Arrays.asList(
              "Registration failed!", "Failed to decode response object.", e.getMessage()));
    }

    Optional<UserId> userId = sessions.getSession(sessionToken);
    if (userId.isPresent()) {
      log.info("Session token accepted for user {}", userId.get());

      RegistrationRequest request = registerRequestStorage.getIfPresent(response.getRequestId());
      registerRequestStorage.invalidate(response.getRequestId());

      if (request == null) {
        log.debug("fail finishRegistration responseJson: {}", responseJson);
        return Either.left(
            Arrays.asList("Registration failed!", "No such registration in progress."));
      } else {
        try {
          final RegistrationResult registration =
              rp.finishRegistration(
                  FinishRegistrationOptions.builder()
                      .request(request.getPublicKeyCredentialCreationOptions())
                      .response(response.getCredential())
                      .build());

          database.storeWebauthnCredential(userId.get(), response.getCredential(), registration);

          return Either.right(new SuccessfulRegistrationResult());

        } catch (RegistrationFailedException e) {
          log.debug("fail finishRegistration responseJson: {}", responseJson, e);
          return Either.left(Arrays.asList("Registration failed!", e.getMessage()));

        } catch (Exception e) {
          log.error("fail finishRegistration responseJson: {}", responseJson, e);
          return Either.left(
              Arrays.asList(
                  "Registration failed unexpectedly; this is likely a bug.", e.getMessage()));
        }
      }
    } else {
      return Either.left(List.of("No such session: " + sessionToken));
    }
  }

  public Either<List<String>, AssertionRequestWrapper> startAuthentication(UserId userId) {
    log.trace("startAuthentication userId: {}", userId);

    AssertionRequestWrapper request =
        new AssertionRequestWrapper(
            generateRandom(32),
            rp.startAssertion(
                StartAssertionOptions.builder()
                    .username(database.getUserIdentity(userId).get().getName())
                    .build()));

    assertRequestStorage.put(request.getRequestId(), request);

    return Either.right(request);
  }

  @Value
  public static class SuccessfulAuthenticationResult {
    boolean success = true;
    Username username;
    UserId userId;

    @JsonIgnore SessionToken sessionToken;
  }

  public Either<List<String>, SuccessfulAuthenticationResult> finishAuthentication(
      String responseJson) {
    log.trace("finishAuthentication responseJson: {}", responseJson);

    final AssertionResponse response;
    try {
      response = jsonMapper.readValue(responseJson, AssertionResponse.class);
    } catch (IOException e) {
      log.debug("Failed to decode response object", e);
      return Either.left(
          Arrays.asList("Assertion failed!", "Failed to decode response object.", e.getMessage()));
    }

    AssertionRequestWrapper request = assertRequestStorage.getIfPresent(response.getRequestId());
    assertRequestStorage.invalidate(response.getRequestId());

    if (request == null) {
      return Either.left(Arrays.asList("Assertion failed!", "No such assertion in progress."));
    } else {
      try {
        AssertionResult result =
            rp.finishAssertion(
                FinishAssertionOptions.builder()
                    .request(request.getRequest())
                    .response(response.getCredential())
                    .build());

        if (result.isSuccess()) {
          final UserId userId = Database.userHandleToUserId(result.getUserHandle());
          try {
            database.updateWebauthnCredential(
                userId, new CredentialId(result.getCredentialId()), result.getSignatureCount());
          } catch (Exception e) {
            log.error(
                "Failed to update credential \"{}\" for user \"{}\"",
                result.getUsername(),
                response.getCredential().getId(),
                e);
          }

          return Either.right(
              new SuccessfulAuthenticationResult(
                  new Username(result.getUsername()), userId, sessions.createSession(userId)));
        } else {
          return Either.left(Collections.singletonList("Assertion failed: Invalid assertion."));
        }
      } catch (AssertionFailedException e) {
        log.debug("Assertion failed", e);
        return Either.left(Arrays.asList("Assertion failed!", e.getMessage()));
      } catch (Exception e) {
        log.error("Assertion failed", e);
        return Either.left(
            Arrays.asList("Assertion failed unexpectedly; this is likely a bug.", e.getMessage()));
      }
    }
  }
}
