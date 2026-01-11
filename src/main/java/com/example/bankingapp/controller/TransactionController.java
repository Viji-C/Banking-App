package com.example.bankingapp.controller;

import com.example.bankingapp.entity.Transaction;
import com.example.bankingapp.service.BankStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bankStatement")
public class TransactionController
{
  @Autowired
  private BankStatementService bankStatementService;

  @GetMapping("/transactions")
  private List<Transaction> generateBankStatement(@RequestParam String accountNumber, @RequestParam String startDate, @RequestParam String endDate){
    return bankStatementService.generateStatement(accountNumber, startDate, endDate);
  }
}
