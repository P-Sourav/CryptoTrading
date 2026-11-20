package com.example.CryptoTrading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CryptoTrading.entities.PaymentDetails;

public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Long>
{

	PaymentDetails findByUserId(Long userId);
}
