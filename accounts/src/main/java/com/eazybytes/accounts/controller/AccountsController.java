/**
 * 
 */
package com.eazybytes.accounts.controller;

import com.eazybytes.accounts.config.AccountsServiceConfig;
import com.eazybytes.accounts.model.*;
import com.eazybytes.accounts.service.client.CardsFeignClient;
import com.eazybytes.accounts.service.client.LoansFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.netflix.discovery.converters.Auto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eazybytes.accounts.repository.AccountsRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;


@RestController
@Slf4j
public class AccountsController {
	

	@Autowired
	private AccountsRepository accountsRepository;
	@Autowired
	private AccountsServiceConfig accountsConfig;
	@Autowired
	private LoansFeignClient loansFeignClient;

	@Autowired
	private CardsFeignClient cardsFeignClient;






	@PostMapping("/myAccount")
	@Timed(value = "getAccountDetails.time", description = "Time taken to return Account Details")
	public Accounts getAccountDetails(@RequestBody Customer customer) {

		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		if (accounts != null) {
			return accounts;
		} else {
			return null;
		}

	}


	@GetMapping("/account/properties")
	public String getPropertyDetails() throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(accountsConfig.getMsg(), accountsConfig.getBuildVersion(),
				accountsConfig.getMailDetails(), accountsConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		return jsonStr;
	}

	@PostMapping("/myCustomerDetails")
	@CircuitBreaker(name = "detailsforCustomerSupportApp")
	public CustomerDetails myCustomerDetails(@RequestBody Customer customer) {
		log.info("myCustomerDetails() method started");
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Loans> loans = loansFeignClient.getLoansDetails(customer);
		List<Cards> cards = cardsFeignClient.getCardDetails(customer);


		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setAccounts(accounts);
		customerDetails.setLoans(loans);
		customerDetails.setCards(cards);
		log.info("myCustomerDetails() method ended");
		return customerDetails;

	}


	@PostMapping("/addNewCustomer")
	public tempObj createNewAccountTest(@RequestBody Accounts accounts) {

		Accounts createAccount = accountsRepository.save(accounts);

		Cards newCard = new Cards();
		newCard.setCardType("DEBIT");
		newCard.setCardNumber("0000XXXX0000");
		newCard.setTotalLimit(7500);
		newCard.setAmountUsed(100);
		newCard.setAvailableAmount(40000000);
		newCard.setCreateDt(Date.valueOf("1999-03-08"));
		newCard.setCustomerId(accounts.getCustomerId());

		Cards createdCard = cardsFeignClient.createCardTest(newCard);

		tempObj customerDetails = new tempObj(createAccount,createdCard);



		return customerDetails;

	}

	//create get endpoint which will return  "Hello world " message
	@GetMapping("/hello")
	public ResponseEntity<String> hello() {
		return  new ResponseEntity<>("Hello World Ferkuuu", org.springframework.http.HttpStatus.OK);
	}

	@GetMapping("/sayHello")
	public String sayHello() {
		Optional<String> podName = Optional.ofNullable(System.getenv("HOSTNAME"));
		return "Hello, Welcome to account  Kubernetes cluster from : "+(podName.isPresent()?podName.get():"");
	}

	private CustomerDetails myCustomerDetailsFallBack(Customer customer, Throwable t) {
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Loans> loans = loansFeignClient.getLoansDetails(customer);
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setAccounts(accounts);
		customerDetails.setLoans(loans);
		return customerDetails;

	}



}

record tempObj(Accounts account,Cards card){}


