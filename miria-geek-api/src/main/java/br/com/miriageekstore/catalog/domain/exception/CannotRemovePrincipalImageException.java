package br.com.miriageekstore.catalog.domain.exception;

public class CannotRemovePrincipalImageException extends RuntimeException {

    public CannotRemovePrincipalImageException() {
        super("Não é possível remover a imagem principal enquanto houver outras imagens. Defina outra imagem como principal antes de remover esta.");
    }
}
