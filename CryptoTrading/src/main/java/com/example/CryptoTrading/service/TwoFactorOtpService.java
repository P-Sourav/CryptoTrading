package com.example.CryptoTrading.service;

import com.example.CryptoTrading.entities.TwoFactorOTP;
import com.example.CryptoTrading.entities.User;

public interface TwoFactorOtpService 
{
	TwoFactorOTP createTwoFactorOtp(User user, String otp, String jwt);
	
	TwoFactorOTP FindByUser(Long userId);
	
	TwoFactorOTP FindById(String id);
	
	boolean verifyTwoFactorOtp(TwoFactorOTP factorOTP, String otp);
	
	void deleteTwoFactorOtp(TwoFactorOTP factorOTP);
	

}
