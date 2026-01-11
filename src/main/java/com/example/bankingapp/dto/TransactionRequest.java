package com.example.bankingapp.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {
  private String accountNumber;
  private BigDecimal amount;
  private String description;
}
