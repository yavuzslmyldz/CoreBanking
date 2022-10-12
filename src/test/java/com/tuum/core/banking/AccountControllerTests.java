package com.tuum.core.banking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuum.core.banking.entity.Account;
import com.tuum.core.banking.service.AccountService;
import com.tuum.core.banking.serviceparam.CreateAccountInput;
import com.tuum.core.banking.serviceparam.CreateAccountOutput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTests {

	@Autowired
	private AccountService accountService;

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void should_return_not_found_when_invalid_accountId() throws Exception {
		mvc.perform(get("/api/v1/account").queryParam("accountId", "0")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());


	}

	@Test
	public void should_return_bad_request_when_invalid_account_or_currency() throws Exception {

		CreateAccountInput payload = new CreateAccountInput();
		payload.setAccount(new Account(0, "Turkey"));
		payload.setCurrencies(Arrays.asList("USDT", "EUARA"));

		mvc.perform(post("/api/v1/account")
						.content(objectMapper.writeValueAsString(payload))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());


	}

	@Test
	public void should_create_account_message_when_supply_valid_input() throws Exception {
		CreateAccountInput payload = new CreateAccountInput();
		payload.setAccount(new Account(100000, "Turkey"));
		payload.setCurrencies(Arrays.asList("USD","EUR"));

	/*	mvc.perform(post("/api/v1/account")
						.content(objectMapper.writeValueAsString(payload))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()); */
	}

	/*@Test
	public void should_retrieve_account_message_when_valid() throws Exception {

		CreateAccountInput payload = new CreateAccountInput();
		payload.setAccount(new Account(100000, "Turkey"));
		payload.setCurrencies(Arrays.asList("USD","EUR"));

		 List<String> violations = accountService.validateAccount(payload);

		assertTrue(violations.isEmpty(),"Invalid Payload");

		CreateAccountOutput unAcked = accountService.createAccountMessage(payload);

		accountService.accountConsumer(unAcked);

	}
*/
}
