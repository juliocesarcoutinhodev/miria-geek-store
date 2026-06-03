package br.com.miriageekstore.catalog.infrastructure.adapter.out.storage;

import br.com.miriageekstore.catalog.domain.exception.StorageException;
import br.com.miriageekstore.catalog.domain.port.out.StoragePort;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
class MinioStorageAdapter implements StoragePort {

    private final MinioClient minioClient;

    @Value("${minio.public-url}")
    private String publicUrl;

    @Override
    public String uploadFile(String bucket, String filename, InputStream inputStream, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .stream(inputStream, -1, 10_485_760)
                    .contentType(contentType)
                    .build());
            var url = publicUrl + "/" + bucket + "/" + filename;
            log.debug("Arquivo enviado ao MinIO: bucket={} filename={}", bucket, filename);
            return url;
        } catch (Exception e) {
            log.error("Falha ao enviar arquivo ao MinIO: bucket={} filename={}", bucket, filename, e);
            throw new StorageException("Falha ao enviar arquivo: " + filename, e);
        }
    }

    @Override
    public void deleteFile(String bucket, String filename) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .build());
            log.debug("Arquivo removido do MinIO: bucket={} filename={}", bucket, filename);
        } catch (Exception e) {
            log.error("Falha ao remover arquivo do MinIO: bucket={} filename={}", bucket, filename, e);
            throw new StorageException("Falha ao remover arquivo: " + filename, e);
        }
    }

    @Override
    public String generatePresignedUrl(String bucket, String filename, Duration expiry) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(filename)
                    .expiry((int) expiry.getSeconds())
                    .build());
        } catch (Exception e) {
            log.error("Falha ao gerar URL pré-assinada: bucket={} filename={}", bucket, filename, e);
            throw new StorageException("Falha ao gerar URL pré-assinada: " + filename, e);
        }
    }
}
