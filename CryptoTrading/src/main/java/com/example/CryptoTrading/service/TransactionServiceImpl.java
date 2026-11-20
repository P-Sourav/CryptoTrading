package com.example.CryptoTrading.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.CryptoTrading.domain.WalletTransactionType;
import com.example.CryptoTrading.entities.Wallet;
import com.example.CryptoTrading.entities.WalletTransation;
import com.example.CryptoTrading.repository.WalletTransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Override
    public WalletTransation createTransaction(Wallet wallet,
                                              WalletTransactionType type,
                                              String transferId,
                                              String purpose,
                                              BigDecimal amount) {
        WalletTransation transaction = new WalletTransation();
        transaction.setWallet(wallet);
        transaction.setType(type);
        transaction.setDate(LocalDate.now());
        transaction.setTransferId(transferId);
        transaction.setPurpose(purpose);
        transaction.setAmount(amount);

        return walletTransactionRepository.save(transaction);
    }

    @Override
    public List<WalletTransation> getTransactionsByWallet(Wallet wallet) {
        return walletTransactionRepository.findByWalletIdOrderByIdDesc(wallet.getId());
    }
}
