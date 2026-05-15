package br.com.miriageekstore.identity.domain.port.out;

import java.time.Instant;
import java.util.UUID;

public interface EmailSender {
    void sendVerificationEmail(String to, String fullName, UUID verificationToken);
    void sendLoginNotificationEmail(String to, String fullName, String ipAddress, String userAgent, Instant loginTime);
}
