package com.example.CryptoTrading.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResetPasswordRequest
{
	private String otp;
	
	private String password;
}
