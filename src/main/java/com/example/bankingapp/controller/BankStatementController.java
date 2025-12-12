package com.example.bankingapp.controller;

import com.example.bankingapp.dto.EmailDetails;
import com.example.bankingapp.dto.StatementEmailRequest;
import com.example.bankingapp.entity.Transaction;
import com.example.bankingapp.service.BankStatementService;
import com.example.bankingapp.service.EmailService;
import com.example.bankingapp.service.PdfStatementGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statement")
@Tag(name = "Bank Statements", description = "Endpoints for generating and emailing bank statements")
public class BankStatementController
{

  @Autowired
  private BankStatementService statementService;
  @Autowired
  private PdfStatementGenerator pdfGenerator;
  @Autowired
  private EmailService emailService;

  @Operation(
      summary = "Download bank statement as PDF",
      description = "Generates a bank statement PDF for the given account number and date range"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "PDF generated successfully",
          content = @Content(mediaType = "application/pdf",
              schema = @Schema(type = "string", format = "binary"))),
      @ApiResponse(responseCode = "400", description = "Invalid request data"),
      @ApiResponse(responseCode = "404", description = "Account not found")
  })
  @PostMapping("/download")
  public ResponseEntity<byte[]> downloadStatementPdf(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Bank statement generation details",
          required = true,
          content = @Content(schema = @Schema(implementation = StatementEmailRequest.class))
      )
      @RequestBody StatementEmailRequest statementEmailRequest) {

    byte[] pdfBytes = pdfGenerator.generateStatementPdf(statementEmailRequest);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=BankStatement-" + statementEmailRequest.getAccountNumber() + ".pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .body(pdfBytes);
  }

  @Operation(
      summary = "Email bank statement PDF",
      description = "Generates a bank statement PDF and emails it to the recipient's email address"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Email sent successfully",
          content = @Content(mediaType = "text/plain")),
      @ApiResponse(responseCode = "400", description = "Invalid request"),
      @ApiResponse(responseCode = "500", description = "Failed to send email")
  })
  @PostMapping("/email")
  public String emailBankStatement(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Details required to generate and send bank statement",
          required = true,
          content = @Content(schema = @Schema(implementation = StatementEmailRequest.class))
      )
      @RequestBody StatementEmailRequest statementEmailRequest) {
    return pdfGenerator.generateAndSendStatement(statementEmailRequest);
  }
}
