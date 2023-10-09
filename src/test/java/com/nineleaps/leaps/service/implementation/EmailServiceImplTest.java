package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.config.EmailConfiguration;
import com.nineleaps.leaps.exceptions.EmailSendingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.mockito.Mockito.when;
@Tag("unit_tests")
@DisplayName("Email Service Tests")
@Slf4j
class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private EmailConfiguration emailConfiguration;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Sending Email with Valid Arguments")
    void sendEmail_shouldSendEmail_whenValidArguments() throws Exception {
        String subject = "Test Subject";
        String message = "Test Message";
        String to = "test@example.com";

        when(emailConfiguration.getSender()).thenReturn("test-sender@example.com");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        MimeMessageHelper helper = mock(MimeMessageHelper.class);

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        whenNew(MimeMessageHelper.class).withArguments(mimeMessage, true).thenReturn(helper);

        emailService.sendEmail(subject, message, to);

        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Creating Mime Message")
    void createMimeMessage_shouldCreateMimeMessage() throws MessagingException {
        String subject = "Test Subject";
        String message = "Test Message";
        String to = "test@example.com";

        when(emailConfiguration.getSender()).thenReturn("test-sender@example.com");

        // Mock the Session
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage mimeMessage = new MimeMessage(session);

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessage createdMimeMessage = emailService.createMimeMessage(subject, message, to);

        // Verify MimeMessage properties
        Assertions.assertEquals(emailConfiguration.getSender(), createdMimeMessage.getFrom()[0].toString());
        Assertions.assertEquals(to, createdMimeMessage.getAllRecipients()[0].toString());
        Assertions.assertEquals(subject, createdMimeMessage.getSubject());
    }

    @Test
    @DisplayName("Handling Email Sending Exception")
    void sendEmail_shouldHandleException() {
        String subject = "Test Subject";
        String message = "Test Message";
        String to = "test@example.com";

        when(emailConfiguration.getSender()).thenReturn("test-sender@example.com");
        doThrow(new EmailSendingException("Simulated exception")).when(javaMailSender).send(any(MimeMessage.class));

        assertThrows(EmailSendingException.class, () -> emailService.sendEmail(subject, message, to));
    }


}
