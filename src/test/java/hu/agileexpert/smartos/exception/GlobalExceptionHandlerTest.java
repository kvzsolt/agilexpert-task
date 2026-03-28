package hu.agileexpert.smartos.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hu.agileexpert.smartos.exception.account.FieldNotAvailableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler(new StaticMessageSource());
    }

    @Test
    void handleFieldNotAvailableExceptionShouldReturnConflict() {
        ResponseEntity<ApiError> response = globalExceptionHandler.handleFieldNotAvailableException(
                new FieldNotAvailableException("Username already exists.")
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("UNAVAILABLE_FIELD_ERROR", response.getBody().getErrorCode());
    }

    @Test
    void handleAuthenticationExceptionShouldReturnUnauthorized() {
        ResponseEntity<ApiError> response = globalExceptionHandler.handleAuthenticationException(
                new BadCredentialsException("Bad credentials")
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("AUTHENTICATION_ERROR", response.getBody().getErrorCode());
    }
}
