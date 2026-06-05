package br.com.miriageekstore.notification.infrastructure.adapter.out.mail;

import br.com.miriageekstore.order.domain.event.OrderTrackingUpdated;
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
import java.time.Year;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTrackingMailSender {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Async
    public void sendTrackingEmail(OrderTrackingUpdated event) {
        var carrierDisplay = event.carrierName() != null
                ? event.carrierName()
                : event.carrier();

        var html = loadTemplate()
                .replace("{{CUSTOMER_NAME}}",  event.customerName())
                .replace("{{ORDER_NUMBER}}",   event.orderNumber())
                .replace("{{CARRIER}}",        carrierDisplay)
                .replace("{{TRACKING_CODE}}",  event.trackingCode())
                .replace("{{TRACKING_URL}}",   event.trackingUrl())
                .replace("{{YEAR}}",           String.valueOf(Year.now().getValue()));

        send(event.customerEmail(),
                "📦 Seu pedido " + event.orderNumber() + " foi enviado!", html);
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
            log.info("Tracking email sent to={} subject={}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send tracking email to={}: {}", to, e.getMessage(), e);
        }
    }

    private String loadTemplate() {
        var path = "templates/email/order-tracking.html";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new IllegalStateException("Template not found: " + path);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load email template: " + path, e);
        }
    }
}
