package com.example.CryptoTrading.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.CryptoTrading.entities.PaymentOrder;

import jakarta.persistence.LockModeType;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long>
{
	// row lock so two concurrent callbacks for the same order cannot both credit the wallet
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from PaymentOrder p where p.id = :id")
	Optional<PaymentOrder> findByIdForUpdate(@Param("id") Long id);
}
