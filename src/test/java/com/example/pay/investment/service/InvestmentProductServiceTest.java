package com.example.pay.investment.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.pay.investment.exception.InvestmentException;
import com.example.pay.investment.mapper.InvestmentProductMapper;
import com.example.pay.investment.model.InvestmentProduct;
import com.example.pay.investment.model.ProductInfo;
import com.example.pay.investment.model.ResponseCode;
import com.example.pay.investment.model.UserInvestment;
import com.example.pay.investment.repo.ProductInfoRedisRepository;

@ExtendWith(MockitoExtension.class)
class InvestmentProductServiceTest {
	@Mock
	private InvestmentProductMapper investmentProductMapper;

	@Mock
	private ProductInfoRedisRepository productInfoRedisRepository;

	@InjectMocks
	private InvestmentProductService investmentProductService;

	@Test
	void testGetInvestmentProducts() {
		List<InvestmentProduct> expected = new ArrayList<>();

		Mockito.when(investmentProductMapper.selectAvailableInvestmentProducts()).thenReturn(expected);

		List<InvestmentProduct> actual = investmentProductService.getInvestmentProducts();

		assertEquals(expected, actual);
	}

	@Test
	void testGetUserInvestments() {
		List<UserInvestment> expected = new ArrayList<>();

		Mockito.when(investmentProductMapper.selectUserInvestments(Mockito.anyInt())).thenReturn(expected);

		List<UserInvestment> actual = investmentProductService.getUserInvestments(1);

		assertEquals(expected, actual);
	}

	@Test
	void testInvest_레디스_데이터_O_정상케이스() {
		UserInvestment userInvestment = new UserInvestment();
		userInvestment.setUserId(1);
		userInvestment.setProductId(1);
		userInvestment.setInvestingAmount(1);

		ProductInfo productInfo = new ProductInfo();
		productInfo.setProductId(1);
		productInfo.setTotalInvestingAmount(1000000);
		productInfo.setRestInvestingAmount(10000);
		productInfo.setFinishedAt(LocalDateTime.now().plusDays(1L));

		Mockito.when(productInfoRedisRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(productInfo));

		investmentProductService.invest(userInvestment);

		Mockito.verify(investmentProductMapper).insertUserInvestment(Mockito.any());
		Mockito.verify(productInfoRedisRepository).save(Mockito.any());
	}

	@Test
	void testInvest_레디스_데이터_X_정상케이스() {
		UserInvestment userInvestment = new UserInvestment();
		userInvestment.setUserId(1);
		userInvestment.setProductId(1);
		userInvestment.setInvestingAmount(1);

		ProductInfo productInfo = new ProductInfo();
		productInfo.setProductId(1);
		productInfo.setTotalInvestingAmount(1000000);
		productInfo.setRestInvestingAmount(10000);
		productInfo.setFinishedAt(LocalDateTime.now().plusDays(1L));

		Mockito.when(productInfoRedisRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());
		Mockito.when(investmentProductMapper.selectAvailableProductInfo(Mockito.anyInt())).thenReturn(productInfo);

		investmentProductService.invest(userInvestment);

		Mockito.verify(investmentProductMapper).insertUserInvestment(Mockito.any());
		Mockito.verify(productInfoRedisRepository).save(Mockito.any());
	}

	@Test
	void testInvest_레디스_데이터_X_이미_만료된_상품_투자_케이스() {
		InvestmentException exception = assertThrows(InvestmentException.class, () -> {
			UserInvestment userInvestment = new UserInvestment();
			userInvestment.setUserId(1);
			userInvestment.setProductId(1);
			userInvestment.setInvestingAmount(1);

			ProductInfo productInfo = new ProductInfo();
			productInfo.setProductId(1);
			productInfo.setTotalInvestingAmount(1000000);
			productInfo.setRestInvestingAmount(10000);
			productInfo.setFinishedAt(LocalDateTime.now().plusDays(1L));

			Mockito.when(productInfoRedisRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());
			Mockito.when(investmentProductMapper.selectAvailableProductInfo(Mockito.anyInt())).thenReturn(null);

			investmentProductService.invest(userInvestment);
		});

		assertEquals(exception.getCode(), ResponseCode.INVESTMENT_END);
	}

	@Test
	void testInvest_레디스_데이터_O_이미_만료된_상품_투자_케이스() {
		InvestmentException exception = assertThrows(InvestmentException.class, () -> {
			UserInvestment userInvestment = new UserInvestment();
			userInvestment.setUserId(1);
			userInvestment.setProductId(1);
			userInvestment.setInvestingAmount(1);

			ProductInfo productInfo = new ProductInfo();
			productInfo.setProductId(1);
			productInfo.setTotalInvestingAmount(1000000);
			productInfo.setRestInvestingAmount(10000);
			productInfo.setFinishedAt(LocalDateTime.now().minusDays(1L));

			Mockito.when(productInfoRedisRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(productInfo));

			investmentProductService.invest(userInvestment);
		});

		assertEquals(exception.getCode(), ResponseCode.INVESTMENT_END);
	}

	@Test
	void testInvest_레디스_데이터_O_SOLD_OUT_케이스() {
		InvestmentException exception = assertThrows(InvestmentException.class, () -> {
			UserInvestment userInvestment = new UserInvestment();
			userInvestment.setUserId(1);
			userInvestment.setProductId(1);
			userInvestment.setInvestingAmount(1);

			ProductInfo productInfo = new ProductInfo();
			productInfo.setProductId(1);
			productInfo.setTotalInvestingAmount(1000000);
			productInfo.setRestInvestingAmount(0);
			productInfo.setFinishedAt(LocalDateTime.now().plusDays(1L));

			Mockito.when(productInfoRedisRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(productInfo));

			investmentProductService.invest(userInvestment);
		});

		assertEquals(exception.getCode(), ResponseCode.SOLD_OUT);
	}

	@Test
	void testInvest_레디스_데이터_O_투자하려는_금액이_더_큰_케이스() {
		InvestmentException exception = assertThrows(InvestmentException.class, () -> {
			UserInvestment userInvestment = new UserInvestment();
			userInvestment.setUserId(1);
			userInvestment.setProductId(1);
			userInvestment.setInvestingAmount(100);

			ProductInfo productInfo = new ProductInfo();
			productInfo.setProductId(1);
			productInfo.setTotalInvestingAmount(1000000);
			productInfo.setRestInvestingAmount(10);
			productInfo.setFinishedAt(LocalDateTime.now().plusDays(1L));

			Mockito.when(productInfoRedisRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(productInfo));

			investmentProductService.invest(userInvestment);
		});

		assertEquals(exception.getCode(), ResponseCode.EXCEED_INVESTMENT_AMOUNT);
	}
}