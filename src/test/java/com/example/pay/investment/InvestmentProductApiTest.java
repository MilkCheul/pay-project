package com.example.pay.investment;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.pay.investment.model.UserInvestment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class InvestmentProductApiTest {
	private MockMvc mockMvc;

	@Autowired
	void setMockMvc(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	@Test
	void testProducts() throws Exception {
		ResultActions result = mockMvc.perform(
			get("/investment/products")
				.accept(MediaType.APPLICATION_JSON)
		);

		result.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	void testUserInvestments() throws Exception {
		ResultActions result = mockMvc.perform(
			get("/user/investments")
				.header("X-USER-ID", 0)
				.accept(MediaType.APPLICATION_JSON)
		);

		result.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code", is("SUCCESS")))
			.andExpect(jsonPath("$.data").isArray());
	}

	@Test
	void testUserInvestments_userId_empty_case() throws Exception {
		ResultActions result = mockMvc.perform(
			get("/user/investments")
				.accept(MediaType.APPLICATION_JSON)
		);

		result.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code", is("FAIL")))
			.andExpect(jsonPath("$.message", is("X-USER-ID header is empty or invalid number type. check request header field.")));
	}

	@Test
	void testInvestProduct_product_id_empty_case() throws Exception {
		UserInvestment userInvestment = new UserInvestment();
		userInvestment.setInvestingAmount(1);

		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = writer.writeValueAsString(userInvestment);

		ResultActions result = mockMvc.perform(
			post("/user/investment")
				.header("X-USER-ID", 0)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
				.accept(MediaType.APPLICATION_JSON)
		);

		result.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code", is("FAIL")))
			.andExpect(jsonPath("$.message", is("productId can not be null")));
	}



	@Test
	void testInvestProduct_investing_amount_empty_case() throws Exception {
		UserInvestment userInvestment = new UserInvestment();
		userInvestment.setProductId(0);

		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = writer.writeValueAsString(userInvestment);

		ResultActions result = mockMvc.perform(
			post("/user/investment")
				.header("X-USER-ID", 0)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
				.accept(MediaType.APPLICATION_JSON)
		);

		result.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code", is("FAIL")))
			.andExpect(jsonPath("$.message", is("investing amount can not be null")));
	}

	@Test
	void testInvestProduct_investing_amount_less_than_1_case() throws Exception {
		UserInvestment userInvestment = new UserInvestment();
		userInvestment.setProductId(0);
		userInvestment.setInvestingAmount(0);

		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = writer.writeValueAsString(userInvestment);

		ResultActions result = mockMvc.perform(
			post("/user/investment")
				.header("X-USER-ID", 0)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
				.accept(MediaType.APPLICATION_JSON)
		);

		result.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code", is("FAIL")))
			.andExpect(jsonPath("$.message", is("investing amount must be at least greater than 0.")));
	}

	@Test
	void testInvestProduct_userId_empty_case() throws Exception {
		ResultActions result = mockMvc.perform(
			post("/user/investment")
				.accept(MediaType.APPLICATION_JSON)
		);

		result.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code", is("FAIL")))
			.andExpect(jsonPath("$.message", is("X-USER-ID header is empty or invalid number type. check request header field.")));
	}
}