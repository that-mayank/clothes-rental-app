package com.nineleaps.leaps.service.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.Properties;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class EmailServiceImpl {
    public void sendEmail(String subject, String message, String to) {

        if (to == null) {
            // Handle the null value here, such as throwing an exception or returning false
            return;
        }
        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Step 1: to get the session object..
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("prathiksha.v@nineleaps.com", "rijzafoqaxoctaba");
            }

        });

        session.setDebug(true);

        // Step 2 : compose the message [text,multimedia]
        MimeMessage m = new MimeMessage(session);

        try {

            m.setFrom();
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            m.setSubject(subject);
            m.setText(message);


            // Step 3 : send the message using Transport class
            Transport.send(m);
            log.trace("sent success");


        } catch (Exception e) {
            log.error("email notification sending failed");
        }

    }

}




