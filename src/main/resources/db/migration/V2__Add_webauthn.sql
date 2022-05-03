CREATE TABLE `webauthn_credentials` (
  `user_id` CHAR(36) NOT NULL,
  `credential_id` VARBINARY(1023) NOT NULL PRIMARY KEY,
  `create_time` DATETIME NOT NULL,
  `last_use_time` DATETIME NOT NULL,
  `signature_count` INTEGER UNSIGNED DEFAULT 0,
  `transports` VARCHAR(256) DEFAULT "",
  `public_key_cose` BLOB NOT NULL,
  KEY `user_and_credid` (`user_id`, `credential_id`(1023)),
  CONSTRAINT `user_id__user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);
