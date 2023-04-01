/**
 * 
 */
package com.eazybytes.cards.controller;

import java.util.List;

import com.eazybytes.cards.config.CardsServiceConfig;
import com.eazybytes.cards.model.Properties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eazybytes.cards.model.Cards;
import com.eazybytes.cards.model.Customer;
import com.eazybytes.cards.repository.CardsRepository;

/**
 * @author Eazy Bytes
 *
 */

@RestController
@Slf4j
public class CardsController {

	@Autowired
	private CardsRepository cardsRepository;


	@Autowired
	CardsServiceConfig cardsConfig;


	@PostMapping("/myCards")
	@Timed(value = "getAccountDetails.time", description = "Time taken to return Account Details")

	public List<Cards> getCardDetails(@RequestBody Customer customer) {
		log.info("myCustomerDetails() method started");
		List<Cards> cards = cardsRepository.findByCustomerId(customer.getCustomerId());
		log.info("myCustomerDetails() method ended");

		if (cards != null) {
			return cards;
		} else {
			return null;
		}

	}


	@GetMapping("/cards/properties")
	public String getPropertyDetails() throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(cardsConfig.getMsg(), cardsConfig.getBuildVersion(),
				cardsConfig.getMailDetails(), cardsConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		return jsonStr;
	}


	@PostMapping("/createCard")
	public Cards createCardTest(@RequestBody Cards cards){
		Cards save = cardsRepository.save(cards);
		return  save;
	}


	//create get method which will return string  "Ahoj kamil"
	@GetMapping("/test")
	public String test(){
		return "Ahoj kamil";
	}




}
