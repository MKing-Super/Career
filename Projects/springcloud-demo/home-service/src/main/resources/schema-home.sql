CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `role` VARCHAR(20) DEFAULT 'ADMIN',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
);

-- Insert default user: mk/123
INSERT INTO user (username, password, role) VALUES ('mk', '123', 'ADMIN');
