-- CREATE TABLE `users` (
--     `id` INT NOT NULL AUTO_INCREMENT,
--     `username` VARCHAR(45) NOT NULL,
--     `password` VARCHAR(45) NOT NULL,
--     `enabled` INT NOT NULL,
--     PRIMARY KEY (`id`)
-- );
--
-- CREATE TABLE `authorities` (
--     `id` INT NOT NULL AUTO_INCREMENT,
--     `username` VARCHAR(45) NOT NULL,
--     `authority` VARCHAR(45) NOT NULL,
--     PRIMARY KEY (`id`)
-- );
--
-- INSERT INTO `users` values (default, 'happy', '12345', '1');
-- INSERT INTO `authorities` values (default, 'happy', 'write');

CREATE TABLE `customer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(45) NOT NULL,
    `password` VARCHAR(200) NOT NULL,
    `role` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`id`)
);
