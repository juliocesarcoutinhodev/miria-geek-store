package br.com.miriageekstore;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class MiriaGeekApiApplication {

    public static void main(String[] args) {
        loadEnv(".env");
        loadEnv(".env.local");
        SpringApplication.run(MiriaGeekApiApplication.class, args);
    }

    private static void loadEnv(String filename) {
        boolean exists = new File(filename).exists();
        Dotenv.configure()
                .filename(filename)
                .ignoreIfMissing()
                .load()
                .entries()
                .forEach(e -> System.setProperty(e.getKey(), e.getValue()));
        System.out.printf("[ENV] %-12s %s%n", filename, exists ? "carregado" : "não encontrado (ignorado)");
    }
}
