package com.example.CryptoTrading.request;

import com.example.CryptoTrading.domain.OrderType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateOrderRequest
{

	private String coinId;
	
	private double quantity;
	
	private OrderType orderType;
}
