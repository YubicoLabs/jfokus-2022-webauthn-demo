CREATE TABLE `users` (
  `id` CHAR(36) NOT NULL PRIMARY KEY,
  `username` VARCHAR(256) NOT NULL,
  `create_time` DATETIME NOT NULL,
  `password_bcrypt` VARCHAR(255) NOT NULL,
  UNIQUE KEY `username` (`username`),
  KEY `username_and_password` (`username`, `password_bcrypt`)
);
