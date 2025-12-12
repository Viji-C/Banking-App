package com.example.bankingapp.repository;

import com.example.bankingapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.beans.JavaBean;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String>
{
  List<Transaction> findByAccountNumberAndCreatedAtBetween(String accountNumber, LocalDateTime start, LocalDateTime end);
}
