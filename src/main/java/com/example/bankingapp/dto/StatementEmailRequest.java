package com.example.bankingapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatementEmailRequest
{
  @Schema(description = "Bank account number", example = "1234567890")
  private String accountNumber;
  @Schema(description = "Email ID to receive the bank statement PDF", example = "customer@gmail.com")
  private String email;
  @Schema(description = "Start date for statement (YYYY-MM-DD)", example = "2025-01-01")
  private String startDate;
  @Schema(description = "End date for statement (YYYY-MM-DD)", example = "2025-01-31")
  private String endDate;
}
