package hu.agileexpert.smartos.exception;

import com.fasterxml.jackson.core.JsonParseException;
import hu.agileexpert.smartos.exception.account.FieldNotAvailableException;
import hu.agileexpert.smartos.exception.account.IdMismatchException;
import hu.agileexpert.smartos.exception.account.PasswordMismatchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ValidationError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("A validation error occurred: ", ex);
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return new ResponseEntity<>(processFieldErrors(fieldErrors), HttpStatus.BAD_REQUEST);
    }

    public ValidationError processFieldErrors(List<FieldError> fieldErrors) {
        return ValidationError.builder()
                .fieldErrors(fieldErrors.stream()
                        .map(fieldError -> new ValidationError.CustomFieldError(
                                fieldError.getField(),
                                messageSource.getMessage(fieldError, Locale.getDefault())))
                        .collect(Collectors.toList()))
                .build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: ", ex);
        ApiError body = new ApiError("RESOURCE_NOT_FOUND", "The requested resource was not found.", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IdMismatchException.class)
    public ResponseEntity<ApiError> handleIdMismatchException(IdMismatchException ex) {
        log.error("Id mismatch: ", ex);
        ApiError body = new ApiError("ID_MISMATCH", "Path id does not match payload id.", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ApiError> handleJsonParseException(JsonParseException ex) {
        log.error("Request JSON could not be parsed: ", ex);
        ApiError body = new ApiError("JSON_PARSE_ERROR", "The request could not be parsed as a valid JSON.", ex.getLocalizedMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument error: ", ex);
        ApiError body = new ApiError("ILLEGAL_ARGUMENT_ERROR", "An illegal argument has been passed to the method.", ex.getLocalizedMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> defaultErrorHandler(Throwable t) {
        log.error("An unexpected error occurred: ", t);
        ApiError body = new ApiError("UNCLASSIFIED_ERROR", "Oh, snap! Something really unexpected occurred.", t.getLocalizedMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiError> handlePasswordMismatchException(PasswordMismatchException ex) {
        log.error("Passwords are not matching: ", ex);
        ApiError body = new ApiError("PASSWORD_MISMATCH_ERROR", "A password mismatch error occurred.", ex.getLocalizedMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(FieldNotAvailableException.class)
    public ResponseEntity<ApiError> handleFieldNotAvailableException(FieldNotAvailableException ex) {
        log.error("Unavailable field error: ", ex);
        ApiError body = new ApiError("UNAVAILABLE_FIELD_ERROR", "An unavailable field error occurred.", ex.getMessage());
        HttpStatus status = HttpStatus.CONFLICT;
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiError> handlesAccountNotFound(AccountNotFoundException t) {
        log.error("Account not found error ", t);
        ApiError body = new ApiError("ACCOUNT_NOT_FOUND_ERROR", "No account found with username", t.getLocalizedMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication error: ", ex);
        ApiError body = new ApiError("AUTHENTICATION_ERROR", "Authentication failed.", ex.getLocalizedMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: ", ex);
        ApiError body = new ApiError("DATA_INTEGRITY_VIOLATION", "The request violates a data integrity rule.", ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
}
