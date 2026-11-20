package com.example.CryptoTrading.service;

import com.example.CryptoTrading.entities.Coin;
import com.example.CryptoTrading.entities.User;
import com.example.CryptoTrading.entities.WatchList;

public interface WatchListService
{
	WatchList findUserWatchList(Long userId) throws Exception;
	
	WatchList createWatchList(User user);
	
	WatchList findById(Long id) throws Exception;
	
	Coin addItemToWatchList(Coin coin, User user) throws Exception;

}
