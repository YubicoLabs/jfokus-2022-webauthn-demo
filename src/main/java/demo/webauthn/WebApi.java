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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yubico.util.Either;
import demo.webauthn.data.AssertionRequestWrapper;
import demo.webauthn.data.RegistrationRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class WebApi {

  private final DemoApplication server;
  private final ObjectMapper jsonMapper =
      new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
          .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
          .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
          .setBase64Variant(Base64Variants.MODIFIED_FOR_URL)
          .registerModule(new Jdk8Module())
          .registerModule(new JavaTimeModule())
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  private final JsonNodeFactory jsonFactory = JsonNodeFactory.instance;

  public WebApi() {
    this(new DemoApplication());
  }

  public WebApi(DemoApplication server) {
    this.server = server;
  }

  @Context private UriInfo uriInfo;

  private final class IndexResponse {
    public final Index actions = new Index();
    public final Info info = new Info();

    private IndexResponse() throws MalformedURLException {}
  }

  private final class Index {
    public final URL authenticate;
    public final URL createUser;
    public final URL register;

    public Index() throws MalformedURLException {
      authenticate = uriInfo.getAbsolutePathBuilder().path("authenticate").build().toURL();
      createUser = uriInfo.getAbsolutePathBuilder().path("create-user").build().toURL();
      register = uriInfo.getAbsolutePathBuilder().path("register").build().toURL();
    }
  }

  private final class Info {
    public final URL version;

    public Info() throws MalformedURLException {
      version = uriInfo.getAbsolutePathBuilder().path("version").build().toURL();
    }
  }

  @GET
  public Response index() throws IOException {
    return Response.ok(writeJson(new IndexResponse())).build();
  }

  @Consumes("application/x-www-form-urlencoded")
  @Path("create-user")
  @POST
  public Response createUser(
      @NonNull @FormParam("username") Username username,
      @NonNull @FormParam("password") String password)
      throws SQLException, ExecutionException {
    Either<String, DemoApplication.CreateUserResult> result =
        server.createUser(username, Secret.of(password));
    if (result.isRight()) {
      return startResponse("createUser", result.right().get())
          .cookie(
              NewCookie.valueOf(
                  "sessionToken="
                      + result.right().get().getSessionToken().getValue()
                      + ";Secure;HttpOnly;SameSite=strict;Path=/"))
          .build();
    } else {
      return messagesJson(Response.status(Status.CONFLICT), result.left().get()).build();
    }
  }

  @Consumes("application/x-www-form-urlencoded")
  @Path("authenticate")
  @POST
  public Response login(
      @NonNull @FormParam("username") Username username,
      @NonNull @FormParam("password") String password)
      throws MalformedURLException, ExecutionException {

    final Optional<UserId> userId = server.checkUserPassword(username, Secret.of(password));
    if (userId.isPresent()) {
      if (server.userHasWebauthnCredentials(userId.get())) {
        return startAuthentication(userId.get());

      } else {
        SessionToken sessionToken = server.createSession(userId.get());
        return startResponse("authenticate", new LoginSuccessResponse(userId.get(), username))
            .cookie(
                NewCookie.valueOf(
                    "sessionToken="
                        + sessionToken.getValue()
                        + ";Secure;HttpOnly;SameSite=strict;Path=/"))
            .build();
      }
    } else {
      return messagesJson(Response.status(Status.FORBIDDEN), "Incorrect username or password.")
          .build();
    }
  }

  private final class StartRegistrationResponse {
    public final boolean success = true;
    public final RegistrationRequest request;
    public final StartRegistrationActions actions = new StartRegistrationActions();

    private StartRegistrationResponse(RegistrationRequest request) throws MalformedURLException {
      this.request = request;
    }
  }

  private final class StartRegistrationActions {
    public final URL finish = uriInfo.getAbsolutePathBuilder().path("finish").build().toURL();

    private StartRegistrationActions() throws MalformedURLException {}
  }

  @Consumes("application/x-www-form-urlencoded")
  @Path("register")
  @POST
  public Response startRegistration(@CookieParam("sessionToken") SessionToken sessionToken)
      throws MalformedURLException, ExecutionException {
    log.trace("startRegistration session: {}", sessionToken);
    Either<String, RegistrationRequest> result = server.startRegistration(sessionToken);

    if (result.isRight()) {
      return startResponse("startRegistration", new StartRegistrationResponse(result.right().get()))
          .build();
    } else {
      return messagesJson(Response.status(Status.BAD_REQUEST), result.left().get()).build();
    }
  }

  @Path("register/finish")
  @POST
  public Response finishRegistration(
      @NonNull @CookieParam("sessionToken") SessionToken sessionToken,
      @NonNull String responseJson) {
    log.trace("finishRegistration responseJson: {}", responseJson);
    Either<List<String>, DemoApplication.SuccessfulRegistrationResult> result =
        server.finishRegistration(responseJson, sessionToken);
    return finishResponse(
            result,
            "Attestation verification failed; further error message(s) were unfortunately lost to an internal server error.",
            "finishRegistration",
            responseJson)
        .build();
  }

  private final class StartAuthenticationResponse {
    public final boolean success = true;
    public final AssertionRequestWrapper request;
    public final StartAuthenticationActions actions = new StartAuthenticationActions();

    private StartAuthenticationResponse(AssertionRequestWrapper request)
        throws MalformedURLException {
      this.request = request;
    }
  }

  private final class StartAuthenticationActions {
    public final URL finish = uriInfo.getAbsolutePathBuilder().path("finish").build().toURL();

    private StartAuthenticationActions() throws MalformedURLException {}
  }

  private Response startAuthentication(@NonNull UserId userId) throws MalformedURLException {
    log.trace("startAuthentication userId: {}", userId);

    Either<List<String>, AssertionRequestWrapper> request = server.startAuthentication(userId);
    return startResponse(
            "startAuthentication", new StartAuthenticationResponse(request.right().get()))
        .build();
  }

  @Path("authenticate/finish")
  @POST
  public Response finishAuthentication(@NonNull String responseJson) {
    log.trace("finishAuthentication responseJson: {}", responseJson);

    Either<List<String>, DemoApplication.SuccessfulAuthenticationResult> result =
        server.finishAuthentication(responseJson);

    return finishResponse(
            result,
            "Authentication verification failed; further error message(s) were unfortunately lost to an internal server error.",
            "finishAuthentication",
            responseJson)
        .cookie(
            NewCookie.valueOf(
                "sessionToken="
                    + result.right().get().getSessionToken().getValue()
                    + ";Secure;HttpOnly;SameSite=strict;Path=/"))
        .build();
  }

  @Path("credential/{credentialId}")
  @DELETE
  public Response deleteCredential(
      @NonNull @CookieParam("sessionToken") SessionToken sessionToken,
      @NonNull @PathParam("credentialId") CredentialId credentialId)
      throws SQLException {
    if (server.deleteCredential(sessionToken, credentialId)) {
      return Response.ok("{\"success\":true}").build();
    } else {
      return messagesJson(
              Response.status(Status.BAD_REQUEST), "Invalid session or unknown credential ID.")
          .build();
    }
  }

  @Path("session")
  @GET
  public Response getSession(@CookieParam("sessionToken") SessionToken sessionToken) {
    return Optional.ofNullable(sessionToken)
        .flatMap(server::getSession)
        .map(session -> startResponse("getSession", session))
        .orElseGet(
            () ->
                messagesJson(Response.status(Status.FORBIDDEN), "No such session: " + sessionToken)
                    .cookie(
                        NewCookie.valueOf(
                            "sessionToken=null;Max-Age=0;Secure;HttpOnly;SameSite=strict;Path=/")))
        .build();
  }

  @Path("logout")
  @POST
  public Response logout(@CookieParam("sessionToken") Optional<SessionToken> sessionToken) {
    server.deleteSession(sessionToken);
    return Response.ok("{\"success\":true}")
        .cookie(
            NewCookie.valueOf("sessionToken=null;Max-Age=0;Secure;HttpOnly;SameSite=strict;Path=/"))
        .build();
  }

  private ResponseBuilder startResponse(String operationName, Object request) {
    try {
      String json = writeJson(request);
      log.debug("{} JSON response: {}", operationName, json);
      return Response.ok(json);
    } catch (IOException e) {
      log.error("Failed to encode response as JSON: {}", request, e);
      return jsonFail();
    }
  }

  private ResponseBuilder finishResponse(
      Either<List<String>, ?> result,
      String jsonFailMessage,
      String methodName,
      String responseJson) {
    if (result.isRight()) {
      try {
        return Response.ok(writeJson(result.right().get()));
      } catch (JsonProcessingException e) {
        log.error("Failed to encode response as JSON: {}", result.right().get(), e);
        return messagesJson(Response.ok(), jsonFailMessage);
      }
    } else {
      log.debug("fail {} responseJson: {}", methodName, responseJson);
      return messagesJson(Response.status(Status.BAD_REQUEST), result.left().get());
    }
  }

  private ResponseBuilder jsonFail() {
    return Response.status(Status.INTERNAL_SERVER_ERROR)
        .entity("{\"messages\":[\"Failed to encode response as JSON\"]}");
  }

  private ResponseBuilder messagesJson(ResponseBuilder response, String message) {
    return messagesJson(response, Arrays.asList(message));
  }

  private ResponseBuilder messagesJson(ResponseBuilder response, List<String> messages) {
    log.debug("Encoding messages as JSON: {}", messages);
    try {
      return response.entity(
          writeJson(
              jsonFactory
                  .objectNode()
                  .set(
                      "messages",
                      jsonFactory
                          .arrayNode()
                          .addAll(
                              messages.stream()
                                  .map(jsonFactory::textNode)
                                  .collect(Collectors.toList())))));
    } catch (JsonProcessingException e) {
      log.error("Failed to encode messages as JSON: {}", messages, e);
      return jsonFail();
    }
  }

  private String writeJson(Object o) throws JsonProcessingException {
    if (uriInfo.getQueryParameters().containsKey("pretty")) {
      return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    } else {
      return jsonMapper.writeValueAsString(o);
    }
  }

  @Value
  private static class LoginSuccessResponse {
    boolean success = true;
    UserId userId;
    Username username;
  }
}
