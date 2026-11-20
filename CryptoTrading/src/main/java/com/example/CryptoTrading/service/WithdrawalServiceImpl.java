package com.example.CryptoTrading.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.CryptoTrading.domain.WithdrawalStatus;
import com.example.CryptoTrading.entities.User;
import com.example.CryptoTrading.entities.Withdrawal;
import com.example.CryptoTrading.repository.WithdrawalRepository;

@Service
public class WithdrawalServiceImpl implements WithdrawalService
{
	@Autowired
	private WithdrawalRepository withdrawalRepository;

	public Withdrawal requestWithdrawal(Long amount, User user)
	{
		Withdrawal withdrawal = new Withdrawal();
		withdrawal.setAmount(amount);
		withdrawal.setUser(user);
		withdrawal.setStatus(WithdrawalStatus.PENDING);
	
		return withdrawalRepository.save(withdrawal);
	}

	
	public Withdrawal procedWithdrawal(Long withdrawalId, boolean accept) throws Exception
	{
		Optional<Withdrawal> withdrawal = withdrawalRepository.findById(withdrawalId);
		
		if (withdrawal.isEmpty()) 
		{
			throw new Exception("Withdrawal not found");
		}
		
		Withdrawal withdrawal2 = withdrawal.get();
		
		// a request can only be processed once, otherwise a decline could refund the same money repeatedly
		if (withdrawal2.getStatus() != WithdrawalStatus.PENDING)
		{
			throw new Exception("Withdrawal already processed");
		}
		
		withdrawal2.setDate(LocalDateTime.now());
		
		if (accept)
		{
			withdrawal2.setStatus(WithdrawalStatus.SUCCESS);
		}
		else
		{
			withdrawal2.setStatus(WithdrawalStatus.DECLINE);
		}
		
		return withdrawalRepository.save(withdrawal2);
	}

	
	
	public List<Withdrawal> getUsersWithdrawalHistory(User user) 
	{
		return withdrawalRepository.findByUserId(user.getId());
	}

	public List<Withdrawal> getAllWithdrawalRequest()
	{
		return withdrawalRepository.findAll();
	}

}
