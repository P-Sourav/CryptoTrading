package com.example.CryptoTrading.utils;

import java.security.SecureRandom;

public class OtpUtils 
{
	public static String generateOTP()
	{
		int otpLength = 6;
		
		SecureRandom random = new SecureRandom();
		
		StringBuilder otp = new StringBuilder(otpLength);
	
		for (int i = 0; i < otpLength; i++)   // was otp.length() (= 0), which produced an empty OTP 
		{
			otp.append(random.nextInt(10));
		}
		
		return otp.toString();
	}

}
