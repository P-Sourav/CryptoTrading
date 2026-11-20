package com.example.CryptoTrading.entities;

import com.example.CryptoTrading.domain.PaymentMethod;
import com.example.CryptoTrading.domain.PaymentOrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class PaymentOrder
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Long amount;

	private PaymentOrderStatus status;

	private PaymentMethod paymentMethod;

	// Razorpay payment-link id or Stripe checkout-session id, used to verify the payment server side
	@JsonIgnore
	private String gatewayReferenceId;

	@ManyToOne
	private User user;

}
