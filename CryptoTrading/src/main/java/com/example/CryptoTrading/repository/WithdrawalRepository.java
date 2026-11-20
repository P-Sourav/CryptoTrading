package com.example.CryptoTrading.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CryptoTrading.entities.Withdrawal;


public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long>
{

	List<Withdrawal> findByUserId(Long userId);
	
}
