package com.example.CryptoTrading.request;

import com.example.CryptoTrading.domain.VerificationType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ForgotPasswordTokenRequest 
{
	private String sendTo;
	
	private VerificationType verificationType;
	
	

}
