package com.example.pay.investment.model;

import java.time.LocalDateTime;

public class InvestmentProduct {
	private int productId;
	private String title;
	private int totalInvestingAmount;
	private int presentInvestingAmount;
	private int investorCount;
	private InvestmentState investmentState;
	private LocalDateTime startedAt;
	private LocalDateTime finishedAt;

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getTotalInvestingAmount() {
		return totalInvestingAmount;
	}

	public void setTotalInvestingAmount(int totalInvestingAmount) {
		this.totalInvestingAmount = totalInvestingAmount;
	}

	public int getPresentInvestingAmount() {
		return presentInvestingAmount;
	}

	public void setPresentInvestingAmount(int presentInvestingAmount) {
		this.presentInvestingAmount = presentInvestingAmount;
	}

	public int getInvestorCount() {
		return investorCount;
	}

	public void setInvestorCount(int investorCount) {
		this.investorCount = investorCount;
	}

	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}

	public LocalDateTime getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(LocalDateTime finishedAt) {
		this.finishedAt = finishedAt;
	}

	public InvestmentState getInvestmentState() {
		return investmentState;
	}

	public void setInvestmentState(InvestmentState investmentState) {
		this.investmentState = investmentState;
	}
}