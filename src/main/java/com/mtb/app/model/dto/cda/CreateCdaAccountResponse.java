package com.mtb.app.model.dto.cda;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.OffsetDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateCdaAccountResponse(
        @JsonProperty("cda_id")
        String cdaId,
        @JsonProperty("bank_id")
        String bankId,
        @JsonProperty("cari_customer_id")
        String cariCustomerId,
        @JsonProperty("bank_cda_id")
        String bankCdaId,
        @JsonProperty("bank_customer_id")
        String bankCustomerId,
        @JsonProperty("cari_wallet_address")
        String cariWalletAddress,
        @JsonProperty("cari_wallet_status")
        String cariWalletStatus,
        @JsonProperty("created_at")
        OffsetDateTime createdAt
) {
}
