package com.mtb.app.controller.v1;

import com.mtb.app.error.ApiKeyException;
import com.mtb.app.error.DuplicateActiveCDAException;
import com.mtb.app.error.ElementNotFoundException;
import com.mtb.app.error.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlesValidationExceptionAsBadRequest() {
        var response = handler.handleBusinessValidationException(new ValidationException("field", "bad value"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().error()).isEqualTo("Validation failed");
        assertThat(response.getBody().details()).singleElement()
                .satisfies(detail -> {
                    assertThat(detail.field()).isEqualTo("field");
                    assertThat(detail.message()).isEqualTo("bad value");
                });
    }

    @Test
    void handlesDuplicateActiveCdaExceptionAsConflict() {
        var response = handler.handleDuplicateActiveCdaException(new DuplicateActiveCDAException("Duplicate", "DUPLICATE", "cariCdaId", "already exists"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().error()).isEqualTo("Duplicate");
        assertThat(response.getBody().code()).isEqualTo("DUPLICATE");
    }

    @Test
    void handlesApiKeyExceptionAsUnauthorized() {
        var response = handler.handleApiKeyValidationException(new ApiKeyException("SECURE_TOKEN", "missing"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().code()).isEqualTo("UNAUTHORIZED");
    }

    @Test
    void handlesElementNotFoundExceptionAsNotFound() {
        var response = handler.handleElementNotFoundException(new ElementNotFoundException("bank_cda_id", "missing"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().code()).isEqualTo("NOT_FOUND");
    }

    @Test
    void handlesRestClientExceptionAsBadGateway() {
        var response = handler.handleServiceUnavailableException(new RestClientException("down"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody().error()).isEqualTo("Cari is unreachable");
        assertThat(response.getBody().details()).isEmpty();
    }

    @Test
    void handlesGenericExceptionAsInternalServerError() {
        var response = handler.handleInternalServerError(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_SERVER_ERROR");
    }
}
