ALTER TABLE `webauthn_credentials`
  ADD COLUMN `attestation_object` BLOB DEFAULT NULL,
  ADD COLUMN `create_clientDataJSON` BLOB DEFAULT NULL;
