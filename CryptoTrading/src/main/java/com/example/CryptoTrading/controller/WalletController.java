package com.example.CryptoTrading.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.CryptoTrading.entities.Order;
import com.example.CryptoTrading.entities.User;
import com.example.CryptoTrading.entities.Wallet;
import com.example.CryptoTrading.entities.WalletTransation;
import com.example.CryptoTrading.service.OrderService;
import com.example.CryptoTrading.service.PaymentService;
import com.example.CryptoTrading.service.UserService;
import com.example.CryptoTrading.service.WalletService;

@RestController
@RequestMapping("/api/wallet")
public class WalletController
{
	@Autowired
	private WalletService walletService;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private PaymentService paymentService;


	@GetMapping()
	public ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization") String jwt) throws Exception
	{
		User user = userService.findUserByProfileByJwt(jwt);

		Wallet wallet = walletService.getUserWallet(user);

		return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
	}


	@PutMapping("/{walletId}/transfer")
	public ResponseEntity<Wallet> walletToWalletTransfer(@RequestHeader("Authorization") String jwt,
														 @PathVariable	Long walletId,
														 @RequestBody WalletTransation req) throws Exception
	{
		User senderUser = userService.findUserByProfileByJwt(jwt);

		Wallet receiverWallet = walletService.findByWalletId(walletId);

		if (req.getAmount() == null || req.getAmount().signum() <= 0 || req.getAmount().stripTrailingZeros().scale() > 0)
		{
			throw new Exception("Transfer amount should be a whole number > 0");
		}

		Wallet wallet = walletService.walletToWalletTransfer(senderUser,
															receiverWallet,
															req.getAmount().longValueExact());


		return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
	}


	@PutMapping("/order/{orderId}/pay")
	public ResponseEntity<Wallet> payOrderPayment(@RequestHeader("Authorization") String jwt,
												@PathVariable	Long orderId
												)  throws Exception
	{
		User user = userService.findUserByProfileByJwt(jwt);

		Order order = orderService.getOrderById(orderId);

		if (!order.getUser().getId().equals(user.getId()))
		{
			throw new Exception("you don't have access");
		}

		Wallet wallet = walletService.payOrderPayment(order, user);

		return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
	}


	/**
	 * Confirms a top-up. Razorpay / Stripe redirect the browser here with GET after payment, so this
	 * endpoint accepts GET, POST and PUT and does not require a JWT. It is safe without one because the
	 * payment is verified with the gateway server side and only ever credits the payment order's owner, once.
	 * Extra gateway params (razorpay_payment_id, razorpay_signature, ...) are ignored.
	 */
	@RequestMapping(value = "/deposit", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
	public ResponseEntity<Wallet> addBalanceToWallet(@RequestHeader(value = "Authorization", required = false) String jwt,
												@RequestParam("order_id") Long orderId
												)  throws Exception
	{
		User requester = (jwt != null && jwt.startsWith("Bearer ")) ? userService.findUserByProfileByJwt(jwt) : null;

		Wallet wallet = paymentService.confirmDeposit(orderId, requester);

		return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
	}

}
