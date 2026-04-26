package com.mtb.app.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateTransactionResponse(
        @JsonProperty("success")
        Boolean success,
        @JsonProperty("cari_txn_id")
        String cariTxnId,
        @JsonProperty("operation")
        String operation,
        @JsonProperty("bank_cda_id")
        String bankCdaId,
        @JsonProperty("token_amount")
        BigDecimal tokenAmount,
        @JsonProperty("status")
        String status,
        @JsonProperty("odfi_status")
        String odfiStatus,
        @JsonProperty("rdfi_status")
        String rdfiStatus
) {
}
