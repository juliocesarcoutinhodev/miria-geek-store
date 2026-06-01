package br.com.miriageekstore.identity.infrastructure.adapter.out.mail;

import br.com.miriageekstore.identity.domain.port.out.EmailSender;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
class JavaMailEmailSender implements EmailSender {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy 'às' HH:mm:ss z", new Locale("pt", "BR"))
            .withZone(ZoneId.of("America/Sao_Paulo"));

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    @Async
    public void sendVerificationEmail(String to, String fullName, UUID verificationToken) {
        var verificationUrl = baseUrl + "/api/v1/auth/verify-email?token=" + verificationToken;
        var html = loadTemplate("email-verification.html")
                .replace("{{FULL_NAME}}", fullName)
                .replace("{{VERIFICATION_URL}}", verificationUrl)
                .replace("{{YEAR}}", String.valueOf(java.time.Year.now().getValue()));

        send(to, "✨ Confirme seu e-mail — Miria Geek Store", html);
    }

    @Override
    @Async
    public void sendLoginNotificationEmail(String to, String fullName,
                                           String ipAddress, String userAgent,
                                           Instant loginTime) {
        var html = loadTemplate("login-notification.html")
                .replace("{{FULL_NAME}}", fullName)
                .replace("{{LOGIN_TIME}}", FORMATTER.format(loginTime))
                .replace("{{IP_ADDRESS}}", ipAddress != null ? ipAddress : "desconhecido")
                .replace("{{USER_AGENT}}", truncate(userAgent, 120))
                .replace("{{YEAR}}", String.valueOf(java.time.Year.now().getValue()));

        send(to, "🔐 Novo acesso detectado em sua conta — Miria Geek Store", html);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String fullName, UUID resetToken) {
        var resetUrl = baseUrl + "/reset-password?token=" + resetToken;
        var html = loadTemplate("password-reset.html")
                .replace("{{FULL_NAME}}", fullName)
                .replace("{{RESET_URL}}", resetUrl)
                .replace("{{YEAR}}", String.valueOf(java.time.Year.now().getValue()));

        send(to, "🔑 Redefinição de senha — Miria Geek Store", html);
    }

    private void send(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Email sent to={} subject={}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to={} subject={}: {}", to, subject, e.getMessage(), e);
        }
    }

    private String loadTemplate(String filename) {
        var path = "templates/email/" + filename;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new IllegalStateException("Template not found: " + path);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load email template: " + path, e);
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return "desconhecido";
        return value.length() > maxLength ? value.substring(0, maxLength) + "…" : value;
    }
}
