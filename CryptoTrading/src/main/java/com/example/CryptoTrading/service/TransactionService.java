package com.example.CryptoTrading.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.CryptoTrading.domain.WalletTransactionType;
import com.example.CryptoTrading.entities.Wallet;
import com.example.CryptoTrading.entities.WalletTransation;

public interface TransactionService {

    WalletTransation createTransaction(Wallet wallet,
                                       WalletTransactionType type,
                                       String transferId,
                                       String purpose,
                                       BigDecimal amount);

    List<WalletTransation> getTransactionsByWallet(Wallet wallet);
}
