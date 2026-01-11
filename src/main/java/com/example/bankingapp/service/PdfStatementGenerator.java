package com.example.bankingapp.service;

import com.example.bankingapp.dto.EmailDetails;
import com.example.bankingapp.dto.StatementEmailRequest;
import com.example.bankingapp.entity.Transaction;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PdfStatementGenerator
{
  @Autowired
  private EmailService emailService;

  @Autowired
  private BankStatementService bankStatementService;

  public byte[] generateStatementPdf(StatementEmailRequest statementEmailRequest)
  {

    String accountNumber = statementEmailRequest.getAccountNumber();
    String startDate = statementEmailRequest.getStartDate();
    String endDate = statementEmailRequest.getEndDate();
    List<Transaction> transactions = bankStatementService.generateStatement(accountNumber, startDate, endDate);

    try
    {
      Document document = new Document(PageSize.A4);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PdfWriter.getInstance(document, baos);

      document.open();

      // Title
      Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
      Paragraph title = new Paragraph("BANK STATEMENT", titleFont);
      title.setAlignment(Element.ALIGN_CENTER);
      title.setSpacingAfter(20);
      document.add(title);

      // Account info
      Font infoFont = new Font(Font.HELVETICA, 12);
      document.add(new Paragraph("Account Number: " + accountNumber, infoFont));
      document.add(new Paragraph("Statement Period: " + startDate + " to " + endDate, infoFont));
      document.add(Chunk.NEWLINE);

      // Table
      PdfPTable table = new PdfPTable(6);
      table.setWidthPercentage(100);
      table.setSpacingBefore(10);

      addTableHeader(table);
      addRows(table, transactions);

      document.add(table);

      // Footer note
      Paragraph footer = new Paragraph("This is a system-generated statement.",
          new Font(Font.HELVETICA, 10, Font.ITALIC));
      footer.setAlignment(Element.ALIGN_CENTER);
      footer.setSpacingBefore(20);
      document.add(footer);

      document.close();

      return baos.toByteArray();

    }
    catch (Exception e)
    {
      throw new RuntimeException("Error generating PDF statement", e);
    }
  }

  private void addTableHeader(PdfPTable table)
  {
    Stream.of("Date", "Type", "Amount", "Account", "Status", "Balance").forEach(headerTitle -> {
      PdfPCell header = new PdfPCell();
      Font headFont = new Font(Font.HELVETICA, 12, Font.BOLD);
      header.setPhrase(new Phrase(headerTitle, headFont));
      header.setHorizontalAlignment(Element.ALIGN_CENTER);
      header.setBackgroundColor(Color.LIGHT_GRAY);
      table.addCell(header);
    });
  }

  private void addRows(PdfPTable table, List<Transaction> transactions)
  {

    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    for (Transaction t : transactions)
    {
      table.addCell(t.getCreatedAt().format(dateFormat));
      table.addCell(t.getTransactionType());
      table.addCell(t.getAmount().toString());
      table.addCell(t.getAccountNumber());
      table.addCell(t.getStatus());
      table.addCell(t.getAvailableBalance().toString());
    }
  }

  public String generateAndSendStatement(StatementEmailRequest statementEmailRequest)
  {

    byte[] pdfBytes = generateStatementPdf(statementEmailRequest);

    EmailDetails details = new EmailDetails();
    details.setRecipient(statementEmailRequest.getEmail());
    details.setSubject("Your Bank Statement");
    details.setMessageBody("Please find attached your requested bank statement.");
    details.setAttachmentName("Bank-Statement.pdf");
    details.setAttachment(pdfBytes);

    emailService.sendEmailWithAttachment(details);

    return "Bank statement emailed successfully!";
  }
}
