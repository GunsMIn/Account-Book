
/*user(회원) 테이블*/
CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  `registeredAt` datetime(6) DEFAULT NULL,
  `updatedAt` datetime(6) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
   PRIMARY KEY (`user_id`)
)

/*account_book(가계부) 테이블*/
CREATE TABLE `account_book` (
  `account_book_id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `balance` int NOT NULL,
  `memo` varchar(255) NOT NULL,
  `registeredAt` datetime(6) DEFAULT NULL,
  `updatedAt` datetime(6) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `user_id` bigint ,
   PRIMARY KEY (`account_book_id`),
   CONSTRAINT `accountBook_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
)

/*record(가계부 기록) 테이블*/
CREATE TABLE `record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT,
  `memo` varchar(255) NOT NULL,
  `money` int NOT NULL,
  `act` varchar(255) NOT NULL,
  `day` varchar(255) NOT NULL,
  `expendType` varchar(255) NOT NULL,
  `registeredAt` datetime(6) DEFAULT NULL,
  `updatedAt` datetime(6) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `account_book_id` bigint,
  `user_id` bigint,
  PRIMARY KEY (`record_id`),
  CONSTRAINT `user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `account_book_fk` FOREIGN KEY (`account_book_id`) REFERENCES `account_book` (`account_book_id`)
)



