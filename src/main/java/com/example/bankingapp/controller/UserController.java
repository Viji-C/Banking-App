package com.example.bankingapp.controller;

import com.example.bankingapp.dto.*;
import com.example.bankingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management APIs")
public class UserController
{
  @Autowired
  UserService userService;

  @Operation(
      summary = "Create a new bank account",
      description = "Creates a new bank account for the user using the provided account creation details."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Account successfully created",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = BankResponse.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request data"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error"
      )
  })
  @PostMapping
  public BankResponse CreateAccount(@RequestBody AccountCreationDto userRequest)
  {
    return userService.createAccount(userRequest);
  }

  @PostMapping("/login")
  public BankResponse login(@RequestBody LoginDto loginDto){
      return userService.login(loginDto);
  }

  @Operation(
      summary = "Fetch all users",
      description = "Returns a list of all registered users."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Successfully retrieved list of users",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = CustomResponse.class))
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error"
      )
  })
  @GetMapping("/allUsers")
  public CustomResponse fetchAllUsers(){
    return userService.fetchAllUsers();
  }

  @Operation(
      summary = "Update an existing user",
      description = "Updates the user details for the given user ID."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User updated successfully",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = CustomResponse.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid input data"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User not found"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error"
      )
  })
  @PutMapping("/{id}")
  public CustomResponse updateUser(@RequestBody AccountCreationDto accountCreationDto, @PathVariable Long id){
    return userService.updateUser(accountCreationDto, id);
  }

  @Operation(
      summary = "Delete a user",
      description = "Deletes a user based on the provided user ID."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User deleted successfully"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @DeleteMapping("/{id}")
  public CustomResponse deleteUser(@PathVariable Long id){
    return userService.deleteUser(id);
  }

  @Operation(
      summary = "Name Enquiry",
      description = "Retrieves and returns the full name of the account holder associated with the given account number."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Account holder found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CustomResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Account not found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CustomResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid or missing account number",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CustomResponse.class)
          )
      )
  })
  @GetMapping("/nameEnquiry")
  public CustomResponse nameEnquiry(@RequestParam String accountNumber){
    return userService.nameEnquiry(accountNumber);
  }

  @Operation(
      summary = "Balance Enquiry",
      description = "Returns the current balance of the account associated with the provided account number."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Balance retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = BankResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Account not found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request parameters",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  @GetMapping("/balanceEnquiry")
  public BankResponse balanceEnquiry(
      @Parameter(
          description = "Account number to check balance for",
          required = true,
          example = "1234567890"
      )
      @RequestParam String accountNumber){
    return userService.balanceEnquiry(accountNumber);
  }

  @Operation(
      summary = "Credit Account",
      description = "Deposits money into a user's bank account."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Amount credited successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = BankResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Account not found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid transaction request",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  @PostMapping("/credit")
  public BankResponse deposit(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Details of the credit transaction",
          required = true,
          content = @Content(
              schema = @Schema(implementation = TransactionRequest.class)
          )
      )
      @RequestBody TransactionRequest transactionRequest){
    return userService.credit(transactionRequest);
  }

  @Operation(
      summary = "Debit Account",
      description = "Withdraws money from a user's bank account."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Amount debited successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = BankResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Account not found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Insufficient funds or invalid request",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  @PostMapping("/debit")
  public BankResponse debit(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Details of the debit transaction",
          required = true,
          content = @Content(
              schema = @Schema(implementation = TransactionRequest.class)
          )
      )
      @RequestBody TransactionRequest transactionRequest){
    return userService.debit(transactionRequest);
  }


  @Operation(
      summary = "Transfer Funds",
      description = "Transfers money from one user's account to another. " +
          "Requires source account, destination account, amount, and narration."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Transfer successful",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = BankResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Source or destination account not found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid transfer request or insufficient funds",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  @PostMapping("/transfer")
  public BankResponse transfer(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Transfer request details",
          required = true,
          content = @Content(
              schema = @Schema(implementation = TransferRequest.class)
          )
      )
      @RequestBody TransferRequest transferRequest) {
    return userService.transfer(transferRequest);
  }
}
