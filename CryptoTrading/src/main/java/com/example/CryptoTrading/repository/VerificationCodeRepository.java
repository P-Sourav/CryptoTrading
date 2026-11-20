package com.example.CryptoTrading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CryptoTrading.entities.VerificationCode;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long>
{

	VerificationCode findByUserId(Long userId);
	
}
