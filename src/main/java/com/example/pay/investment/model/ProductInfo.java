package com.example.pay.investment.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("product")
public class ProductInfo {
	@Id
	private int productId;
	private int totalInvestingAmount;
	private int restInvestingAmount;
	private LocalDateTime finishedAt;

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public LocalDateTime getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(LocalDateTime finishedAt) {
		this.finishedAt = finishedAt;
	}

	public int getTotalInvestingAmount() {
		return totalInvestingAmount;
	}

	public void setTotalInvestingAmount(int totalInvestingAmount) {
		this.totalInvestingAmount = totalInvestingAmount;
	}

	public int getRestInvestingAmount() {
		return restInvestingAmount;
	}

	public void setRestInvestingAmount(int restInvestingAmount) {
		this.restInvestingAmount = restInvestingAmount;
	}
}