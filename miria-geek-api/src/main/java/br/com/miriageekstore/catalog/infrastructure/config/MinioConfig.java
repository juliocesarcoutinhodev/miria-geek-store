package br.com.miriageekstore.catalog.infrastructure.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket-products}")
    private String bucketProducts;

    @Bean
    MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @EventListener(ApplicationReadyEvent.class)
    void initBuckets() {
        ensureBucketExists(bucketProducts);
    }

    private void ensureBucketExists(String bucket) {
        try {
            boolean exists = minioClient().bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient().makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("Bucket MinIO criado: {}", bucket);
            } else {
                log.debug("Bucket MinIO já existe: {}", bucket);
            }
        } catch (Exception e) {
            log.error("Falha ao verificar/criar bucket MinIO: {}", bucket, e);
        }
    }
}
