package com.example.pay.investment.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.pay.investment.exception.InvestmentException;
import com.example.pay.investment.model.ApiResult;
import com.example.pay.investment.model.ResponseCode;
import com.example.pay.investment.model.UserInvestment;
import com.example.pay.investment.service.InvestmentProductService;

@RestController
public class InvestmentProductController {
	private static final Logger LOGGER = LoggerFactory.getLogger(InvestmentProductController.class);
	private static final int FIRST = 0;

	private final InvestmentProductService investmentProductService;

	public InvestmentProductController(InvestmentProductService investmentProductService) {
		this.investmentProductService = investmentProductService;
	}

	@GetMapping("/investment/products")
	public ApiResult products() {
		ApiResult apiResult = new ApiResult();

		apiResult.setCode(ResponseCode.SUCCESS.name());
		apiResult.setData(investmentProductService.getInvestmentProducts());

		return apiResult;
	}

	@GetMapping("/user/investments")
	public ApiResult userInvestments(@RequestHeader(name = "X-USER-ID") int userId) {
		ApiResult apiResult = new ApiResult();

		apiResult.setCode(ResponseCode.SUCCESS.name());
		apiResult.setData(investmentProductService.getUserInvestments(userId));

		return apiResult;
	}

	@PostMapping("/user/investment")
	public ApiResult investProduct(@RequestHeader(name = "X-USER-ID") int userId, @Valid @RequestBody UserInvestment userInvestment) {
		userInvestment.setUserId(userId);
		investmentProductService.investProduct(userInvestment);

		ApiResult apiResult = new ApiResult();
		apiResult.setCode(ResponseCode.SUCCESS.name());

		return apiResult;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({MissingRequestHeaderException.class, MethodArgumentTypeMismatchException.class})
	public ApiResult handlingAboutUserIdException(Exception exception) {
		LOGGER.error(exception.getMessage(), exception);

		ApiResult apiResult = new ApiResult();

		apiResult.setCode(ResponseCode.FAIL.name());
		apiResult.setMessage("X-USER-ID header is empty or invalid number type. check request header field.");

		return apiResult;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ApiResult handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
		LOGGER.error(exception.getMessage(), exception);

		BindingResult bindingResult = exception.getBindingResult();

		ApiResult apiResult = new ApiResult();
		apiResult.setCode(ResponseCode.FAIL.name());

		if (bindingResult.hasErrors()) {
			apiResult.setMessage(exception.getBindingResult().getAllErrors().get(FIRST).getDefaultMessage());
		} else {
			apiResult.setMessage("argument is not valid");
		}

		return apiResult;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(InvestmentException.class)
	public ApiResult handlingInvestmentException(InvestmentException exception) {
		LOGGER.error(exception.getMessage(), exception);

		ApiResult apiResult = new ApiResult();

		apiResult.setCode(exception.getCode().name());
		apiResult.setMessage(exception.getMessage());

		return apiResult;
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ApiResult handlingException(Exception exception) {
		LOGGER.error(exception.getMessage(), exception);

		ApiResult apiResult = new ApiResult();

		apiResult.setCode(ResponseCode.FAIL.name());
		apiResult.setMessage("internal server error.");

		return apiResult;
	}
}