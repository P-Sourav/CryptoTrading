package com.example.CryptoTrading.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CryptoTrading.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long>
{
	List<Order> findByUserId(Long userId);
}
