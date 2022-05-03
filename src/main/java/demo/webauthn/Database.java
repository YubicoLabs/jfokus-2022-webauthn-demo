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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.exception.Base64UrlException;
import demo.webauthn.tables.records.UsersRecord;
import demo.webauthn.tables.records.WebauthnCredentialsRecord;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.mindrot.jbcrypt.BCrypt;

@Slf4j
public class Database implements CredentialRepository {

  private static final ZoneOffset instantStorageZoneOffset = ZoneOffset.UTC;

  private final Clock clock = Clock.systemUTC();
  private final DataSource dataSource;

  public Database(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Database() {
    this(makeDataSource());
  }

  private static BasicDataSource makeDataSource() {
    BasicDataSource ds = new BasicDataSource();
    ds.setUrl("jdbc:mysql://localhost:3306/webauthn_demo?allowPublicKeyRetrieval=true");
    ds.setUsername("webauthn_demo");
    ds.setPassword("1234");

    ds.setDriverClassName("org.mariadb.jdbc.Driver");

    return ds;
  }

  private DSLContext jooq() {
    return DSL.using(
        dataSource, SQLDialect.MYSQL, new Settings().withExecuteWithOptimisticLocking(true));
  }

  private static ObjectMapper json() {
    return new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
        .setBase64Variant(Base64Variants.MODIFIED_FOR_URL);
  }

  public Optional<UserIdentity> getUserIdentity(UserId userId) {
    return jooq().selectFrom(Tables.USERS).where(Tables.USERS.ID.eq(userId.getId())).stream()
        .map(
            record -> {
              try {
                return UserIdentity.builder()
                    .name(record.getUsername())
                    .displayName(record.getUsername())
                    .id(userIdToUserHandle(new UserId(record.getId())))
                    .build();
              } catch (Base64UrlException e) {
                throw new RuntimeException(e);
              }
            })
        .findAny();
  }

  public boolean userExists(Username username) {
    return jooq()
            .selectCount()
            .from(Tables.USERS)
            .where(Tables.USERS.USERNAME.eq(username.getUsername()))
            .fetch()
            .get(0)
            .value1()
        > 0;
  }

  public Username getUsername(UserId userId) {
    return new Username(
        jooq()
            .selectFrom(Tables.USERS)
            .where(Tables.USERS.ID.eq(userId.getId()))
            .fetch()
            .get(0)
            .getUsername());
  }

  @Override
  public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(final String username) {
    return jooq()
        .select()
        .from(Tables.WEBAUTHN_CREDENTIALS)
        .join(Tables.USERS)
        .on(Tables.WEBAUTHN_CREDENTIALS.USER_ID.eq(Tables.USERS.ID))
        .where(Tables.USERS.USERNAME.eq(username))
        .stream()
        .map(
            record -> {
              final Set<AuthenticatorTransport> transports = new HashSet<>();
              if (record.get(Tables.WEBAUTHN_CREDENTIALS.TRANSPORTS) != null) {
                ObjectMapper json = json();
                try {
                  for (Iterator<JsonNode> it =
                          json.readTree(record.get(Tables.WEBAUTHN_CREDENTIALS.TRANSPORTS))
                              .elements();
                      it.hasNext(); ) {
                    transports.add(AuthenticatorTransport.of(it.next().textValue()));
                  }
                } catch (JsonProcessingException e) {
                  throw new RuntimeException(e);
                }
              }

              return PublicKeyCredentialDescriptor.builder()
                  .id(new ByteArray(record.get(Tables.WEBAUTHN_CREDENTIALS.CREDENTIAL_ID)))
                  .transports(transports)
                  .build();
            })
        .collect(Collectors.toSet());
  }

  @Override
  public Optional<ByteArray> getUserHandleForUsername(final String username) {
    return jooq().selectFrom(Tables.USERS).where(Tables.USERS.USERNAME.eq(username)).stream()
        .findAny()
        .map(
            record -> {
              try {
                return userIdToUserHandle(new UserId(record.getId()));
              } catch (Base64UrlException e) {
                throw new RuntimeException(e);
              }
            });
  }

  @Override
  public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
    return jooq()
        .selectFrom(Tables.USERS)
        .where(Tables.USERS.ID.eq(userHandleToUserId(userHandle).getId()))
        .stream()
        .findAny()
        .map(UsersRecord::getUsername);
  }

