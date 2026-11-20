package com.example.CryptoTrading.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.CryptoTrading.domain.WalletTransactionType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class WalletTransation 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@JsonIgnore						// avoid serialising the wallet (and its user) inside every history row
	@ManyToOne
	private Wallet wallet;
	
	
	private WalletTransactionType type;
	
	private LocalDate date;
	
	private String transferId;				// id of the other wallet / order / withdrawal involved
	
	private String purpose;
	
	// signed amount: positive = money in, negative = money out
	private BigDecimal amount;
	
	
}
