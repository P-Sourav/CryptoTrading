package com.example.CryptoTrading.service;

import com.example.CryptoTrading.entities.Order;
import com.example.CryptoTrading.entities.User;
import com.example.CryptoTrading.entities.Wallet;

public interface WalletService 
{
	Wallet getUserWallet(User user);
	
	Wallet addBalanceToWallet(Wallet wallet, Long money);
	
	Wallet findByWalletId(Long id) throws Exception;
	
	Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, Long amount) throws Exception;
	
	Wallet payOrderPayment(Order order, User user) throws Exception;
	
	

}
