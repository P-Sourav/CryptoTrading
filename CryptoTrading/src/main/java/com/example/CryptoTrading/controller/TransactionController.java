package com.example.CryptoTrading.controller;

import com.example.CryptoTrading.entities.User;
import com.example.CryptoTrading.entities.Wallet;
import com.example.CryptoTrading.entities.WalletTransation;
import com.example.CryptoTrading.service.TransactionService;
import com.example.CryptoTrading.service.UserService;
import com.example.CryptoTrading.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    // wallet history of the logged-in user, newest first
    @GetMapping
    public ResponseEntity<List<WalletTransation>> getUserTransactions(
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByProfileByJwt(jwt);

        Wallet wallet = walletService.getUserWallet(user);
        List<WalletTransation> transactionList = transactionService.getTransactionsByWallet(wallet);

        return new ResponseEntity<>(transactionList, HttpStatus.OK);
    }

}
