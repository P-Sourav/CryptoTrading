package com.example.CryptoTrading.entities;

import com.example.CryptoTrading.domain.VerificationType;

import lombok.Data;

@Data
public class TwoFactorAuth 
{
	private boolean isEnable = false;
	
	private VerificationType sendTo;
}
