package com.mtb.app.model.dto.transaction;

import com.mtb.app.error.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateTransactionRequestValidationTests {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validMintRequestHasNoViolations() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(validMintRequest());

        assertThat(violations).isEmpty();
    }

    @Test
    void validBurnRequestHasNoViolations() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "burn",
                "bank-dda-1",
                "bank-cda-1",
                "1000.00",
                "bank-customer-1",
                null,
                null
        ));

        assertThat(violations).isEmpty();
    }

    @Test
    void validOnUsTransferRequestHasNoViolations() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "ON_US",
                "bank-dda-1",
                "bank-cda-1",
                "1000.00",
                "bank-customer-1",
                "0xwallet-destination",
                null
        ));

        assertThat(violations).isEmpty();
    }

    @Test
    void validInterbankTransferRequestHasNoViolations() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "INTERBANK",
                "bank-dda-1",
                "bank-cda-1",
                "1000.00",
                "bank-customer-1",
                "0xwallet-destination",
                "check-transaction-1"
        ));

        assertThat(violations).isEmpty();
    }

    @Test
    void typeIsRequiredWhenNull() {
        assertValidationException(
                () -> new CreateTransactionRequest(null, "bank-dda-1", "bank-cda-1", "1000.00", "bank-customer-1", null, null),
                "Invalid Transaction Type",
                "VALIDATION_ERROR",
                "type",
                "Transaction type must be mint, burn, onus-transfer or interbank-transfer"
        );
    }

    @Test
    void typeIsRequiredWhenBlank() {
        assertValidationException(
                () -> new CreateTransactionRequest(" ", "bank-dda-1", "bank-cda-1", "1000.00", "bank-customer-1", null, null),
                "Invalid Transaction Type",
                "VALIDATION_ERROR",
                "type",
                "Transaction type must be mint, burn, onus-transfer or interbank-transfer"
        );
    }

    @Test
    void typeMustBeValidTransactionOperation() {
        assertValidationException(
                () -> new CreateTransactionRequest("deposit", "bank-dda-1", "bank-cda-1", "1000.00", "bank-customer-1", null, null),
                "Invalid Transaction Type",
                "VALIDATION_ERROR",
                "type",
                "Transaction type must be mint, burn, onus-transfer or interbank-transfer"
        );
    }

    @Test
    void bankDdaIdIsRequiredWhenNull() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "mint",
                null,
                "bank-cda-1",
                "1000.00",
                "bank-customer-1",
                null,
                null
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankDdaId");
        assertThat(singleViolation(violations).getMessage()).isEqualTo("bank_dda_id is Required");
    }

    @Test
    void bankDdaIdIsRequiredWhenBlank() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "mint",
                " ",
                "bank-cda-1",
                "1000.00",
                "bank-customer-1",
                null,
                null
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankDdaId");
    }

    @Test
    void bankDdaIdCannotExceed255Characters() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "mint",
                repeat('a', 256),
                "bank-cda-1",
                "1000.00",
                "bank-customer-1",
                null,
                null
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankDdaId");
    }

    @Test
    void bankCdaIdIsRequiredWhenNull() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "mint",
                "bank-dda-1",
                null,
                "1000.00",
                "bank-customer-1",
                null,
                null
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCdaId");
        assertThat(singleViolation(violations).getMessage()).isEqualTo("bank_cda_id is Required");
    }

    @Test
    void bankCdaIdIsRequiredWhenBlank() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "mint",
                "bank-dda-1",
                " ",
                "1000.00",
                "bank-customer-1",
                null,
                null
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCdaId");
    }

    @Test
    void bankCdaIdCannotExceed255Characters() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "mint",
                "bank-dda-1",
                repeat('b', 256),
                "1000.00",
                "bank-customer-1",
                null,
                null
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCdaId");
    }

    @Test
    void tokenAmountIsRequiredWhenNull() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "mint",
                "bank-dda-1",
                "bank-cda-1",
                null,
                "bank-customer-1",
                null,
                null
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("tokenAmount");
        assertThat(singleViolation(violations).getMessage()).isEqualTo("token_amount is Required");
    }

    @Test
    void tokenAmountMustBeNonNegativeDecimalWithUpToTwoDecimalPlaces() {
        for (String amount : new String[]{"1000.555", "-100.00", "abc"}) {
            Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                    "mint",
                    "bank-dda-1",
                    "bank-cda-1",
                    amount,
                    "bank-customer-1",
                    null,
                    null
            ));

            assertThat(violations).hasSize(1);
            assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("tokenAmount");
            assertThat(singleViolation(violations).getMessage()).isEqualTo("token_amount must be a non-negative decimal with up to two decimal places");
        }
    }

    @Test
    void tokenAmountAllowsWholeNumbersAndOneOrTwoDecimals() {
        for (String amount : new String[]{"1000", "1000.5", "1000.00"}) {
            Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                    "mint",
                    "bank-dda-1",
                    "bank-cda-1",
                    amount,
                    "bank-customer-1",
                    null,
                    null
            ));

            assertThat(violations).isEmpty();
        }
    }

    @Test
    void bankCustomerIdIsRequiredWhenNull() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "mint",
                "bank-dda-1",
                "bank-cda-1",
                "1000.00",
                null,
                null,
                null
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCustomerId");
        assertThat(singleViolation(violations).getMessage()).isEqualTo("bank_customer_id is Required");
    }

    @Test
    void bankCustomerIdIsRequiredWhenBlank() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "mint",
                "bank-dda-1",
                "bank-cda-1",
                "1000.00",
                " ",
                null,
                null
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCustomerId");
    }

    @Test
    void bankCustomerIdCannotExceed255Characters() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "mint",
                "bank-dda-1",
                "bank-cda-1",
                "1000.00",
                repeat('c', 256),
                null,
                null
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCustomerId");
    }

    @Test
    void onUsTransferRequiresDestinationWalletAddress() {
        assertValidationException(
                () -> new CreateTransactionRequest("ON_US", "bank-dda-1", "bank-cda-1", "1000.00", "bank-customer-1", null, null),
                "Missing Destination Wallet Address",
                "VALIDATION_ERROR",
                "destination_wallet_address",
                "Field Destination Wallet Address is required for transfer"
        );
    }

    @Test
    void interbankTransferRequiresDestinationWalletAddress() {
        assertValidationException(
                () -> new CreateTransactionRequest("INTERBANK", "bank-dda-1", "bank-cda-1", "1000.00", "bank-customer-1", " ", "check-transaction-1"),
                "Missing Destination Wallet Address",
                "VALIDATION_ERROR",
                "destination_wallet_address",
                "Field Destination Wallet Address is required for transfer"
        );
    }

    @Test
    void interbankTransferRequiresCheckTransactionId() {
        assertValidationException(
                () -> new CreateTransactionRequest("INTERBANK", "bank-dda-1", "bank-cda-1", "1000.00", "bank-customer-1", "0xwallet-destination", null),
                "Invalid Transaction Type",
                "VALIDATION_ERROR",
                "check_transaction_id",
                "Check Transaction Id is required for interbank transfers"
        );
    }

    @Test
    void destinationWalletAddressCannotExceed255Characters() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "ON_US",
                "bank-dda-1",
                "bank-cda-1",
                "1000.00",
                "bank-customer-1",
                repeat('d', 256),
                null
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("destinationWalletAddress");
    }

    @Test
    void checkTransactionIdCannotExceed255Characters() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "INTERBANK",
                "bank-dda-1",
                "bank-cda-1",
                "1000.00",
                "bank-customer-1",
                "0xwallet-destination",
                repeat('e', 256)
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("checkTransactionId");
    }

    @Test
    void multipleInvalidFieldsProduceMultipleViolations() {
        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(new CreateTransactionRequest(
                "mint",
                "",
                "",
                "1000.555",
                "",
                repeat('d', 256),
                repeat('e', 256)
        ));

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder(
                        "bankDdaId",
                        "bankCdaId",
                        "tokenAmount",
                        "bankCustomerId",
                        "destinationWalletAddress",
                        "checkTransactionId"
                );
    }

    private static CreateTransactionRequest validMintRequest() {
        return new CreateTransactionRequest(
                "mint",
                "bank-dda-1",
                "bank-cda-1",
                "1000.00",
                "bank-customer-1",
                null,
                null
        );
    }

    private static void assertValidationException(Runnable action,
                                                  String error,
                                                  String code,
                                                  String field,
                                                  String message) {
        assertThatThrownBy(action::run)
                .isInstanceOf(ValidationException.class)
                .satisfies(exception -> {
                    ValidationException validationException = (ValidationException) exception;
                    assertThat(validationException.getError()).isEqualTo(error);
                    assertThat(validationException.getCode()).isEqualTo(code);
                    assertThat(validationException.getField()).isEqualTo(field);
                    assertThat(validationException.getMessage()).isEqualTo(message);
                });
    }

    private static ConstraintViolation<CreateTransactionRequest> singleViolation(Set<ConstraintViolation<CreateTransactionRequest>> violations) {
        return violations.iterator().next();
    }

    private static String repeat(char character, int count) {
        return String.valueOf(character).repeat(count);
    }
}
