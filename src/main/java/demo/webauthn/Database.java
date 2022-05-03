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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import demo.webauthn.tables.records.UsersRecord;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.mindrot.jbcrypt.BCrypt;

@Slf4j
public class Database {

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

  private static ObjectMapper json() {
    return new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
        .setBase64Variant(Base64Variants.MODIFIED_FOR_URL);
  }
}
