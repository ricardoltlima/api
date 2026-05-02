package com.mtb.app.validation;

import com.mtb.app.error.ValidationException;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateTransaction {

    private static final Logger logger = LoggerFactory.getLogger(ValidateTransaction.class);

    private ValidateTransaction() {
    }

    public static void validateCreateTransactionHeader(String idempotencyKey) {

        logger.info("Validating transaction header");

        // Idempotency-Key must not be empty or longer than 128 characters
        validateIdempotencyKey(idempotencyKey);
    }

    public static void validateIdempotencyKey(String idempotencyKey) {

        if (StringUtils.isEmpty(idempotencyKey)) {
            throw new ValidationException("Idempotency Key Required", "IDEMPOTENCY_KEY_REQUIRED", "idempotency_key", "idempotency_key is missing");
        }

        if (StringUtils.isBlank(idempotencyKey) || idempotencyKey.length() > 128) {
            throw new ValidationException("Invalid Idempotency Key", "INVALID_IDEMPOTENCY_KEY", "idempotency_key", "idempotency_key cannot be blank or longer than 128 characters");
        }
    }
}
