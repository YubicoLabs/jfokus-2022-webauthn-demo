/*
 * This file is generated by jOOQ.
 */
package demo.webauthn.tables.records;


import demo.webauthn.tables.WebauthnCredentials;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.UInteger;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WebauthnCredentialsRecord extends UpdatableRecordImpl<WebauthnCredentialsRecord> implements Record10<String, byte[], String, LocalDateTime, LocalDateTime, UInteger, String, byte[], Boolean, Boolean> {

    private static final long serialVersionUID = -942435800;

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
     * Setter for <code>webauthn_demo.webauthn_credentials.nickname</code>.
     */
    public void setNickname(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.nickname</code>.
     */
    public String getNickname() {
        return (String) get(2);
    }

    /**
     * Setter for <code>webauthn_demo.webauthn_credentials.create_time</code>.
     */
    public void setCreateTime(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.create_time</code>.
     */
    public LocalDateTime getCreateTime() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>webauthn_demo.webauthn_credentials.last_use_time</code>.
     */
    public void setLastUseTime(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.last_use_time</code>.
     */
    public LocalDateTime getLastUseTime() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>webauthn_demo.webauthn_credentials.signature_count</code>.
     */
    public void setSignatureCount(UInteger value) {
        set(5, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.signature_count</code>.
     */
    public UInteger getSignatureCount() {
        return (UInteger) get(5);
    }

    /**
     * Setter for <code>webauthn_demo.webauthn_credentials.transports</code>.
     */
    public void setTransports(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.transports</code>.
     */
    public String getTransports() {
        return (String) get(6);
    }

    /**
     * Setter for <code>webauthn_demo.webauthn_credentials.public_key_cose</code>.
     */
    public void setPublicKeyCose(byte[] value) {
        set(7, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.public_key_cose</code>.
     */
    public byte[] getPublicKeyCose() {
        return (byte[]) get(7);
    }

    /**
     * Setter for <code>webauthn_demo.webauthn_credentials.discoverable</code>.
     */
    public void setDiscoverable(Boolean value) {
        set(8, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.discoverable</code>.
     */
    public Boolean getDiscoverable() {
        return (Boolean) get(8);
    }

    /**
     * Setter for <code>webauthn_demo.webauthn_credentials.uv_capable</code>.
     */
    public void setUvCapable(Boolean value) {
        set(9, value);
    }

    /**
     * Getter for <code>webauthn_demo.webauthn_credentials.uv_capable</code>.
     */
    public Boolean getUvCapable() {
        return (Boolean) get(9);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<byte[]> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row10<String, byte[], String, LocalDateTime, LocalDateTime, UInteger, String, byte[], Boolean, Boolean> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    @Override
    public Row10<String, byte[], String, LocalDateTime, LocalDateTime, UInteger, String, byte[], Boolean, Boolean> valuesRow() {
        return (Row10) super.valuesRow();
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
    public Field<String> field3() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.NICKNAME;
    }

    @Override
    public Field<LocalDateTime> field4() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.CREATE_TIME;
    }

    @Override
    public Field<LocalDateTime> field5() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.LAST_USE_TIME;
    }

    @Override
    public Field<UInteger> field6() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.SIGNATURE_COUNT;
    }

    @Override
    public Field<String> field7() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.TRANSPORTS;
    }

    @Override
    public Field<byte[]> field8() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.PUBLIC_KEY_COSE;
    }

    @Override
    public Field<Boolean> field9() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.DISCOVERABLE;
    }

    @Override
    public Field<Boolean> field10() {
        return WebauthnCredentials.WEBAUTHN_CREDENTIALS.UV_CAPABLE;
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
    public String component3() {
        return getNickname();
    }

    @Override
    public LocalDateTime component4() {
        return getCreateTime();
    }

    @Override
    public LocalDateTime component5() {
        return getLastUseTime();
    }

    @Override
    public UInteger component6() {
        return getSignatureCount();
    }

    @Override
    public String component7() {
        return getTransports();
    }

    @Override
    public byte[] component8() {
        return getPublicKeyCose();
    }

    @Override
    public Boolean component9() {
        return getDiscoverable();
    }

    @Override
    public Boolean component10() {
        return getUvCapable();
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
    public String value3() {
        return getNickname();
    }

    @Override
    public LocalDateTime value4() {
        return getCreateTime();
    }

    @Override
    public LocalDateTime value5() {
        return getLastUseTime();
    }

    @Override
    public UInteger value6() {
        return getSignatureCount();
    }

    @Override
    public String value7() {
        return getTransports();
    }

    @Override
    public byte[] value8() {
        return getPublicKeyCose();
    }

    @Override
    public Boolean value9() {
        return getDiscoverable();
    }

    @Override
    public Boolean value10() {
        return getUvCapable();
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
    public WebauthnCredentialsRecord value3(String value) {
        setNickname(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord value4(LocalDateTime value) {
        setCreateTime(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord value5(LocalDateTime value) {
        setLastUseTime(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord value6(UInteger value) {
        setSignatureCount(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord value7(String value) {
        setTransports(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord value8(byte[] value) {
        setPublicKeyCose(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord value9(Boolean value) {
        setDiscoverable(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord value10(Boolean value) {
        setUvCapable(value);
        return this;
    }

    @Override
    public WebauthnCredentialsRecord values(String value1, byte[] value2, String value3, LocalDateTime value4, LocalDateTime value5, UInteger value6, String value7, byte[] value8, Boolean value9, Boolean value10) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
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
    public WebauthnCredentialsRecord(String userId, byte[] credentialId, String nickname, LocalDateTime createTime, LocalDateTime lastUseTime, UInteger signatureCount, String transports, byte[] publicKeyCose, Boolean discoverable, Boolean uvCapable) {
        super(WebauthnCredentials.WEBAUTHN_CREDENTIALS);

        set(0, userId);
        set(1, credentialId);
        set(2, nickname);
        set(3, createTime);
        set(4, lastUseTime);
        set(5, signatureCount);
        set(6, transports);
        set(7, publicKeyCose);
        set(8, discoverable);
        set(9, uvCapable);
    }
}