  @Override
  public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
    return jooq()
        .selectFrom(Tables.WEBAUTHN_CREDENTIALS)
        .where(Tables.WEBAUTHN_CREDENTIALS.USER_ID.eq(userHandleToUserId(userHandle).getId()))
        .and(
            Tables.WEBAUTHN_CREDENTIALS.CREDENTIAL_ID.eq(
                new CredentialId(credentialId).getId().getBytes()))
        .stream()
        .flatMap(Database::parseWebauthnCredential)
        .findAny();
  }

  @Override
  public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
    return jooq()
        .selectFrom(Tables.WEBAUTHN_CREDENTIALS)
        .where(
            Tables.WEBAUTHN_CREDENTIALS.CREDENTIAL_ID.eq(
                new CredentialId(credentialId).getId().getBytes()))
        .stream()
        .flatMap(Database::parseWebauthnCredential)
        .collect(Collectors.toSet());
  }

  private static Stream<RegisteredCredential> parseWebauthnCredential(
      WebauthnCredentialsRecord record) {
    final ByteArray credentialId = new ByteArray(record.getCredentialId());
    try {
      return Stream.of(
          RegisteredCredential.builder()
              .credentialId(credentialId)
              .userHandle(userIdToUserHandle(new UserId(record.getUserId())))
              .publicKeyCose(new ByteArray(record.getPublicKeyCose()))
              .signatureCount(record.getSignatureCount().longValue())
              .build());
    } catch (Base64UrlException e) {
      log.error("Failed to decode user handle: {}", record.getUserId(), e);
      return Stream.empty();
    }
  }

  public Set<WebauthnCredentialView> getUserWebauthnCredentials(final UserId userId) {
    return jooq()
        .selectFrom(Tables.WEBAUTHN_CREDENTIALS)
        .where(Tables.WEBAUTHN_CREDENTIALS.USER_ID.eq(userId.getId()))
        .stream()
        .flatMap(
            record -> {
              final ByteArray id = new ByteArray(record.getCredentialId());
              try {
                return Stream.of(
                    new WebauthnCredentialView(
                        new CredentialId(id),
                        new UserId(record.getUserId()),
                        Optional.ofNullable(record.getNickname()),
                        Optional.ofNullable(record.getCreateTime()).map(Database::toInstant),
                        Optional.ofNullable(record.getLastUseTime()).map(Database::toInstant),
                        record.getDiscoverable() && record.getUvCapable()));
              } catch (Exception e) {
                log.error("Failed to decode WebAuthn credential: {}", id, e);
                return Stream.empty();
              }
            })
        .collect(Collectors.toSet());
  }

  public boolean userHasWebauthnCredentials(final UserId userId) {
    return jooq()
            .selectCount()
            .from(Tables.WEBAUTHN_CREDENTIALS)
            .where(Tables.WEBAUTHN_CREDENTIALS.USER_ID.eq(userId.getId()))
            .fetch()
            .get(0)
            .value1()
        > 0;
  }

  public void storeWebauthnCredential(
      final UserId userId,
      final PublicKeyCredential<
              AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs>
          credential,
      final RegistrationResult registrationResult,
      final Optional<String> nickname) {

    final int numInserted =
        jooq()
            .transactionResult(
                configuration -> {
                  DSLContext dsl = configuration.dsl();

                  return dsl.insertInto(Tables.WEBAUTHN_CREDENTIALS)
                      .set(Tables.WEBAUTHN_CREDENTIALS.USER_ID, userId.getId())
                      .set(Tables.WEBAUTHN_CREDENTIALS.CREDENTIAL_ID, credential.getId().getBytes())
                      .set(Tables.WEBAUTHN_CREDENTIALS.NICKNAME, nickname.orElse(null))
                      .set(Tables.WEBAUTHN_CREDENTIALS.CREATE_TIME, now())
                      .set(Tables.WEBAUTHN_CREDENTIALS.LAST_USE_TIME, now())
                      .set(
                          Tables.WEBAUTHN_CREDENTIALS.SIGNATURE_COUNT,
                          UInteger.valueOf(
                              credential
                                  .getResponse()
                                  .getParsedAuthenticatorData()
                                  .getSignatureCounter()))
                      .set(
                          Tables.WEBAUTHN_CREDENTIALS.TRANSPORTS,
                          json().writeValueAsString(credential.getResponse().getTransports()))
                      .set(
                          Tables.WEBAUTHN_CREDENTIALS.PUBLIC_KEY_COSE,
                          registrationResult.getPublicKeyCose().getBytes())
                      .set(
                          Tables.WEBAUTHN_CREDENTIALS.DISCOVERABLE,
                          registrationResult.isDiscoverable().orElse(false))
                      .set(
                          Tables.WEBAUTHN_CREDENTIALS.UV_CAPABLE,
                          credential.getResponse().getParsedAuthenticatorData().getFlags().UV)
                      .set(
                          Tables.WEBAUTHN_CREDENTIALS.ATTESTATION_OBJECT,
                          credential.getResponse().getAttestationObject().getBytes())
                      .set(
                          Tables.WEBAUTHN_CREDENTIALS.CREATE_CLIENTDATAJSON,
                          credential.getResponse().getClientDataJSON().getBytes())
                      .execute();
                });

    log.info("Inserted credential {} for {} ({} rows)", credential.getId(), userId, numInserted);
  }

  public void updateWebauthnCredential(
      final UserId userId,
      final CredentialId credentialId,
      final long signatureCount,
      final boolean uv) {
    log.info(
        "Update credential {}/{}: signatureCount={}, uv={}",
        userId,
        credentialId,
        signatureCount,
        uv);
    jooq()
        .update(Tables.WEBAUTHN_CREDENTIALS)
        .set(Tables.WEBAUTHN_CREDENTIALS.SIGNATURE_COUNT, UInteger.valueOf(signatureCount))
        .set(
            Tables.WEBAUTHN_CREDENTIALS.UV_CAPABLE,
            Tables.WEBAUTHN_CREDENTIALS.UV_CAPABLE.bitOr(uv))
        .set(Tables.WEBAUTHN_CREDENTIALS.LAST_USE_TIME, now())
        .where(Tables.WEBAUTHN_CREDENTIALS.USER_ID.eq(userId.getId()))
        .and(Tables.WEBAUTHN_CREDENTIALS.CREDENTIAL_ID.eq(credentialId.getId().getBytes()))
        .execute();
  }

  public void deleteWebauthnCredential(final UserId userId, final CredentialId credentialId) {
    final int numDeleted =
        jooq()
            .deleteFrom(Tables.WEBAUTHN_CREDENTIALS)
            .where(Tables.WEBAUTHN_CREDENTIALS.USER_ID.eq(userId.getId()))
            .and(Tables.WEBAUTHN_CREDENTIALS.CREDENTIAL_ID.eq(credentialId.getId().getBytes()))
            .execute();
    log.info("Dropped credential {} from {} ({} rows)", credentialId, userId, numDeleted);
  }

  public static ByteArray userIdToUserHandle(final UserId userId) throws Base64UrlException {
    return new ByteArray(userId.getId().getBytes(StandardCharsets.UTF_8));
  }

  public static UserId userHandleToUserId(final ByteArray userHandle) {
    return new UserId(new String(userHandle.getBytes(), StandardCharsets.UTF_8));
  }

  public LocalDateTime now() {
    return fromInstant(clock.instant());
  }

  private static Instant toInstant(LocalDateTime d) {
    return d.toInstant(instantStorageZoneOffset);
  }

  public static LocalDateTime fromInstant(Instant d) {
    return LocalDateTime.ofInstant(d, instantStorageZoneOffset);
  }

  public UserId createUser(final Username username, final PasswordBcrypt password) {
    final UserId id = UserId.generate();
    jooq()
        .insertInto(Tables.USERS)
        .set(Tables.USERS.ID, id.getId())
        .set(Tables.USERS.USERNAME, username.getUsername())
        .set(Tables.USERS.CREATE_TIME, now())
        .set(Tables.USERS.PASSWORD_BCRYPT, password.getBcrypt())
        .execute();

    return id;
  }

  public Optional<UserId> checkUserPassword(
      @NonNull final Username username, @NonNull final Secret<String> password) {
    final Result<UsersRecord> userRecord =
        jooq()
            .selectFrom(Tables.USERS)
            .where(Tables.USERS.USERNAME.eq(username.getUsername()))
            .fetch();

    if (userRecord.size() != 1) {
      log.info(
          "Check password: too few or too many ({}) users for username: {}",
          userRecord.size(),
          username);
      return Optional.empty();

    } else {
      PasswordBcrypt currentPassword =
          new PasswordBcrypt(userRecord.stream().findAny().get().getPasswordBcrypt());
      final boolean result = BCrypt.checkpw(password.reveal(), currentPassword.getBcrypt());
      log.info("Check password: {} - correct: {}", username, result);

      if (result) {
        return Optional.of(new UserId(userRecord.stream().findAny().get().getId()));
      } else {
        return Optional.empty();
      }
    }
  }
}
