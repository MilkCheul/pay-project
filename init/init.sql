SET NAMES utf8;
SET character_set_client = utf8mb4;

CREATE TABLE `investment_product` (
  `product_id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL,
  `total_investing_amount` INT NOT NULL,
  `started_at` DATETIME NOT NULL,
  `finished_at` DATETIME NOT NULL,
  PRIMARY KEY (`product_id`),
  KEY `IDX_STARTED_FINISHED_AT` (`started_at`,`finished_at`)
 ) ENGINE=InnoDB;


CREATE TABLE `user_investment` (
  `investor_seq` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `product_id` int NOT NULL,
  `investing_amount` int NOT NULL,
  `invested_at` DATETIME NOT NULL,
  PRIMARY KEY (`investor_seq`),
  KEY `IDX_PRODUCT_ID` (`product_id`),
  KEY `IDX_USER_ID` (`user_id`)
) ENGINE=InnoDB;

INSERT INTO investment_product (title, total_investing_amount, started_at, finished_at)
VALUES('개인신용 포트폴리오', 1000000, STR_TO_DATE('2021-06-26 00:00:00', '%Y-%m-%d %T'), STR_TO_DATE('2021-06-26 23:59:59', '%Y-%m-%d %T'));


INSERT INTO investment_product (title, total_investing_amount, started_at, finished_at)
VALUES('부동산 포트폴리오', 5000000, STR_TO_DATE('2021-06-02 00:00:00', '%Y-%m-%d %T'), STR_TO_DATE('2021-06-30 00:00:00', '%Y-%m-%d %T'));

INSERT INTO investment_product (title, total_investing_amount, started_at, finished_at)
VALUES('테스트 포트폴리오', 5000000, STR_TO_DATE('2021-06-20 00:00:00', '%Y-%m-%d %T'), STR_TO_DATE('2021-07-23 19:50:00', '%Y-%m-%d %T'));