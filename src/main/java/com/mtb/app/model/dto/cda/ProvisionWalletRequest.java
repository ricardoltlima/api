package com.mtb.app.model.dto.cda;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProvisionWalletRequest(

        @JsonProperty("bank_customer_id")
        String bankCustomerId,

        @JsonProperty("bank_cda_id")
        String bankCdaId,

        @JsonProperty("bank_dda_linked")
        Boolean bankDdaLinked,

        @JsonProperty("bank_dda_linked_id")
        String bankDdaLinkedId,

        @JsonProperty("bank_customer_legal_name")
        String bankCustomerLegalName,

        @JsonProperty("bank_customer_ein")
        String bankCustomerEin
) {
}
