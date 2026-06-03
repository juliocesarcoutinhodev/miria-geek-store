package br.com.miriageekstore.catalog.domain.exception;

public class ImageLimitExceededException extends RuntimeException {

    public ImageLimitExceededException() {
        super("O produto já atingiu o limite máximo de 10 imagens");
    }
}
