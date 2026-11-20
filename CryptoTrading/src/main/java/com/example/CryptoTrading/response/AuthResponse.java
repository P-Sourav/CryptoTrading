package com.example.CryptoTrading.response;

import com.example.CryptoTrading.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse
{
	private String jwt;
	private boolean status;
	private String message;
	private boolean isTwoFactorAuthEnabled;
	private String session;

}
