package com.example.CryptoTrading.service;

import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import com.example.CryptoTrading.domain.PaymentMethod;
import com.example.CryptoTrading.entities.PaymentOrder;
import com.example.CryptoTrading.entities.User;
import com.example.CryptoTrading.entities.Wallet;
import com.example.CryptoTrading.response.PaymentResponse;

public interface PaymentService
{
	PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);

	PaymentOrder getPaymentOrderById(Long id) throws Exception;

	/**
	 * Verifies the payment with the gateway and, if it is paid, credits the order owner's wallet exactly once.
	 *
	 * @param requester the logged-in user, or null when called by the gateway redirect (no JWT)
	 * @return the order owner's wallet (unchanged if the payment is not completed yet)
	 */
	Wallet confirmDeposit(Long paymentOrderId, User requester) throws Exception;

	PaymentResponse createRazorpayPaymentLink(User user, Long amount, Long orderId) throws RazorpayException;

	PaymentResponse createStripePaymentLink(User user, Long amount, Long orderId) throws StripeException;


}
