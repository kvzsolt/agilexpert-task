package hu.agileexpert.smartos.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
public class ValidationError {

    @Singular
    private final List<CustomFieldError> fieldErrors;

    @Builder
    private ValidationError(List<CustomFieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    @Getter
    @Builder
    static class CustomFieldError {
        private final String field;
        private final String message;
    }
}
