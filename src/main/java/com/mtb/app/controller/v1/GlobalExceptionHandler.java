package com.mtb.app.controller.v1;

import com.mtb.app.error.ApiException;
import com.mtb.app.error.ApiKeyException;
import com.mtb.app.error.DuplicateActiveCDAException;
import com.mtb.app.error.ElementNotFoundException;
import com.mtb.app.error.ErrorResponse;
import com.mtb.app.error.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaTypeException(HttpMediaTypeNotSupportedException exception) {
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new ErrorResponse(
                        "Request body sent without Content-Type: application/json",
                        "UNSUPPORTED_MEDIA_TYPE",
                        List.of(new ErrorResponse.ErrorDetail("Content-Type", exception.getMessage()))
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleArgumentsNotValidException(MethodArgumentNotValidException exception) {
        List<ErrorResponse.ErrorDetail> errorDetails = new ArrayList<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errorDetails.add(new ErrorResponse.ErrorDetail(error.getField(), error.getDefaultMessage())));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "Validation failed",
                        "VALIDATION_ERROR",
                        errorDetails
                ));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessValidationException(ValidationException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        exception.getError(),
                        exception.getCode(),
                        List.of(new ErrorResponse.ErrorDetail(exception.getField(), exception.getMessage()))
                ));
    }

    @ExceptionHandler(DuplicateActiveCDAException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateActiveCdaException(DuplicateActiveCDAException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        exception.getError(),
                        exception.getCode(),
                        List.of(new ErrorResponse.ErrorDetail(exception.getField(), exception.getMessage()))
                ));
    }

    @ExceptionHandler(ApiKeyException.class)
    public ResponseEntity<ErrorResponse> handleApiKeyValidationException(ApiKeyException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        exception.getError(),
                        exception.getCode(),
                        List.of(new ErrorResponse.ErrorDetail(exception.getField(), exception.getMessage()))
                ));
    }

    @ExceptionHandler(ElementNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleElementNotFoundException(ElementNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        exception.getError(),
                        exception.getCode(),
                        List.of(new ErrorResponse.ErrorDetail(exception.getField(), exception.getMessage()))
                ));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        ErrorResponse body = new ErrorResponse(ex.getMessage(), ex.getCode(), List.of());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "Internal Server Error",
                        "INTERNAL_SERVER_ERROR",
                        List.of()
                ));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(RestClientException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(
                        "Cari is unreachable",
                        "SERVICE_UNAVAILABLE",
                        List.of()
                ));
    }
}
