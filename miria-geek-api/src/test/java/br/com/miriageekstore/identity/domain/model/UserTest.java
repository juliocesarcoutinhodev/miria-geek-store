package br.com.miriageekstore.identity.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void shouldRegisterUserWithPendingVerificationStatus() {
        var user = buildUser();
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);
    }

    @Test
    void shouldAssignCustomerRoleOnRegister() {
        var user = buildUser();
        assertThat(user.getRoles()).containsExactly(UserRole.ROLE_CUSTOMER);
    }

    @Test
    void shouldGenerateNonNullId() {
        var user = buildUser();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getId().value()).isNotNull();
    }

    @Test
    void shouldGenerateDifferentIdsForDifferentUsers() {
        var u1 = buildUser();
        var u2 = buildUser();
        assertThat(u1.getId()).isNotEqualTo(u2.getId());
    }

    @Test
    void shouldPreserveVerificationToken() {
        var token = VerificationToken.generate();
        var user = User.register(
                FullName.of("Test User"),
                Email.of("test@example.com"),
                Password.fromHash("$2a$12$hash"),
                token
        );
        assertThat(user.getVerificationToken().token()).isEqualTo(token.token());
    }

    @Test
    void shouldSetCreatedAtOnRegister() {
        var user = buildUser();
        assertThat(user.getCreatedAt()).isNotNull();
    }

    private User buildUser() {
        return User.register(
                FullName.of("Maria Geek"),
                Email.of("maria@geek.com"),
                Password.fromHash("$2a$12$hashvalue"),
                VerificationToken.generate()
        );
    }
}
