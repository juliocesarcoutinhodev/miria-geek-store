package br.com.miriageekstore.identity.infrastructure.config;

import br.com.miriageekstore.identity.domain.model.Email;
import br.com.miriageekstore.identity.domain.model.FullName;
import br.com.miriageekstore.identity.domain.model.Password;
import br.com.miriageekstore.identity.domain.model.User;
import br.com.miriageekstore.identity.domain.model.UserId;
import br.com.miriageekstore.identity.domain.port.out.PasswordHasher;
import br.com.miriageekstore.identity.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
class AdminSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Value("${app.admin-seed.email}")
    private String email;

    @Value("${app.admin-seed.password}")
    private String password;

    @Value("${app.admin-seed.name:Super Admin}")
    private String name;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var adminEmail = Email.of(email);
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        var hashedPassword = Password.hash(password, passwordHasher);
        var admin = User.createAdmin(FullName.of(name), adminEmail, hashedPassword, UserId.generate());
        userRepository.save(admin);

        log.warn("""

                ╔══════════════════════════════════════════════════╗
                ║         ADMIN SEED — DEV ENVIRONMENT             ║
                ║                                                  ║
                ║  Email    : {}
                ║  Password : {}
                ║                                                  ║
                ║  Altere a senha após o primeiro login!           ║
                ╚══════════════════════════════════════════════════╝
                """, email, password);
    }
}
