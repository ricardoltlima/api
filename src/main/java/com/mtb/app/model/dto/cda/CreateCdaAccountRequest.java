package com.mtb.app.model.dto.cda;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateCdaAccountRequest(

        @NotBlank(message = "bank_customer_id is Required")
        @Size(max = 255, message = "bank_customer_id must not exceed 255 characters")
        @JsonProperty("bank_customer_id")
        String bankCustomerId,

        @NotBlank(message = "bank_cda_id is Required")
        @Size(max = 255, message = "bank_cda_id must not exceed 255 characters")
        @JsonProperty("bank_cda_id")
        String bankCdaId,

        @NotNull(message = "bank_dda_linked is Required")
        @AssertTrue(message = "bank_dda_linked must be true - CDA accounts require a linked DDA account")
        @JsonProperty("bank_dda_linked")
        Boolean bankDdaLinked,

        @Size(max = 255, message = "bank_dda_linked_id must not exceed 255 characters")
        @JsonProperty("bank_dda_linked_id")
        String bankDdaLinkedId,

        @Size(max = 255, message = "bank_customer_legal_name must not exceed 255 characters")
        @JsonProperty("bank_customer_legal_name")
        String bankCustomerLegalName,

        @Pattern(
                regexp = "\\d{2}-\\d{7}",
                message = "bank_customer_ein must have format XX-XXXXXXX"
        )
        @JsonProperty("bank_customer_ein")
        String bankCustomerEin
) {
}
