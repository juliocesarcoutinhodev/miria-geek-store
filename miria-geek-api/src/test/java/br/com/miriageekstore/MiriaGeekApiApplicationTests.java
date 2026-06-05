package br.com.miriageekstore;

import br.com.miriageekstore.identity.domain.port.out.EmailSender;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class MiriaGeekApiApplicationTests {

    @MockitoBean
    @SuppressWarnings("rawtypes")
    KafkaTemplate kafkaTemplate;

    @MockitoBean
    EmailSender emailSender;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Test
    void contextLoads() {
    }

}
