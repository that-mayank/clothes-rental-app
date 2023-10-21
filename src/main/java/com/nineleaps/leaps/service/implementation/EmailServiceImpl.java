package com.nineleaps.leaps.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.Properties;

@Service // Marks this class as a Spring service component
@Slf4j // Lombok's annotation to generate a logger for this class
@Transactional // Marks this class as transactional for database operations
public class EmailServiceImpl {

    @Value("${email.username}")
    private String username;
    @Value("${email.password}")
    private String password;

    // Method to send an email
    public boolean sendEmail(String subject, String message, String to) {
        // Check if the 'to' address is null
        if (to == null) {
            // Handle the null value here, such as throwing an exception or returning false
            return false;
        }

        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Step 1: Get the session object for sending the email
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Provide your email credentials here (username and password)
                return new PasswordAuthentication("ateamfour01@gmail.com", "hacikennxstdtkll");
            }
        });

        session.setDebug(true); // Enable debugging for the email sending process

        // Step 2: Compose the email message (subject, recipient, message content)
        MimeMessage m = new MimeMessage(session);

        try {
            m.setFrom(); // Set the sender's email address (you can specify it here)
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); // Add the recipient's email address
            m.setSubject(subject); // Set the email subject
            m.setText(message); // Set the email message content

            // Step 3: Send the email using the Transport class
            Transport.send(m);
            log.info("Email sent successfully"); // Log a success message if the email is sent
            return true;
        } catch (Exception e) {
            log.error("Email notification sending failed"); // Log an error message if email sending fails
        }
        return false;
    }
}
