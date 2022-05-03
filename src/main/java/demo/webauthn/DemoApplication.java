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
import com.yubico.util.Either;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemoApplication {

  private final Database database;
  private final SessionManager sessions = new SessionManager();

  public DemoApplication() {
    this(new Database());
  }

  public DemoApplication(Database database) {
    this.database = database;
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

  public SessionToken createSession(UserId userId) throws ExecutionException {
    return sessions.createSession(userId);
  }

  @Value
  public static class GetSessionResponse {
    boolean success = true;
    UserId userId;
    Username username;
  }

  public Optional<GetSessionResponse> getSession(SessionToken sessionToken) {
    return sessions
        .getSession(sessionToken)
        .map(userId -> new GetSessionResponse(userId, database.getUsername(userId)));
  }

  public void deleteSession(Optional<SessionToken> sessionToken) {
    sessions.endSession(sessionToken);
  }
}
