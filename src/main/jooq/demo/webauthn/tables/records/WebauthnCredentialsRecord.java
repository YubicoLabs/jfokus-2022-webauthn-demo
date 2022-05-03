/*
 * This file is generated by jOOQ.
 */
package demo.webauthn.tables.records;


import demo.webauthn.tables.WebauthnCredentials;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.UInteger;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WebauthnCredentialsRecord extends UpdatableRecordImpl<WebauthnCredentialsRecord> implements Record7<String, byte[], LocalDateTime, LocalDateTime, UInteger, String, byte[]> {

    private static final long serialVersionUID = -1131354960;

    /**
     * Setter for <code>webauthn_demo.webauthn_credentials.user_id</code>.
     */
    public void setUserId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.user_id</code>.
     */
    public String getUserId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>webauthn_demo.webauthn_credentials.credential_id</code>.
     */
    public void setCredentialId(byte[] value) {
        set(1, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.credential_id</code>.
     */
    public byte[] getCredentialId() {
        return (byte[]) get(1);
    }

    /**
     * Setter for <code>webauthn_demo.webauthn_credentials.create_time</code>.
     */
    public void setCreateTime(LocalDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.create_time</code>.
     */
    public LocalDateTime getCreateTime() {
        return (LocalDateTime) get(2);
    }

    /**
     * Setter for <code>webauthn_demo.webauthn_credentials.last_use_time</code>.
     */
    public void setLastUseTime(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.last_use_time</code>.
     */
    public LocalDateTime getLastUseTime() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>webauthn_demo.webauthn_credentials.signature_count</code>.
     */
    public void setSignatureCount(UInteger value) {
        set(4, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.signature_count</code>.
     */
    public UInteger getSignatureCount() {
        return (UInteger) get(4);
    }

    /**
     * Setter for <code>webauthn_demo.webauthn_credentials.transports</code>.
     */
    public void setTransports(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.transports</code>.
     */
    public String getTransports() {
        return (String) get(5);
    }

    /**
     * Setter for <code>webauthn_demo.webauthn_credentials.public_key_cose</code>.
     */
    public void setPublicKeyCose(byte[] value) {
        set(6, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.public_key_cose</code>.
     */
    public byte[] getPublicKeyCose() {
        return (byte[]) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<byte[]> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<String, byte[], LocalDateTime, LocalDateTime, UInteger, String, byte[]> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<String, byte[], LocalDateTime, LocalDateTime, UInteger, String, byte[]> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.USER_ID;
    }

    @Override
    public Field<byte[]> field2() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.CREDENTIAL_ID;
    }

    @Override
    public Field<LocalDateTime> field3() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.CREATE_TIME;
    }

    @Override
    public Field<LocalDateTime> field4() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.LAST_USE_TIME;
    }

    @Override
    public Field<UInteger> field5() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.SIGNATURE_COUNT;
    }

    @Override
    public Field<String> field6() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.TRANSPORTS;
    }

    @Override
    public Field<byte[]> field7() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.PUBLIC_KEY_COSE;
    }

    @Override
    public String component1() {
        return getUserId();
    }

    @Override
    public byte[] component2() {
        return getCredentialId();
    }

    @Override
    public LocalDateTime component3() {
        return getCreateTime();
    }

    @Override
    public LocalDateTime component4() {
        return getLastUseTime();
    }

    @Override
    public UInteger component5() {
        return getSignatureCount();
    }

    @Override
    public String component6() {
        return getTransports();
    }

    @Override
    public byte[] component7() {
        return getPublicKeyCose();
    }

    @Override
    public String value1() {
        return getUserId();
    }

    @Override
    public byte[] value2() {
        return getCredentialId();
    }

    @Override
    public LocalDateTime value3() {
        return getCreateTime();
    }

    @Override
    public LocalDateTime value4() {
        return getLastUseTime();
    }

    @Override
    public UInteger value5() {
        return getSignatureCount();
    }

    @Override
    public String value6() {
        return getTransports();
    }

    @Override
    public byte[] value7() {
        return getPublicKeyCose();
    }

    @Override
    public WebauthnCredentialsRecord value1(String value) {
        setUserId(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord value2(byte[] value) {
        setCredentialId(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord value3(LocalDateTime value) {
        setCreateTime(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord value4(LocalDateTime value) {
        setLastUseTime(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord value5(UInteger value) {
        setSignatureCount(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord value6(String value) {
        setTransports(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord value7(byte[] value) {
        setPublicKeyCose(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord values(String value1, byte[] value2, LocalDateTime value3, LocalDateTime value4, UInteger value5, String value6, byte[] value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached WebauthnCredentialsRecord
     */
    public WebauthnCredentialsRecord() {
        super(WebauthnCredentials.WEBAUTHN_CREDENTIALS);
    }

    /**
     * Create a detached, initialised WebauthnCredentialsRecord
     */
    public WebauthnCredentialsRecord(String userId, byte[] credentialId, LocalDateTime createTime, LocalDateTime lastUseTime, UInteger signatureCount, String transports, byte[] publicKeyCose) {
        super(WebauthnCredentials.WEBAUTHN_CREDENTIALS);

        set(0, userId);
        set(1, credentialId);
        set(2, createTime);
        set(3, lastUseTime);
        set(4, signatureCount);
        set(5, transports);
        set(6, publicKeyCose);
    }
}
