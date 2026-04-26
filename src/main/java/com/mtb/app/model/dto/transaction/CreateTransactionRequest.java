package com.mtb.app.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateTransactionRequest(

        @JsonProperty("type")
        String type,

        @Size(max = 255)
        @JsonProperty("bank_cda_id")
        String bankCdaId,

        @NotNull
        @JsonProperty("token_amount")
        String tokenAmount,

        @Size(max = 255)
        @JsonProperty("destination_wallet_address")
        String destinationWalletAddress,

        @Size(max = 255)
        @JsonProperty("check_transaction_id")
        String checkTransactionId
) {
}
