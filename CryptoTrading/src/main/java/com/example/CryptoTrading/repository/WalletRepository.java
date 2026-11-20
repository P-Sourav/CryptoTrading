package com.example.CryptoTrading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CryptoTrading.entities.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long>
{

	Wallet findByUserId(Long userId);
}
