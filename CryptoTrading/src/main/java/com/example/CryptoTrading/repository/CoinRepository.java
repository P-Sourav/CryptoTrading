package com.example.CryptoTrading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CryptoTrading.entities.Coin;

public interface CoinRepository extends JpaRepository<Coin, String>
{

}
