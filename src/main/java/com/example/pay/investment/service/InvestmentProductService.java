package com.example.pay.investment.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.example.pay.investment.exception.InvestmentException;
import com.example.pay.investment.mapper.InvestmentProductMapper;
import com.example.pay.investment.model.ResponseCode;
import com.example.pay.investment.model.InvestmentProduct;
import com.example.pay.investment.model.ProductInfo;
import com.example.pay.investment.model.UserInvestment;
import com.example.pay.investment.repo.ProductInfoRedisRepository;

@Service
public class InvestmentProductService {
	private static final String LOCK_PREFIX = "LOCK:";

	private final InvestmentProductMapper investmentProductMapper;

	private final RedissonClient redissonClient;

	private final ProductInfoRedisRepository productInfoRedisRepository;

	public InvestmentProductService(InvestmentProductMapper investmentProductMapper, RedissonClient redissonClient, ProductInfoRedisRepository productInfoRedisRepository) {
		this.investmentProductMapper = investmentProductMapper;
		this.redissonClient = redissonClient;
		this.productInfoRedisRepository = productInfoRedisRepository;
	}

	public List<InvestmentProduct> getInvestmentProducts() {
		return investmentProductMapper.selectAvailableInvestmentProducts();
	}

	public void investProduct(UserInvestment userInvestment) {
		RLock rLock = redissonClient.getLock(LOCK_PREFIX + userInvestment.getProductId());

		try {
			rLock.lock(2, TimeUnit.SECONDS);

			invest(userInvestment);
		} finally {
			rLock.unlock();
		}
	}

	public void invest(UserInvestment userInvestment) {
		ProductInfo productInfo = productInfoRedisRepository.findById(userInvestment.getProductId())
			.orElseGet(() -> getAvailableProductInfo(userInvestment.getProductId()));

		if (LocalDateTime.now().isAfter(productInfo.getFinishedAt())) {
			productInfoRedisRepository.deleteById(userInvestment.getProductId());
			throw new InvestmentException(ResponseCode.INVESTMENT_END, "investment product is already closed.");
		}

		if (productInfo.getRestInvestingAmount() == 0) {
			throw new InvestmentException(ResponseCode.SOLD_OUT, "already sold-out");
		} else if (productInfo.getRestInvestingAmount() < userInvestment.getInvestingAmount()) {
			throw new InvestmentException(ResponseCode.EXCEED_INVESTMENT_AMOUNT, "You have exceeded the amount that can be invested.");
		}

		investmentProductMapper.insertUserInvestment(userInvestment);
		productInfo.setRestInvestingAmount(productInfo.getRestInvestingAmount() - userInvestment.getInvestingAmount());
		productInfoRedisRepository.save(productInfo);
	}

	private ProductInfo getAvailableProductInfo(int productId) {
		return Optional.ofNullable(investmentProductMapper.selectAvailableProductInfo(productId))
			.orElseThrow(() -> new InvestmentException(ResponseCode.INVESTMENT_END, "investment product is already closed."));
	}

	public List<UserInvestment> getUserInvestments(int userId) {
		return investmentProductMapper.selectUserInvestments(userId);
	}
}