package com.example.CryptoTrading.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.CryptoTrading.domain.OrderType;
import com.example.CryptoTrading.domain.WalletTransactionType;
import com.example.CryptoTrading.entities.Order;
import com.example.CryptoTrading.entities.User;
import com.example.CryptoTrading.entities.Wallet;
import com.example.CryptoTrading.repository.WalletRepository;

@Service
public class WallletServiceImpl implements WalletService
{
	
	@Autowired
	private WalletRepository walletRepository;
	
	@Autowired
	private TransactionService transactionService;
	
	

	public Wallet getUserWallet(User user) 
	{
		Wallet wallet = walletRepository.findByUserId(user.getId());
		
		if (wallet == null)
		{
			wallet = new Wallet();
			wallet.setUser(user);
			wallet.setBalance(BigDecimal.ZERO);
			return walletRepository.save(wallet);
		}
		
		// wallets created before the default existed may still have a NULL balance in the DB
		if (wallet.getBalance() == null)
		{
			wallet.setBalance(BigDecimal.ZERO);
			wallet = walletRepository.save(wallet);
		}
		return wallet;
	}
	
	

	public Wallet addBalanceToWallet(Wallet wallet, Long money) 
	{
		BigDecimal balance = wallet.getBalance() == null ? BigDecimal.ZERO : wallet.getBalance();
		
		BigDecimal newBalance = balance.add(BigDecimal.valueOf(money));
		
		wallet.setBalance(newBalance);
		
		return walletRepository.save(wallet);
	}

	
	public Wallet findByWalletId(Long id) throws Exception 
	{
		Optional<Wallet> wallet = walletRepository.findById(id);
		
		if (wallet.isPresent())
		{
			return wallet.get();
		}
		
		throw new Exception("Wallet not found");
	}
	
	

	@Transactional(rollbackFor = Exception.class)
	public Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, Long amount) throws Exception
	{
		if (amount == null || amount <= 0)
		{
			throw new Exception("Transfer amount should be > 0");
		}
		
		Wallet senderWallet = getUserWallet(sender);
		
		if (senderWallet.getId().equals(receiverWallet.getId()))
		{
			throw new Exception("Source and destination wallet cannot be the same");
		}
		
		BigDecimal transferAmount = BigDecimal.valueOf(amount);
		
		if (senderWallet.getBalance().compareTo(transferAmount) < 0)
		{
			throw new Exception("Insufficient balance...");
		} 
		
		BigDecimal receiverCurrent = receiverWallet.getBalance() == null ? BigDecimal.ZERO : receiverWallet.getBalance();
		
		senderWallet.setBalance(senderWallet.getBalance().subtract(transferAmount));
		walletRepository.save(senderWallet);
		
		receiverWallet.setBalance(receiverCurrent.add(transferAmount));
		walletRepository.save(receiverWallet);
		
		transactionService.createTransaction(senderWallet, WalletTransactionType.WALLET_TRANSFER,
				String.valueOf(receiverWallet.getId()), "Transfer to wallet " + receiverWallet.getId(),
				transferAmount.negate());
		
		transactionService.createTransaction(receiverWallet, WalletTransactionType.WALLET_TRANSFER,
				String.valueOf(senderWallet.getId()), "Transfer from wallet " + senderWallet.getId(),
				transferAmount);
	
		return senderWallet;
	}

	@Transactional(rollbackFor = Exception.class)
	public Wallet payOrderPayment(Order order, User user) throws Exception
	{
		Wallet wallet = getUserWallet(user);
		
		if(order.getOrderType().equals(OrderType.BUY))
		{
			// check the current balance against the price (previously compared the balance *after* paying)
			if(wallet.getBalance().compareTo(order.getPrice()) < 0)
			{
				throw new Exception("Insufficient funds for this transaction");
			}
			
			wallet.setBalance(wallet.getBalance().subtract(order.getPrice()));
			
			transactionService.createTransaction(wallet, WalletTransactionType.BUY_ASSET,
					String.valueOf(order.getId()), "Buy " + order.getOrderItem().getCoin().getSymbol(),
					order.getPrice().negate());
		}
		else
		{
			wallet.setBalance(wallet.getBalance().add(order.getPrice()));
			
			transactionService.createTransaction(wallet, WalletTransactionType.SELL_ASSET,
					String.valueOf(order.getId()), "Sell " + order.getOrderItem().getCoin().getSymbol(),
					order.getPrice());
		}
		
		walletRepository.save(wallet);
		
		return wallet;
	}

}
