package br.com.miriageekstore.catalog.domain.exception;

public class InvalidImageTypeException extends RuntimeException {

    public InvalidImageTypeException(String contentType) {
        super("Tipo de arquivo não suportado: " + contentType + ". Formatos aceitos: JPEG, PNG, WebP");
    }
}
