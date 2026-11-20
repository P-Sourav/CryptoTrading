package com.example.CryptoTrading.service;

import java.util.List;

import com.example.CryptoTrading.domain.OrderType;
import com.example.CryptoTrading.entities.Coin;
import com.example.CryptoTrading.entities.Order;
import com.example.CryptoTrading.entities.OrderItem;
import com.example.CryptoTrading.entities.User;

public interface OrderService 
{
	Order createOrder(User user, OrderItem orderItem, OrderType orderType);
	
	Order getOrderById(Long orderId) throws Exception;
	
	List<Order> getAllOrderOfUser(Long userId, OrderType orderType, String assetSymbol);

	Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception;
	
	
	
}
