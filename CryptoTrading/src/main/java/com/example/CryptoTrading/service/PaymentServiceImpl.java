package com.example.CryptoTrading.service;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.example.CryptoTrading.domain.PaymentMethod;
import com.example.CryptoTrading.domain.PaymentOrderStatus;
import com.example.CryptoTrading.domain.WalletTransactionType;
import com.example.CryptoTrading.entities.PaymentOrder;
import com.example.CryptoTrading.entities.User;
import com.example.CryptoTrading.entities.Wallet;
import com.example.CryptoTrading.repository.PaymentOrderRepository;
import com.example.CryptoTrading.response.PaymentResponse;

@Service
public class PaymentServiceImpl implements PaymentService
{
	@Autowired
	private PaymentOrderRepository paymentOrderRepository;

	@Autowired
	private WalletService walletService;

	@Autowired
	private TransactionService transactionService;

	@Value("${stripe.api.key}")
	private String stripeSecretKey;

	@Value("${razorpay.api.key}")
	private String razorApiKey;

	@Value("${razorpay.api.secret}")
	private String apiSecretKey;

	// public URL of this backend, used for the gateway redirect back to /api/wallet/deposit
	@Value("${app.base-url:http://localhost:8080}")
	private String baseUrl;


	public PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod)
	{
		PaymentOrder paymentOrder = new PaymentOrder();

		paymentOrder.setUser(user);
		paymentOrder.setAmount(amount);
		paymentOrder.setPaymentMethod(paymentMethod);
		paymentOrder.setStatus(PaymentOrderStatus.PENDING);
		return paymentOrderRepository.save(paymentOrder);
	}

	public PaymentOrder getPaymentOrderById(Long id) throws Exception
	{
		return paymentOrderRepository.findById(id)
				.orElseThrow(() -> new Exception("Payment order not found"));
	}



	@Transactional(rollbackFor = Exception.class)
	public Wallet confirmDeposit(Long paymentOrderId, User requester) throws Exception
	{
		PaymentOrder paymentOrder = paymentOrderRepository.findByIdForUpdate(paymentOrderId)
				.orElseThrow(() -> new Exception("Payment order not found"));

		if (requester != null && !paymentOrder.getUser().getId().equals(requester.getId()))
		{
			throw new Exception("you don't have access to this payment order");
		}

		Wallet wallet = walletService.getUserWallet(paymentOrder.getUser());

		// already credited (or failed) -> never credit twice
		if (paymentOrder.getStatus() != null && paymentOrder.getStatus() != PaymentOrderStatus.PENDING)
		{
			return wallet;
		}

		if (!isPaidAtGateway(paymentOrder))
		{
			return wallet;			// not paid yet; status stays PENDING so it can be confirmed later
		}

		paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
		paymentOrderRepository.save(paymentOrder);

		wallet = walletService.addBalanceToWallet(wallet, paymentOrder.getAmount());

		transactionService.createTransaction(wallet, WalletTransactionType.ADD_MONEY,
				String.valueOf(paymentOrder.getId()),
				"Deposit via " + paymentOrder.getPaymentMethod(),
				BigDecimal.valueOf(paymentOrder.getAmount()));

		return wallet;
	}


	// asks Razorpay / Stripe whether the order was really paid in full, instead of trusting callback params
	private boolean isPaidAtGateway(PaymentOrder paymentOrder) throws Exception
	{
		String reference = paymentOrder.getGatewayReferenceId();

		if (reference == null || reference.isBlank())
		{
			throw new Exception("Payment order has no gateway reference; create a new payment link");
		}

		long expectedMinorUnits = paymentOrder.getAmount() * 100;

		if (paymentOrder.getPaymentMethod() == PaymentMethod.RAZORPAY)
		{
			RazorpayClient razorpay = new RazorpayClient(razorApiKey, apiSecretKey);

			PaymentLink link = razorpay.paymentLink.fetch(reference);

			String status = link.get("status");
			Object amountPaid = link.get("amount_paid");

			return "paid".equals(status)
					&& amountPaid instanceof Number
					&& ((Number) amountPaid).longValue() >= expectedMinorUnits;
		}

		Stripe.apiKey = stripeSecretKey;

		Session session = Session.retrieve(reference);

		return "paid".equals(session.getPaymentStatus())
				&& session.getAmountTotal() != null
				&& session.getAmountTotal() >= expectedMinorUnits;
	}


	private void saveGatewayReference(Long orderId, String reference)
	{
		paymentOrderRepository.findById(orderId).ifPresent(order -> {
			order.setGatewayReferenceId(reference);
			paymentOrderRepository.save(order);
		});
	}



	public PaymentResponse createRazorpayPaymentLink(User user, Long amount, Long orderId) throws RazorpayException
	{
		Long amountInPaise = amount*100;

		 try
		 {
			 // Instantiate a razorpay client with your key ID and Secret
			 RazorpayClient razorpay = new RazorpayClient(razorApiKey, apiSecretKey);

			 // create JSON object with the payment link request paramenters
			 JSONObject paymentLinkRequest = new JSONObject();

			 paymentLinkRequest.put("amount", amountInPaise);
			 paymentLinkRequest.put("currency", "INR");
			 paymentLinkRequest.put("reference_id", String.valueOf(orderId));


			 // create a JSON object with the customer details
			 JSONObject customer = new JSONObject();

			 customer.put("name", user.getFullName());
			 customer.put("email", user.getEmail());

			 paymentLinkRequest.put("customer", customer);


			 // create a JSON object with the notification setting
			 JSONObject notify = new JSONObject();

			 notify.put("email", true);

			 paymentLinkRequest.put("notify", notify);

			 //set the remainder setting
			 paymentLinkRequest.put("reminder_enable", true);

			 //set the callback URL and method (the deposit endpoint accepts GET for this redirect)
			 paymentLinkRequest.put("callback_url", baseUrl + "/api/wallet/deposit?order_id=" + orderId);
			 paymentLinkRequest.put("callback_method", "get");

			 PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);

			 String paymentLinkId = payment.get("id");
			 String paymentLinkUrl = payment.get("short_url");

			 saveGatewayReference(orderId, paymentLinkId);

			 PaymentResponse res = new PaymentResponse();
			 res.setPayment_url(paymentLinkUrl);

			 return res;
		 }
		catch (RazorpayException e)
		 {
			System.out.println("Error creating payment link: "+ e.getMessage());
			throw new RazorpayException(e.getMessage());
		 }
	}




	public PaymentResponse createStripePaymentLink(User user, Long amount, Long orderId) throws StripeException
	{
			Stripe.apiKey = stripeSecretKey;

			SessionCreateParams params = SessionCreateParams.builder()
											.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
											.setMode(SessionCreateParams.Mode.PAYMENT)
											.setSuccessUrl(baseUrl + "/api/wallet/deposit?order_id="+orderId)
											.setCancelUrl(baseUrl + "/payment/cancel")
											.setClientReferenceId(String.valueOf(orderId))
											.addLineItem(SessionCreateParams.LineItem.builder()
													.setQuantity(1L)
													.setPriceData(
															SessionCreateParams
															.LineItem
															.PriceData.builder()
															.setCurrency("usd")
															.setUnitAmount(amount*100)
															.setProductData(SessionCreateParams
																				.LineItem
																				.PriceData
																				.ProductData
																				.builder()
																				.setName("Top up wallet")
																				.build()
																	).build()
															).build()
													).build();


		Session session = Session.create(params);

		saveGatewayReference(orderId, session.getId());

		PaymentResponse response = new PaymentResponse();
		response.setPayment_url(session.getUrl());

		return response;
	}

}
