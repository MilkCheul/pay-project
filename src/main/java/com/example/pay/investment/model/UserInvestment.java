package com.example.pay.investment.model;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserInvestment {
	@JsonIgnore
	private int userId;
	@NotNull(message = "productId can not be null")
	private Integer productId;
	@NotNull(message = "investing amount can not be null")
	@Min(value = 1, message = "investing amount must be at least greater than 0.")
	private Integer investingAmount;
	private int totalInvestingAmount;
	private LocalDateTime investedAt;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getInvestingAmount() {
		return investingAmount;
	}

	public void setInvestingAmount(Integer investingAmount) {
		this.investingAmount = investingAmount;
	}

	public int getTotalInvestingAmount() {
		return totalInvestingAmount;
	}

	public void setTotalInvestingAmount(int totalInvestingAmount) {
		this.totalInvestingAmount = totalInvestingAmount;
	}

	public LocalDateTime getInvestedAt() {
		return investedAt;
	}

	public void setInvestedAt(LocalDateTime investedAt) {
		this.investedAt = investedAt;
	}
}