ALTER TABLE `webauthn_credentials`
  ADD COLUMN `discoverable` BIT(1) DEFAULT 0,
  ADD COLUMN `uv_capable` BIT(1) DEFAULT 0;
