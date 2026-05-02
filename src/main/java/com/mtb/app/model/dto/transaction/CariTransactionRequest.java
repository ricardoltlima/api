package com.mtb.app.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtb.app.model.TransactionOperations;

public record CariTransactionRequest(

        @JsonProperty("type")
        TransactionOperations type,

        @JsonProperty("bank_cda_id")
        String bankCdaId,

        @JsonProperty("token_amount")
        String tokenAmount
) {
}
