/**
 * 
 */
package com.eazybytes.loans.controller;

import java.util.List;

import com.eazybytes.loans.config.LoansServiceConfig;
import com.eazybytes.loans.model.Properties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eazybytes.loans.model.Customer;
import com.eazybytes.loans.model.Loans;
import com.eazybytes.loans.repository.LoansRepository;

/**
 * @author Eazy Bytes
 *
 */

@RestController
@Slf4j
public class LoansController {

	@Autowired
	private LoansRepository loansRepository;

	@Autowired
	private LoansServiceConfig loansServiceConfig;

	@PostMapping("/myLoans")
	public List<Loans> getLoansDetails(@RequestBody Customer customer) {
		log.info("myCustomerDetails() method started");
		List<Loans> loans = loansRepository.findByCustomerIdOrderByStartDtDesc(customer.getCustomerId());
		log.info("myCustomerDetails() method ended");

		if (loans != null) {
			return loans;
		} else {
			return null;
		}

	}


	@GetMapping("/loans/properties")
	public String getPropertyDetails() throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(loansServiceConfig.getMsg(), loansServiceConfig.getBuildVersion(),
				loansServiceConfig.getMailDetails(), loansServiceConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		return jsonStr;
	}

}
