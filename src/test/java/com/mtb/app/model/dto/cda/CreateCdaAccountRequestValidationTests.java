package com.mtb.app.model.dto.cda;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateCdaAccountRequestValidationTests {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequestHasNoViolations() {
        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(validRequest());

        assertThat(violations).isEmpty();
    }

    @Test
    void validRequestAllowsOptionalFieldsToBeNull() {
        CreateCdaAccountRequest request = new CreateCdaAccountRequest(
                "bank-customer-1",
                "bank-cda-1",
                true,
                null,
                null,
                null
        );

        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void bankCustomerIdIsRequiredWhenNull() {
        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(new CreateCdaAccountRequest(
                null,
                "bank-cda-1",
                true,
                "dda-1",
                "Acme Corp",
                "12-3456789"
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCustomerId");
    }

    @Test
    void bankCustomerIdIsRequiredWhenBlank() {
        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(new CreateCdaAccountRequest(
                "   ",
                "bank-cda-1",
                true,
                "dda-1",
                "Acme Corp",
                "12-3456789"
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCustomerId");
    }

    @Test
    void bankCustomerIdCannotExceed255Characters() {
        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(new CreateCdaAccountRequest(
                repeat('a', 256),
                "bank-cda-1",
                true,
                "dda-1",
                "Acme Corp",
                "12-3456789"
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCustomerId");
    }

    @Test
    void bankCdaIdIsRequiredWhenNull() {
        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(new CreateCdaAccountRequest(
                "bank-customer-1",
                null,
                true,
                "dda-1",
                "Acme Corp",
                "12-3456789"
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCdaId");
    }

    @Test
    void bankCdaIdIsRequiredWhenBlank() {
        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(new CreateCdaAccountRequest(
                "bank-customer-1",
                "   ",
                true,
                "dda-1",
                "Acme Corp",
                "12-3456789"
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCdaId");
    }

    @Test
    void bankCdaIdCannotExceed255Characters() {
        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(new CreateCdaAccountRequest(
                "bank-customer-1",
                repeat('b', 256),
                true,
                "dda-1",
                "Acme Corp",
                "12-3456789"
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCdaId");
    }

    @Test
    void bankDdaLinkedIsRequired() {
        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(new CreateCdaAccountRequest(
                "bank-customer-1",
                "bank-cda-1",
                null,
                "dda-1",
                "Acme Corp",
                "12-3456789"
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankDdaLinked");
    }

    @Test
    void bankDdaLinkedIdCannotExceed255Characters() {
        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(new CreateCdaAccountRequest(
                "bank-customer-1",
                "bank-cda-1",
                true,
                repeat('c', 256),
                "Acme Corp",
                "12-3456789"
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankDdaLinkedId");
    }

    @Test
    void bankCustomerLegalNameCannotExceed255Characters() {
        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(new CreateCdaAccountRequest(
                "bank-customer-1",
                "bank-cda-1",
                true,
                "dda-1",
                repeat('d', 256),
                "12-3456789"
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCustomerLegalName");
    }

    @Test
    void bankCustomerEinMustMatchExpectedFormatWhenDigitsAreMissing() {
        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(new CreateCdaAccountRequest(
                "bank-customer-1",
                "bank-cda-1",
                true,
                "dda-1",
                "Acme Corp",
                "12-345678"
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCustomerEin");
    }

    @Test
    void bankCustomerEinMustMatchExpectedFormatWhenHyphenIsMissing() {
        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(new CreateCdaAccountRequest(
                "bank-customer-1",
                "bank-cda-1",
                true,
                "dda-1",
                "Acme Corp",
                "123456789"
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCustomerEin");
    }

    @Test
    void bankCustomerEinMustMatchExpectedFormatWhenAlphabeticCharactersAreUsed() {
        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(new CreateCdaAccountRequest(
                "bank-customer-1",
                "bank-cda-1",
                true,
                "dda-1",
                "Acme Corp",
                "ab-cdefghi"
        ));

        assertThat(violations).hasSize(1);
        assertThat(singleViolation(violations).getPropertyPath().toString()).isEqualTo("bankCustomerEin");
    }

    @Test
    void multipleInvalidFieldsProduceMultipleViolations() {
        Set<ConstraintViolation<CreateCdaAccountRequest>> violations = validator.validate(new CreateCdaAccountRequest(
                "",
                "",
                null,
                repeat('c', 256),
                repeat('d', 256),
                "invalid"
        ));

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder(
                        "bankCustomerId",
                        "bankCdaId",
                        "bankDdaLinked",
                        "bankDdaLinkedId",
                        "bankCustomerLegalName",
                        "bankCustomerEin"
                );
    }

    private static CreateCdaAccountRequest validRequest() {
        return new CreateCdaAccountRequest(
                "bank-customer-1",
                "bank-cda-1",
                true,
                "dda-1",
                "Acme Corp",
                "12-3456789"
        );
    }

    private static ConstraintViolation<CreateCdaAccountRequest> singleViolation(Set<ConstraintViolation<CreateCdaAccountRequest>> violations) {
        return violations.iterator().next();
    }

    private static String repeat(char character, int count) {
        return String.valueOf(character).repeat(count);
    }
}
