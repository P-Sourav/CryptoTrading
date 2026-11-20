package com.example.CryptoTrading.service;

import com.example.CryptoTrading.domain.VerificationType;
import com.example.CryptoTrading.entities.User;
import com.example.CryptoTrading.entities.VerificationCode;

public interface VerificationCodeService 
{
	VerificationCode sendVerificationCode(User user, VerificationType verificationType);
	
	VerificationCode getVerificationCodeById(Long id) throws Exception;
	
	VerificationCode getVerificationCodeByUser(Long userId);
	
	void deleteVerificationCodeById(VerificationCode code);
	
	

}
