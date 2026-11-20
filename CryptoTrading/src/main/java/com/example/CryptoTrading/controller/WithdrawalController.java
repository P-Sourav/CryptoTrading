package com.example.CryptoTrading.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.CryptoTrading.domain.USER_ROLE;
import com.example.CryptoTrading.domain.WalletTransactionType;
import com.example.CryptoTrading.entities.User;
import com.example.CryptoTrading.entities.Wallet;
import com.example.CryptoTrading.entities.Withdrawal;
import com.example.CryptoTrading.service.TransactionService;
import com.example.CryptoTrading.service.UserService;
import com.example.CryptoTrading.service.WalletService;
import com.example.CryptoTrading.service.WithdrawalService;

@RestController
public class WithdrawalController
{

	@Autowired
	private WithdrawalService withdrawalService;

	@Autowired
	private WalletService walletService;

	@Autowired
	private UserService userService;

	@Autowired
	private TransactionService transactionService;


	@PostMapping("/api/withdrawal/{amount}")
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> withdrawalRequest(@PathVariable Long amount,
												@RequestHeader("Authorization") String jwt) throws Exception
	{
		User user = userService.findUserByProfileByJwt(jwt);

		if (amount == null || amount <= 0)
		{
			throw new Exception("Withdrawal amount should be > 0");
		}

		Wallet userWallet = walletService.getUserWallet(user);

		if (userWallet.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0)
		{
			throw new Exception("Insufficient balance...");
		}

		Withdrawal withdrawal = withdrawalService.requestWithdrawal(amount, user);

		// money is held (deducted) while the request is pending and refunded if the admin declines it
		walletService.addBalanceToWallet(userWallet, -withdrawal.getAmount());

		transactionService.createTransaction(userWallet,
												WalletTransactionType.WITHDRAWAL,
												String.valueOf(withdrawal.getId()),
												"Bank account withdrawal",
												BigDecimal.valueOf(withdrawal.getAmount()).negate());

		return new ResponseEntity<>(withdrawal, HttpStatus.OK);
	}



	@PatchMapping("/api/admin/withdrawal/{id}/proceed/{accept}")
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> proceedWithdrawal(@PathVariable Long id,
												@PathVariable boolean accept,
												@RequestHeader("Authorization") String jwt) throws Exception
	{
		User admin = userService.findUserByProfileByJwt(jwt);
		requireAdmin(admin);

		Withdrawal withdrawal = withdrawalService.procedWithdrawal(id, accept);

		if(!accept)
		{
			// refund the user who requested the withdrawal (previously refunded the admin's own wallet)
			Wallet userWallet = walletService.getUserWallet(withdrawal.getUser());

			walletService.addBalanceToWallet(userWallet, withdrawal.getAmount());

			transactionService.createTransaction(userWallet,
													WalletTransactionType.WITHDRAWAL,
													String.valueOf(withdrawal.getId()),
													"Withdrawal declined - refund",
													BigDecimal.valueOf(withdrawal.getAmount()));
		}

		return new ResponseEntity<>(withdrawal, HttpStatus.OK);
	}



	@GetMapping("/api/withdrawal")
	public ResponseEntity<?> getWithdrawalHistory(@RequestHeader("Authorization") String jwt) throws Exception
	{
		User user = userService.findUserByProfileByJwt(jwt);


		List<Withdrawal> withdrawal = withdrawalService.getUsersWithdrawalHistory(user);

		return new ResponseEntity<>(withdrawal, HttpStatus.OK);
	}



	@GetMapping("/api/admin/withdrawal")
	public ResponseEntity<?> getAllWithdrawalRequest(@RequestHeader("Authorization") String jwt) throws Exception
	{
		User user = userService.findUserByProfileByJwt(jwt);
		requireAdmin(user);

		List<Withdrawal> withdrawal = withdrawalService.getAllWithdrawalRequest();

		return new ResponseEntity<>(withdrawal, HttpStatus.OK);
	}


	private void requireAdmin(User user) throws Exception
	{
		if (user.getRoles() != USER_ROLE.ROLE_ADMIN)
		{
			throw new Exception("Only admins can access this resource");
		}
	}

}
