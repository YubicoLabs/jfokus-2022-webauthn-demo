/*
 * This file is generated by jOOQ.
 */
package demo.webauthn.tables;


import demo.webauthn.Indexes;
import demo.webauthn.Keys;
import demo.webauthn.WebauthnDemo;
import demo.webauthn.tables.records.WebauthnCredentialsRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row7;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.UInteger;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WebauthnCredentials extends TableImpl<WebauthnCredentialsRecord> {

    private static final long serialVersionUID = -581632693;

    /**
     * The reference instance of <code>webauthn_demo.webauthn_credentials</code>
     */
    public static final WebauthnCredentials WEBAUTHN_CREDENTIALS = new WebauthnCredentials();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WebauthnCredentialsRecord> getRecordType() {
        return WebauthnCredentialsRecord.class;
    }

    /**
     * The column <code>webauthn_demo.webauthn_credentials.user_id</code>.
     */
    public final TableField<WebauthnCredentialsRecord, String> USER_ID = createField(DSL.name("user_id"), org.jooq.impl.SQLDataType.CHAR(36).nullable(false), this, "");

    /**
     * The column <code>webauthn_demo.webauthn_credentials.credential_id</code>.
     */
    public final TableField<WebauthnCredentialsRecord, byte[]> CREDENTIAL_ID = createField(DSL.name("credential_id"), org.jooq.impl.SQLDataType.VARBINARY(1023).nullable(false), this, "");

    /**
     * The column <code>webauthn_demo.webauthn_credentials.create_time</code>.
     */
    public final TableField<WebauthnCredentialsRecord, LocalDateTime> CREATE_TIME = createField(DSL.name("create_time"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>webauthn_demo.webauthn_credentials.last_use_time</code>.
     */
    public final TableField<WebauthnCredentialsRecord, LocalDateTime> LAST_USE_TIME = createField(DSL.name("last_use_time"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>webauthn_demo.webauthn_credentials.signature_count</code>.
     */
    public final TableField<WebauthnCredentialsRecord, UInteger> SIGNATURE_COUNT = createField(DSL.name("signature_count"), org.jooq.impl.SQLDataType.INTEGERUNSIGNED.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGERUNSIGNED)), this, "");

    /**
     * The column <code>webauthn_demo.webauthn_credentials.transports</code>.
     */
    public final TableField<WebauthnCredentialsRecord, String> TRANSPORTS = createField(DSL.name("transports"), org.jooq.impl.SQLDataType.VARCHAR(256).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>webauthn_demo.webauthn_credentials.public_key_cose</code>.
     */
    public final TableField<WebauthnCredentialsRecord, byte[]> PUBLIC_KEY_COSE = createField(DSL.name("public_key_cose"), org.jooq.impl.SQLDataType.BLOB.nullable(false), this, "");

    /**
     * Create a <code>webauthn_demo.webauthn_credentials</code> table reference
     */
    public WebauthnCredentials() {
        this(DSL.name("webauthn_credentials"), null);
    }

    /**
     * Create an aliased <code>webauthn_demo.webauthn_credentials</code> table reference
     */
    public WebauthnCredentials(String alias) {
        this(DSL.name(alias), WEBAUTHN_CREDENTIALS);
    }

    /**
     * Create an aliased <code>webauthn_demo.webauthn_credentials</code> table reference
     */
    public WebauthnCredentials(Name alias) {
        this(alias, WEBAUTHN_CREDENTIALS);
    }

    private WebauthnCredentials(Name alias, Table<WebauthnCredentialsRecord> aliased) {
        this(alias, aliased, null);
    }

    private WebauthnCredentials(Name alias, Table<WebauthnCredentialsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> WebauthnCredentials(Table<O> child, ForeignKey<O, WebauthnCredentialsRecord> key) {
        super(child, key, WEBAUTHN_CREDENTIALS);
    }

    @Override
    public Schema getSchema() {
        return WebauthnDemo.WEBAUTHN_DEMO;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.WEBAUTHN_CREDENTIALS_USER_AND_CREDID);
    }

    @Override
    public UniqueKey<WebauthnCredentialsRecord> getPrimaryKey() {
        return Keys.KEY_WEBAUTHN_CREDENTIALS_PRIMARY;
    }

    @Override
    public List<UniqueKey<WebauthnCredentialsRecord>> getKeys() {
        return Arrays.<UniqueKey<WebauthnCredentialsRecord>>asList(Keys.KEY_WEBAUTHN_CREDENTIALS_PRIMARY);
    }

    @Override
    public List<ForeignKey<WebauthnCredentialsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<WebauthnCredentialsRecord, ?>>asList(Keys.USER_ID__USER_ID);
    }

    public Users users() {
        return new Users(this, Keys.USER_ID__USER_ID);
    }

    @Override
    public WebauthnCredentials as(String alias) {
        return new WebauthnCredentials(DSL.name(alias), this);
    }

    @Override
    public WebauthnCredentials as(Name alias) {
        return new WebauthnCredentials(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public WebauthnCredentials rename(String name) {
        return new WebauthnCredentials(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WebauthnCredentials rename(Name name) {
        return new WebauthnCredentials(name, null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<String, byte[], LocalDateTime, LocalDateTime, UInteger, String, byte[]> fieldsRow() {
        return (Row7) super.fieldsRow();
    }
}
