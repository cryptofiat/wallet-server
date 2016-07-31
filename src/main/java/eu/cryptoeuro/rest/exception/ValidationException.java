package eu.cryptoeuro.rest.exception;

import org.springframework.validation.Errors;

import javax.validation.constraints.NotNull;

public class ValidationException extends RuntimeException {

    @NotNull
    private Errors errors;

    public ValidationException(Errors errors) {
        this.errors = errors;
    }

    Errors getErrors() {
        return this.errors;
    }
}
