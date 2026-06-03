package br.com.miriageekstore.catalog.domain.port.in;

import java.io.InputStream;
import java.util.UUID;

public record UploadProductImageCommand(
        UUID productId,
        InputStream inputStream,
        String contentType,
        long fileSize
) {}
