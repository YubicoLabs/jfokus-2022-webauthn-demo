/*
 * This file is generated by jOOQ.
 */
package demo.webauthn;


import demo.webauthn.tables.Users;
import demo.webauthn.tables.WebauthnCredentials;
import demo.webauthn.tables.records.UsersRecord;
import demo.webauthn.tables.records.WebauthnCredentialsRecord;

import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>webauthn_demo</code> schema.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<UsersRecord> KEY_USERS_PRIMARY = UniqueKeys0.KEY_USERS_PRIMARY;
    public static final UniqueKey<UsersRecord> KEY_USERS_USERNAME = UniqueKeys0.KEY_USERS_USERNAME;
    public static final UniqueKey<WebauthnCredentialsRecord> KEY_WEBAUTHN_CREDENTIALS_PRIMARY = UniqueKeys0.KEY_WEBAUTHN_CREDENTIALS_PRIMARY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<WebauthnCredentialsRecord, UsersRecord> USER_ID__USER_ID = ForeignKeys0.USER_ID__USER_ID;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class UniqueKeys0 {
        public static final UniqueKey<UsersRecord> KEY_USERS_PRIMARY = Internal.createUniqueKey(Users.USERS, "KEY_users_PRIMARY", new TableField[] { Users.USERS.ID }, true);
        public static final UniqueKey<UsersRecord> KEY_USERS_USERNAME = Internal.createUniqueKey(Users.USERS, "KEY_users_username", new TableField[] { Users.USERS.USERNAME }, true);
        public static final UniqueKey<WebauthnCredentialsRecord> KEY_WEBAUTHN_CREDENTIALS_PRIMARY = Internal.createUniqueKey(WebauthnCredentials.WEBAUTHN_CREDENTIALS, "KEY_webauthn_credentials_PRIMARY", new TableField[] { WebauthnCredentials.WEBAUTHN_CREDENTIALS.CREDENTIAL_ID }, true);
    }

    private static class ForeignKeys0 {
        public static final ForeignKey<WebauthnCredentialsRecord, UsersRecord> USER_ID__USER_ID = Internal.createForeignKey(Keys.KEY_USERS_PRIMARY, WebauthnCredentials.WEBAUTHN_CREDENTIALS, "user_id__user_id", new TableField[] { WebauthnCredentials.WEBAUTHN_CREDENTIALS.USER_ID }, true);
    }
}
