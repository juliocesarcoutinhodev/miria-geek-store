package br.com.miriageekstore.identity.domain.model;

import br.com.miriageekstore.identity.domain.exception.UserAlreadyVerifiedException;
import br.com.miriageekstore.identity.domain.exception.VerificationTokenExpiredException;

import java.time.Instant;
import java.util.Set;

public class User {

    private final UserId id;
    private FullName name;
    private final Email email;
    private Password password;
    private UserStatus status;
    private final Set<UserRole> roles;
    private VerificationToken verificationToken;
    private final Instant createdAt;
    private final java.util.UUID createdByAdminId;

    private User(UserId id, FullName name, Email email, Password password,
                 UserStatus status, Set<UserRole> roles,
                 VerificationToken verificationToken, Instant createdAt,
                 java.util.UUID createdByAdminId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = status;
        this.roles = Set.copyOf(roles);
        this.verificationToken = verificationToken;
        this.createdAt = createdAt;
        this.createdByAdminId = createdByAdminId;
    }

    public static User register(FullName name, Email email, Password password, VerificationToken token) {
        return new User(
                UserId.generate(), name, email, password,
                UserStatus.PENDING_VERIFICATION, Set.of(UserRole.ROLE_CUSTOMER),
                token, Instant.now(), null
        );
    }

    public static User createAdmin(FullName name, Email email, Password password, UserId createdByAdminId) {
        return new User(
                UserId.generate(), name, email, password,
                UserStatus.ACTIVE, Set.of(UserRole.ROLE_ADMIN),
                null, Instant.now(), createdByAdminId.value()
        );
    }

    public static User reconstitute(UserId id, FullName name, Email email, Password password,
                                    UserStatus status, Set<UserRole> roles,
                                    VerificationToken verificationToken, Instant createdAt,
                                    java.util.UUID createdByAdminId) {
        return new User(id, name, email, password, status, roles, verificationToken, createdAt, createdByAdminId);
    }

    public void changeName(FullName newName) {
        this.name = newName;
    }

    public void changePassword(Password newPassword) {
        this.password = newPassword;
    }

    public void regenerateVerificationToken() {
        this.verificationToken = VerificationToken.generate();
    }

    public void verifyEmail() {
        if (this.status == UserStatus.ACTIVE) {
            throw new UserAlreadyVerifiedException();
        }
        if (this.verificationToken.isExpired()) {
            throw new VerificationTokenExpiredException();
        }
        this.status = UserStatus.ACTIVE;
    }

    public UserId getId() { return id; }
    public FullName getName() { return name; }
    public Email getEmail() { return email; }
    public Password getPassword() { return password; }
    public UserStatus getStatus() { return status; }
    public Set<UserRole> getRoles() { return roles; }
    public VerificationToken getVerificationToken() { return verificationToken; }
    public Instant getCreatedAt() { return createdAt; }
    public java.util.UUID getCreatedByAdminId() { return createdByAdminId; }
}
