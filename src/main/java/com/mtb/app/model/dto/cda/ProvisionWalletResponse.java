package com.mtb.app.model.dto.cda;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProvisionWalletResponse(

        @JsonProperty("cari_cda_id")
        String cariCdaId,

        @JsonProperty("bank_cda_id")
        String bankCdaId,

        @JsonProperty("cari_customer_id")
        String cariCustomerId,

        @JsonProperty("cari_wallet_address")
        String cariWalletAddress,

        @JsonProperty("cari_wallet_status")
        String cariWalletStatus
) {
}
