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
public class UserDto {
  private Long id;
  private String firstName;
  private String lastName;
  private String otherName;
  private BigDecimal accountBalance;
  private String accountNumber;
  private String email;
}
