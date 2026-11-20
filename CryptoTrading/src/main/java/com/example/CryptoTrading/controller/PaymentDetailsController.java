package com.example.CryptoTrading.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.CryptoTrading.entities.PaymentDetails;
import com.example.CryptoTrading.entities.User;
import com.example.CryptoTrading.service.PaymentDetailsService;
import com.example.CryptoTrading.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/payment-details")
public class PaymentDetailsController 
{
	@Autowired
	private UserService userService;
	
	@Autowired
	private PaymentDetailsService paymentDetailsService;
	
	
	@PostMapping()
	public ResponseEntity<PaymentDetails> addPaymentDetails(
								@RequestBody PaymentDetails paymentDetailsRequest,
								@RequestHeader("Authorization") String jwt) throws Exception
	{
		User user = userService.findUserByProfileByJwt(jwt);
		
		PaymentDetails paymentDetails = paymentDetailsService.addPaymentDetails(
												paymentDetailsRequest.getAccountNumber(),
												paymentDetailsRequest.getAccountHolderName(), 
												paymentDetailsRequest.getIfsc(), 
												paymentDetailsRequest.getBankName(),
												user);
		
		
		return new ResponseEntity<PaymentDetails>(paymentDetails, HttpStatus.CREATED);
				
	}
	
	
	@GetMapping()
	public ResponseEntity<PaymentDetails> getUserPaymentDetails(@RequestHeader("Authorization") String jwt) throws Exception 
	{
		User user = userService.findUserByProfileByJwt(jwt);
		
		PaymentDetails paymentDetails = paymentDetailsService.getUserPaymentDetails(user);
		
		return new ResponseEntity<PaymentDetails>(paymentDetails, HttpStatus.OK);
	}
	
	

}
