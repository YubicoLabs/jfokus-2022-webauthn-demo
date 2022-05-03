ALTER TABLE `webauthn_credentials`
  ADD COLUMN `nickname` VARCHAR(256) DEFAULT NULL AFTER `credential_id`;
