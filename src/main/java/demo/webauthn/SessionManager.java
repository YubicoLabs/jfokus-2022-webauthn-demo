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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;

public class SessionManager {

  private final Cache<SessionToken, UserId> sessionIdsToUsers = newCache();
  private final Cache<UserId, SessionToken> usersToSessionIds = newCache();

  private static <K, V> Cache<K, V> newCache() {
    return CacheBuilder.newBuilder()
        .maximumSize(100)
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build();
  }

  /** @return Create a new session for the given user, or return the existing one. */
  public SessionToken createSession(@NonNull UserId userId) throws ExecutionException {
    SessionToken sessionId =
        usersToSessionIds.get(userId, () -> new SessionToken(UUID.randomUUID().toString()));
    sessionIdsToUsers.put(sessionId, userId);
    return sessionId;
  }

  /** @return the user ID of the given session, if any. */
  public Optional<UserId> getSession(@NonNull SessionToken token) {
    return Optional.ofNullable(sessionIdsToUsers.getIfPresent(token));
  }

  public boolean isSessionForUser(@NonNull UserId claimedUserId, @NonNull SessionToken token) {
    return getSession(token).map(claimedUserId::equals).orElse(false);
  }

  public boolean isSessionForUser(
      @NonNull UserId claimedUserId, @NonNull Optional<SessionToken> token) {
    return token.map(t -> isSessionForUser(claimedUserId, t)).orElse(false);
  }

  public void endSession(@NonNull Optional<SessionToken> token) {
    token.ifPresent(sessionIdsToUsers::invalidate);
  }
}
