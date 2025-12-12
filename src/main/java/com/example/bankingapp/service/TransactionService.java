package com.example.bankingapp.service;

import com.example.bankingapp.dto.TransactionDto;
import com.example.bankingapp.entity.Transaction;
import com.example.bankingapp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService
{

  @Autowired
  TransactionRepository transactionRepository;

  public void saveTransaction(TransactionDto transactionDto)
  {
    Transaction transaction = Transaction.builder()
        .transactionType(transactionDto.getTransactionType())
        .accountNumber(transactionDto.getAccountNumber())
        .amount(transactionDto.getAmount())
        .status("SUCCESS")
        .availableBalance(transactionDto.getAvailableBalance())
        .description(transactionDto.getDescription())
        .build();
    transactionRepository.save(transaction);
  }
}
