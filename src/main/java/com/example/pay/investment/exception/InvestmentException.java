/**
 * Copyright 2021 Naver Corp. All rights Reserved.
 * Naver PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.example.pay.investment.exception;

import com.example.pay.investment.model.ResponseCode;

public class InvestmentException extends RuntimeException {
	private ResponseCode code;

	public InvestmentException(ResponseCode code, String message) {
		super(message);
		this.code = code;
	}

	public ResponseCode getCode() {
		return code;
	}

	public void setCode(ResponseCode code) {
		this.code = code;
	}
}