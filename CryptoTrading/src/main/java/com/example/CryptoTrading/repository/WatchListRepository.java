package com.example.CryptoTrading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CryptoTrading.entities.WatchList;

public interface WatchListRepository extends JpaRepository<WatchList, Long>
{

	WatchList findByUserId(Long userId);
}
