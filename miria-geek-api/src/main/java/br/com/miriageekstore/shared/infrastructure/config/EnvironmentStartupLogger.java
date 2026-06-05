package br.com.miriageekstore.shared.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnvironmentStartupLogger {

    private final Environment env;

    @EventListener(ApplicationReadyEvent.class)
    public void logStartupInfo() {
        String profiles = Arrays.toString(env.getActiveProfiles());
        log.info("╔══════════════════════════════════════════════╗");
        log.info("║  Perfil ativo : {}",      profiles);
        log.info("║  Mail         : {}:{}", env.getProperty("spring.mail.host"), env.getProperty("spring.mail.port"));
        log.info("║  DB           : {}",    env.getProperty("spring.datasource.url"));
        log.info("║  MinIO        : {}",    env.getProperty("minio.endpoint"));
        log.info("╚══════════════════════════════════════════════╝");
    }
}
