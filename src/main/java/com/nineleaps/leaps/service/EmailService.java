package com.nineleaps.leaps.service;

import com.nineleaps.leaps.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    CartRepository cartRepository;

    //@Scheduled(cron = "0/15 * * * * ?")
    public boolean sendEmail(String subject, String message , String to) {

        if (to == null) {
            // Handle the null value here, such as throwing an exception or returning false
            return false;
        }
        // Variable for gmail
        String host = "smtp.gmail.com";

        String from = "prathiksha";

        boolean f = false;
        // get the system properties
        Properties properties = System.getProperties();
        //System.out.println("PROPERTIES " + properties);

        // setting important information to properties object

        // host set
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        //properties.put("mail.smtp.ssl.debug","true");
        //properties.put("mail.smtp.starttls.enable", "true");

        // Step 1: to get the session object..
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("prathiksha.v@nineleaps.com", "rijzafoqaxoctaba");
            }

        });

        session.setDebug(true);

        // Step 2 : compose the message [text,multi media]
        MimeMessage m = new MimeMessage(session);

        try {

            // from email
            m.setFrom();

            // adding recipient to message
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // adding subject to message
            m.setSubject(subject);

            // adding text to message
            m.setText(message);

            // send

            // Step 3 : send the message using Transport class
            Transport.send(m);

            System.out.println("Sent success...................");

            f = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return f;
    }

    }




