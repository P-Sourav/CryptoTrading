package com.example.CryptoTrading.service;

import com.example.CryptoTrading.entities.PaymentDetails;
import com.example.CryptoTrading.entities.User;

public interface PaymentDetailsService 
{

	PaymentDetails addPaymentDetails(String accountNumber,
										String accountHolderName,
										String ifsc,
										String bankName,
										User user);
	
	
	public PaymentDetails getUserPaymentDetails(User user);
										
}
