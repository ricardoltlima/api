package com.mtb.app.model.dto.cda;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateCdaAccountRequest(

        @NotBlank
        @Size(max = 255)
        @JsonProperty("bank_customer_id")
        String bankCustomerId,

        @NotBlank
        @Size(max = 255)
        @JsonProperty("bank_cda_id")
        String bankCdaId,

        @NotNull
        @JsonProperty("bank_dda_linked")
        Boolean bankDdaLinked,

        @Size(max = 255)
        @JsonProperty("bank_dda_linked_id")
        String bankDdaLinkedId,

        @Size(max = 255)
        @JsonProperty("bank_customer_legal_name")
        String bankCustomerLegalName,

        @Pattern(regexp = "\\d{2}-\\d{7}")
        @JsonProperty("bank_customer_ein")
        String bankCustomerEin
) {
}
