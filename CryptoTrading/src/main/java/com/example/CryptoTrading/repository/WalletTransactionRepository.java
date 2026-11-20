package com.example.CryptoTrading.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CryptoTrading.entities.WalletTransation;

public interface WalletTransactionRepository extends JpaRepository<WalletTransation, Long>
{
	List<WalletTransation> findByWalletIdOrderByIdDesc(Long walletId);
}
