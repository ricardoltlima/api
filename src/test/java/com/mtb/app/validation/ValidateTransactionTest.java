package com.mtb.app.validation;

import com.mtb.app.error.ValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidateTransactionTest {

    @Test
    void validateCreateTransactionHeaderAcceptsValidIdempotencyKey() {
        assertThatCode(() -> ValidateTransaction.validateCreateTransactionHeader("idem-1"))
                .doesNotThrowAnyException();
    }

    @Test
    void validateIdempotencyKeyRequiresValue() {
        assertThatThrownBy(() -> ValidateTransaction.validateIdempotencyKey(null))
                .isInstanceOf(ValidationException.class)
                .hasMessage("idempotency_key is missing")
                .extracting("error", "code", "field")
                .containsExactly("Idempotency Key Required", "IDEMPOTENCY_KEY_REQUIRED", "idempotency_key");
    }

    @Test
    void validateIdempotencyKeyRejectsBlankValue() {
        assertThatThrownBy(() -> ValidateTransaction.validateIdempotencyKey(" "))
                .isInstanceOf(ValidationException.class)
                .hasMessage("idempotency_key cannot be blank or longer than 128 characters");
    }

    @Test
    void validateIdempotencyKeyRejectsLongValue() {
        assertThatThrownBy(() -> ValidateTransaction.validateIdempotencyKey("a".repeat(129)))
                .isInstanceOf(ValidationException.class)
                .extracting("error", "code", "field")
                .containsExactly("Invalid Idempotency Key", "INVALID_IDEMPOTENCY_KEY", "idempotency_key");
    }
}
