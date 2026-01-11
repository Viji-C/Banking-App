package com.example.bankingapp.service;

import com.example.bankingapp.dto.EmailDetails;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService
{
  @Autowired
  private JavaMailSender javaMailSender;

  @Value("${spring.mail.username}")
  private String senderEmail;

  @Async
  public void sendEmailAlert(EmailDetails emailDetails) {
    try {
      SimpleMailMessage mailMessage = new SimpleMailMessage();
      mailMessage.setFrom(senderEmail);
      mailMessage.setTo(emailDetails.getRecipient());
      mailMessage.setText(emailDetails.getMessageBody());
      mailMessage.setSubject(emailDetails.getSubject());

      javaMailSender.send(mailMessage);
      System.out.println("Mail sent successfully");
    } catch (MailException e) {
      throw new RuntimeException(e);
    }
  }

  @Async
  public void sendEmailWithAttachment(EmailDetails details) {

    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);

      helper.setFrom(senderEmail);
      helper.setTo(details.getRecipient());
      helper.setSubject(details.getSubject());
      helper.setText(details.getMessageBody());

      helper.addAttachment(details.getAttachmentName(),
          new ByteArrayDataSource(details.getAttachment(), "application/pdf"));

      javaMailSender.send(message);

    } catch (Exception e) {
      throw new RuntimeException("Failed to send email", e);
    }
  }
}
