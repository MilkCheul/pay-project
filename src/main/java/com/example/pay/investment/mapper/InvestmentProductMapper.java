package com.example.pay.investment.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.pay.investment.model.InvestmentProduct;
import com.example.pay.investment.model.ProductInfo;
import com.example.pay.investment.model.UserInvestment;

@Repository
public interface InvestmentProductMapper {
	List<InvestmentProduct> selectAvailableInvestmentProducts();

	ProductInfo selectAvailableProductInfo(int productId);

	void insertUserInvestment(UserInvestment userInvestment);

	List<UserInvestment> selectUserInvestments(int userId);
}