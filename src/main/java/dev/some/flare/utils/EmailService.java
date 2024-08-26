package dev.some.flare.utils;

import dev.some.flare.exception.EmailServiceException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JavaMailSender mailSender;

    public void sendPasswordResetMail(String to, String username, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String subject = String.format("Reset password for %s at Flare", username);
            String body = String.format(""" 
                    <div>
                         <p>Hello, %s</p>
                         <p>Your otp to reset password for <span style="font-weight=bold">Flare<span></p3>
                         <p>This code will only be valid for the next 30 minutes.</p>
                         <h2>%s</h2>
                    </div>
                    """, username, otp);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // `true` indicates HTML content
//            helper.setFrom("your-email@gmail.com");

            mailSender.send(message);
        } catch (MessagingException ex) {
            logger.error("Error occure while sending mail to {}", username);
            throw new EmailServiceException("Failed to send email due to messaging error", ex);
        }
    }
}