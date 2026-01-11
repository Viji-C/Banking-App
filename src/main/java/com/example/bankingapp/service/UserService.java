package com.example.bankingapp.service;

import com.example.bankingapp.config.JwtTokenProvider;
import com.example.bankingapp.dto.*;
import com.example.bankingapp.entity.Role;
import com.example.bankingapp.entity.User;
import com.example.bankingapp.repository.UserRepository;
import com.example.bankingapp.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class UserService
{

  @Autowired
  UserRepository userRepository;

  @Autowired
  EmailService emailService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  TransactionService transactionService;

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  public BankResponse createAccount(AccountCreationDto userRequest) {

    /**
     * Creating an account - saving a new user into the db
     * check if user already has an account
     */
    if (userRepository.existsByEmail(userRequest.getEmail())){
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
          .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
          .accountInfo(null)
          .build();
    }
    User newUser = User.builder()
        .firstName(userRequest.getFirstName())
        .lastName(userRequest.getLastName())
        .otherName(userRequest.getOtherName())
        .gender(userRequest.getGender())
        .address(userRequest.getAddress())
        .stateOfOrigin(userRequest.getStateOfOrigin())
        .accountNumber(AccountUtils.generateAccountNumber())
        .accountBalance(BigDecimal.ZERO)
        .email(userRequest.getEmail())
        .password(passwordEncoder.encode(userRequest.getPassword()))
        .phoneNumber(userRequest.getPhoneNumber())
        .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
        .active(true)
        .role(Role.ROLE_ADMIN)
        .build();

    User savedUser = userRepository.save(newUser);
    //Send email Alert
    EmailDetails emailDetails = EmailDetails.builder()
        .recipient(savedUser.getEmail())
        .subject("ACCOUNT CREATION")
        .messageBody("Congratulations! Your Account Has been Successfully Created.\nYour Account Details: \n" +
            "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\nAccount Number: " + savedUser.getAccountNumber())
        .build();
    emailService.sendEmailAlert(emailDetails);

    return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
        .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
        .accountInfo(AccountInfo.builder()
            .accountBalance(savedUser.getAccountBalance())
            .accountNumber(savedUser.getAccountNumber())
            .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
            .build())
        .build();
  }

  public BankResponse login(LoginDto loginDto){
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
    );

    EmailDetails loginAlert = EmailDetails.builder()
            .subject("You're logged in!")
            .recipient(loginDto.getEmail())
            .messageBody("You are logged in your account. If you, If you didn't initiate your request, please contact your bank")
            .build();

    emailService.sendEmailAlert(loginAlert);

    return BankResponse.builder()
            .responseCode("Login Success")
            .responseMessage(jwtTokenProvider.generateToken(authentication))
            .build();
  }
  public CustomResponse fetchAllUsers(){
    List<User> userList = userRepository.findAll();
    List<UserDto> userDtos = userList.stream().map(AccountUtils::mapUserToUserDto).toList();
    return CustomResponse.builder()
        .error(false)
        .message("SUCCESS")
        .users(userDtos)
        .build();
  }

  public CustomResponse updateUser(AccountCreationDto accountCreationDto, Long id){
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()){
      return CustomResponse.builder()
          .error(true)
          .message("User with id " + id + " not found")
          .build();
    }
    User userToUpdate = user.get();
    if (accountCreationDto.getFirstName() != null){
      userToUpdate.setFirstName(accountCreationDto.getFirstName());
    }
    if (accountCreationDto.getLastName() != null){
      userToUpdate.setLastName(accountCreationDto.getLastName());
    }
    if (accountCreationDto.getOtherName() != null){
      userToUpdate.setOtherName(accountCreationDto.getOtherName());
    }
    userToUpdate = userRepository.save(userToUpdate);
    EmailDetails emailDetails = EmailDetails.builder()
        .recipient(userToUpdate.getEmail())
        .subject("ACCOUNT UPDATED")
        .messageBody("Congratulations! Your Account Has been Successfully Updated.\nYour Account Details: \n" +
            "Account Name: " + userToUpdate.getFirstName() + " " + userToUpdate.getLastName() + " " + userToUpdate.getOtherName() + "\nAccount Number: " + userToUpdate.getAccountNumber())
        .build();
    emailService.sendEmailAlert(emailDetails);
    return CustomResponse.builder()
        .error(false)
        .message("SUCCESS")
        .userDetails(AccountUtils.mapUserToUserDto(userToUpdate))
        .build();
  }

  public CustomResponse deleteUser(Long id){
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()){
      return CustomResponse.builder()
          .error(true)
          .message("User with id " + id + " not found")
          .build();
    }
    userRepository.delete(user.get());
    return CustomResponse.builder()
        .error(false)
        .message("User deleted!")
        .build();
  }

  public CustomResponse nameEnquiry(String accountNumber) {
    boolean isAccountExist = userRepository.existsByAccountNumber(accountNumber);
    if (!isAccountExist){
      return CustomResponse.builder()
          .error(true)
          .message(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .build();
    }
    User foundUser = userRepository.findByAccountNumber(accountNumber);
    UserDto userDto = UserDto.builder()
        .id(foundUser.getId())
        .firstName(foundUser.getFirstName())
        .lastName(foundUser.getLastName())
        .otherName(foundUser.getOtherName())
        .accountBalance(foundUser.getAccountBalance())
        .accountNumber(foundUser.getAccountNumber())
        .email(foundUser.getEmail())
        .build();
    return CustomResponse.builder()
        .error(false)
        .message(AccountUtils.ACCOUNT_FOUND_SUCCESS)
        .userDetails(userDto)
        .build();
  }

  public BankResponse balanceEnquiry(String accountNumber) {
    //check if the provided account number exists in the db
    boolean isAccountExist = userRepository.existsByAccountNumber(accountNumber);
    if (!isAccountExist){
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .accountInfo(null)
          .build();
    }

    User foundUser = userRepository.findByAccountNumber(accountNumber);
    return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
        .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
        .accountInfo(AccountInfo.builder()
            .accountBalance(foundUser.getAccountBalance())
            .accountNumber(foundUser.getAccountNumber())
            .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
            .build())
        .build();
  }

  public BankResponse credit(TransactionRequest request) {
    //checking if the account exists
    boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
    if (!isAccountExist){
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .accountInfo(null)
          .build();
    }

    User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
    userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
    userRepository.save(userToCredit);

    TransactionDto transactionDto = TransactionDto.builder()
        .accountNumber(userToCredit.getAccountNumber())
        .transactionType("CREDIT")
        .amount(request.getAmount())
        .status("SUCCESS")
        .description(request.getDescription())
        .availableBalance(userToCredit.getAccountBalance())
        .build();
    transactionService.saveTransaction(transactionDto);

    EmailDetails creditAlert = EmailDetails.builder()
        .subject("CREDIT ALERT")
        .recipient(userToCredit.getEmail())
        .messageBody("The Amount of " + request.getAmount() + " has credited to your Account. Now the total Amount of Balance in your account is " + userToCredit.getAccountBalance())
        .build();
    emailService.sendEmailAlert(creditAlert);

    return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
        .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
        .accountInfo(AccountInfo.builder()
            .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
            .accountBalance(userToCredit.getAccountBalance())
            .accountNumber(request.getAccountNumber())
            .build())
        .build();
  }

  public BankResponse debit(TransactionRequest request) {
    //check if the account exists
    //check if the amount you intend to withdraw is not more than the current account balance
    boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
    if (!isAccountExist){
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .accountInfo(null)
          .build();
    }

    User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
    BigDecimal availableBalance =userToDebit.getAccountBalance();
    BigDecimal debitAmount = request.getAmount();
    if ( availableBalance.intValue() < debitAmount.intValue()){
      return BankResponse.builder()
          .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
          .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
          .accountInfo(null)
          .build();
    }
    else {
      userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
      userRepository.save(userToDebit);

      TransactionDto transactionDto = TransactionDto.builder()
          .accountNumber(userToDebit.getAccountNumber())
          .transactionType("DEBIT")
          .amount(request.getAmount())
          .description(request.getDescription())
          .availableBalance(userToDebit.getAccountBalance())
          .build();
      transactionService.saveTransaction(transactionDto);

      EmailDetails debitAlert = EmailDetails.builder()
          .subject("DEBIT ALERT")
          .recipient(userToDebit.getEmail())
          .messageBody("The Amount of " + request.getAmount() + " has debited from your Account. Now the total Amount of Balance in your account is " + userToDebit.getAccountBalance())
          .build();
      emailService.sendEmailAlert(debitAlert);

      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
          .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
          .accountInfo(AccountInfo.builder()
              .accountNumber(request.getAccountNumber())
              .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
              .accountBalance(userToDebit.getAccountBalance())
              .build())
          .build();
    }
  }

  public BankResponse transfer(TransferRequest transferRequest)
  {
    // get the account to debit (check if exists)
    // Check debit amount is more than the current balance
    // debit the amount from account
    // credit the amount to account
    boolean isSourceAccountExist = userRepository.existsByAccountNumber(transferRequest.getSourceAccountNumber());
    boolean isDestinationAccount = userRepository.existsByAccountNumber(transferRequest.getDestinationAccountNumber());

    if(!isSourceAccountExist){
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .accountInfo(null)
          .build();
    }

    if (!isDestinationAccount){
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .accountInfo(null)
          .build();
    }

    User sourceAccountUser = userRepository.findByAccountNumber(transferRequest.getSourceAccountNumber());
    if(sourceAccountUser.getAccountBalance().compareTo(transferRequest.getAmount()) < 0){
      return BankResponse.builder()
          .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
          .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
          .accountInfo(null)
          .build();
    }

    sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(transferRequest.getAmount()));
    String sourceUserName = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName() + " " + sourceAccountUser.getOtherName();

    userRepository.save(sourceAccountUser);

    TransactionDto debitTransactionDto = TransactionDto.builder()
        .accountNumber(sourceAccountUser.getAccountNumber())
        .transactionType("DEBIT")
        .amount(transferRequest.getAmount())
        .description(transferRequest.getDescription())
        .availableBalance(sourceAccountUser.getAccountBalance())
        .build();
    transactionService.saveTransaction(debitTransactionDto);

    EmailDetails debitAlert = EmailDetails.builder()
        .subject("DEBIT ALERT")
        .recipient(sourceAccountUser.getEmail())
        .messageBody("The Amount of " + transferRequest.getAmount() + " has been debited from your account")
        .build();
    emailService.sendEmailAlert(debitAlert);

    User destinatinoAccountUser = userRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber());
    destinatinoAccountUser.setAccountBalance(destinatinoAccountUser.getAccountBalance().add(transferRequest.getAmount()));
    String recipientUserName = destinatinoAccountUser.getFirstName() + " " + destinatinoAccountUser.getLastName() + " " + destinatinoAccountUser.getOtherName();
    userRepository.save(destinatinoAccountUser);

    TransactionDto creditTransactionDto = TransactionDto.builder()
        .accountNumber(destinatinoAccountUser.getAccountNumber())
        .transactionType("CREDIT")
        .amount(transferRequest.getAmount())
        .description(transferRequest.getDescription())
        .availableBalance(destinatinoAccountUser.getAccountBalance())
        .build();
    transactionService.saveTransaction(creditTransactionDto);

    EmailDetails creditAlert = EmailDetails.builder()
        .subject("CREDIT ALERT")
        .recipient(destinatinoAccountUser.getEmail())
        .messageBody("The amount of " + transferRequest.getAmount() + " has been credited to your account from " + sourceUserName + " Your current balance is " + destinatinoAccountUser.getAccountBalance())
        .build();
    emailService.sendEmailAlert(creditAlert);

    return BankResponse.builder()
        .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
        .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
        .accountInfo(null)
        .build();
  }
}
