package com.example.bankingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto
{
  private String transactionType; // CREDIT, DEBIT, TRANSFER
  private BigDecimal amount;
  private String accountNumber;
  private String status; // SUCCESS, FAILED, PENDING
  private String description;
  private BigDecimal availableBalance;
}
