package com.nineleaps.leaps.service.implementation;
import com.nineleaps.leaps.config.EmailConfiguration;
import com.nineleaps.leaps.exceptions.EmailSendingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.MessagingException;

@Service
@Slf4j
@AllArgsConstructor
public class EmailServiceImpl {

    private final JavaMailSender javaMailSender;
    private final EmailConfiguration emailConfiguration;

    public void sendEmail(String subject, String message, String to) {
        try {
            MimeMessage mimeMessage = createMimeMessage(subject, message, to);
            javaMailSender.send(mimeMessage);
//            log.info("Email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send email: " + e.getMessage(), e);
            throw new EmailSendingException("Failed to send email");
        }
    }

    MimeMessage createMimeMessage(String subject, String message, String to) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom(emailConfiguration.getSender());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message, true);

        return mimeMessage;
    }
}





