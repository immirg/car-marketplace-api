package org.example.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.example.client.AppUser;
import org.example.dao.AppUserDAO;
import org.example.entity.CarInReview;
import org.example.enums.Role;
import org.example.exceptions.UserException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

@Service
@AllArgsConstructor
public class MailService {
    private JavaMailSender javaMailSender;
    private final AppUserDAO appUserDAO;

    public void send(CarInReview carInReview, String userEmail) {
        String message = "User " + userEmail + " tries to create an ad:\n" + carInReview.toString();
        String email;
        List<AppUser> managers = appUserDAO.findAllByRole(Role.PLATFORM_MANAGER);

        if (!managers.isEmpty()) {
            email = managers.get(0).getEmail();
        } else {
            email = appUserDAO.findAllByRole(Role.PLATFORM_ADMIN)
                    .stream()
                    .findFirst()
                    .map(AppUser::getEmail)
                    .orElseThrow(() -> new UserException("No manager or administrator found"));
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setTo(email);
            helper.setSubject("Suspicious car advertisement");
            helper.setText(message, false);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
