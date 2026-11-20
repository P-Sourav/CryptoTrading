package com.example.CryptoTrading.service;

import com.example.CryptoTrading.domain.VerificationType;
import com.example.CryptoTrading.entities.ForgotPasswordToken;
import com.example.CryptoTrading.entities.User;

public interface ForgotPasswordService
{
	ForgotPasswordToken createToken(User user, String id, String otp, 
									VerificationType verificationType , String sendTo);
	
	ForgotPasswordToken findById(String id);
	
	ForgotPasswordToken findByUser(Long userId);
	
	void deleteToken(ForgotPasswordToken token);
	
	
	
	
}
