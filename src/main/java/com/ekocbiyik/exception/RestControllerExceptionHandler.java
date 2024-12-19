package com.ekocbiyik.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class RestControllerExceptionHandler {

    public static class CustomAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) {
            throw new AccessDeniedException("accessDenied");
        }
    }

    private ResponseEntity<?> generateResponse(Exception ex, int code) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage(), code), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentialsException(Exception ex) {
        return generateResponse(ex, ExceptionStatus.BAD_CREDENTIALS_ERROR);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<?> unauthorizationException(Exception ex) {
        return generateResponse(ex, ExceptionStatus.AUTHORIZATION_ERROR);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestException(Exception ex) {
        return generateResponse(ex, ExceptionStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> commonException(Exception ex) {
        return generateResponse(ex, ExceptionStatus.INTERNAL_SERVER_ERROR);
    }

}
