package com.mtb.app.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CariTransactionResponse(

        @JsonProperty("operation")
        String operation,

        @JsonProperty("token_amount")
        String tokenAmount,

        @JsonProperty("odfi_status")
        String odfiStatus
) {

    public String type() {
        return operation;
    }
}
