package br.com.miriageekstore.catalog.domain.port.out;

import java.io.InputStream;
import java.time.Duration;

public interface StoragePort {

    String uploadFile(String bucket, String filename, InputStream inputStream, String contentType);

    void deleteFile(String bucket, String filename);

    String generatePresignedUrl(String bucket, String filename, Duration expiry);
}
