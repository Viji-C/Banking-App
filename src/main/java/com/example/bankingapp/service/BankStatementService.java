package com.example.bankingapp.service;

import com.example.bankingapp.entity.Transaction;
import com.example.bankingapp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BankStatementService
{
  /**
   * retrive list of transactions within a date range for given account number
   * generate a pdf file with list of transactions
   * send the pdf file via email
   */

  @Autowired
  TransactionRepository transactionRepository;

  public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate)
  {

    LocalDate start = LocalDate.parse(startDate);
    LocalDate end = LocalDate.parse(endDate);

    LocalDateTime startDateTime = start.atStartOfDay();
    LocalDateTime endDateTime = end.atTime(23, 59, 59);

    return transactionRepository.findByAccountNumberAndCreatedAtBetween(accountNumber, startDateTime, endDateTime);
  }
}
