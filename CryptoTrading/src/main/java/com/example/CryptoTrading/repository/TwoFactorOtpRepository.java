package com.example.CryptoTrading.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.CryptoTrading.entities.TwoFactorOTP;

@Repository
public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOTP, String>
{
	TwoFactorOTP findByUserId(Long userId);
	
	
}
