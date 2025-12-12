package com.example.bankingapp.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnquiryRequest {
  private String accountNumber;
}
