package com.example.pay.investment.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.pay.investment.mapper.InvestmentProductMapper;
import com.example.pay.investment.model.ProductInfo;
import com.example.pay.investment.model.UserInvestment;
import com.example.pay.investment.repo.ProductInfoRedisRepository;

@SpringBootTest
class InvestmentProductServiceConcurrentTest {
	@Autowired
	private ProductInfoRedisRepository productInfoRedisRepository;

	@Autowired
	private RedissonClient redissonClient;

	@Mock
	private InvestmentProductMapper investmentProductMapper;

	private InvestmentProductService investmentProductService;

	@BeforeEach
	void setUp() {
		investmentProductService = new InvestmentProductService(investmentProductMapper, redissonClient, productInfoRedisRepository);
	}

	@Test
	void testInvestProductConcurrent() throws InterruptedException {
		ExecutorService service = Executors.newFixedThreadPool(10);

		ProductInfo testInfo = new ProductInfo();
		testInfo.setProductId(10000);
		testInfo.setTotalInvestingAmount(1000);
		testInfo.setRestInvestingAmount(1000);
		testInfo.setFinishedAt(LocalDateTime.now().plusDays(1L));
		productInfoRedisRepository.save(testInfo);

		UserInvestment testUser = new UserInvestment();
		testUser.setUserId(0);
		testUser.setProductId(10000);
		testUser.setInvestingAmount(1);
		testUser.setInvestedAt(LocalDateTime.now());

		Mockito.doNothing().when(investmentProductMapper).insertUserInvestment(Mockito.any());

		for (int i = 0; i < 10; i++) {
			service.execute(() -> {
				investmentProductService.investProduct(testUser);
				investmentProductService.investProduct(testUser);
				investmentProductService.investProduct(testUser);
				investmentProductService.investProduct(testUser);
				investmentProductService.investProduct(testUser);
			});
		}

		service.shutdown();
		service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

		ProductInfo actual = productInfoRedisRepository.findById(10000).get();
		productInfoRedisRepository.deleteById(10000);

		assertEquals(950, actual.getRestInvestingAmount());
	}
}