package com.cg.futurefunds.utility;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class MailSenderUtility {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Future Funds <${USER_MAIL}>");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("Future Funds <${USER_MAIL}>");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }

    public void sendPdf(String to, String filePath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("Future Funds <${USER_MAIL}>");

        helper.setTo(to);
        helper.setSubject("Future Funds - Your Investments Report");

        // Add the text part
        helper.setText("Please find attached the investment plans report.");

        // Add the PDF attachment
        File pdfFile = new File(filePath);
        if (pdfFile.exists()) {
            helper.addAttachment("InvestmentReport.pdf", pdfFile);
        } else {
            throw new MessagingException("PDF file not found at the provided path: " + filePath);
        }

        mailSender.send(message);

        System.out.println("Email with the PDF attachment sent to: " + to);
    }
}
