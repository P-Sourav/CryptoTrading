package com.example.CryptoTrading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CryptoTrading.entities.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>
{

}
