### 환경

- spring boot(standalone)
- spring data redis
- redisson(분산 lock처리)
- docker compose(mysql, redis)
- mybatis
- junit5



### 구동

- 프로젝트 root에서 `docker-compose up -d` 커맨드 입력 후 spring application실행



### DB스키마 ({project_root}/init/init.sql)

```
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
```



### 전략

- 투자상품 조회 API는 DB에 바로 접근하여 가져옵니다.
- 나의 투자상품 조회API는 DB에 바로 접근하여 가져옵니다.
- 투자하기 API를 호출시에는 상품정보(상품ID, 총 모집금액, 남은 모집금액, 모집종료시간)을 redis에서 조회한뒤 없으면 DB에서 조회하고 다시 redis에 적재합니다. 이후 같은 상품에 접근하게 되면 redis에 접근하여 정보를 가져온 뒤 검증로직을 거친 후 투자정보를 DB에 insert합니다.
- 투자시에 redis에서 값을 읽고 검증을 한 뒤 다시 redis에 데이터를 쓰게 될 수 있는데 이때 동시성 문제를 해결하기 위해 redissonClient의 분산 lock을 사용했습니다.
- [품절], [기간이 지난 상품에 대한 투자], [가능한 투자금액보다 큰 액수의 투자], [Request header empty(userId)], [각종 파라미터 검증], [이외의 예외] 는 모두 Exception Handler를 사용하여 예외처리 되어 코드와 메시지를 노출하도록 설계했습니다.
- Mysql의 isolation level은 REPEATABLE-READ입니다.