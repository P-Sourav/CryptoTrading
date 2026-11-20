package com.example.CryptoTrading.service;

import java.util.List;

import com.example.CryptoTrading.entities.User;
import com.example.CryptoTrading.entities.Withdrawal;

public interface WithdrawalService 
{
	Withdrawal requestWithdrawal(Long amount, User user);
	
	Withdrawal procedWithdrawal(Long withdrawalId, boolean accept) throws Exception;
	
	List<Withdrawal> getUsersWithdrawalHistory(User user);
	
	List<Withdrawal> getAllWithdrawalRequest();
	
	

}
