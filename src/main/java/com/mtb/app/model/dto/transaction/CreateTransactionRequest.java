package com.mtb.app.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mtb.app.error.ValidationException;
import com.mtb.app.model.TransactionOperations;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

public record CreateTransactionRequest(

        @NotBlank(message = "type is Required")
        @JsonProperty("type")
        String type,

        @NotBlank(message = "bank_dda_id is Required")
        @Size(max = 255, message = "bank_dda_id must not exceed 255 characters")
        @JsonProperty("bank_dda_id")
        String bankDdaId,

        @NotBlank(message = "bank_cda_id is Required")
        @Size(max = 255, message = "bank_cda_id must not exceed 255 characters")
        @JsonProperty("bank_cda_id")
        String bankCdaId,

        @NotNull(message = "token_amount is Required")
        @Pattern(
                regexp = "\\d+(\\.\\d{1,2})?",
                message = "token_amount must be a non-negative decimal with up to two decimal places"
        )
        @JsonProperty("token_amount")
        String tokenAmount,

        @NotBlank(message = "bank_customer_id is Required")
        @Size(max = 255, message = "bank_customer_id must not exceed 255 characters")
        @JsonProperty("bank_customer_id")
        String bankCustomerId,

        @Size(max = 255, message = "Destination Wallet Address must not exceed 255 characters")
        @JsonProperty("destination_wallet_address")
        String destinationWalletAddress,

        @Size(max = 255)
        @JsonProperty("check_transaction_id")
        String checkTransactionId
) {
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String INVALID_TRANSACTION_TYPE = "Invalid Transaction Type";
    public static final String MISSING_DESTINATION_WALLET_ADDRESS = "Missing Destination Wallet Address";

    public CreateTransactionRequest {
        // Validate Type
        if (StringUtils.isBlank(type) || !EnumUtils.isValidEnum(TransactionOperations.class, type.toUpperCase())) {
            throw new ValidationException(INVALID_TRANSACTION_TYPE, VALIDATION_ERROR, "type", "Transaction type must be mint, burn, onus-transfer or interbank-transfer");
        }

        // Validate Destination Wallet Address
        if ((TransactionOperations.INTERBANK.name().equalsIgnoreCase(type) && StringUtils.isBlank(destinationWalletAddress))
                || (TransactionOperations.ON_US.name().equalsIgnoreCase(type)) && StringUtils.isBlank(destinationWalletAddress)) {
            throw new ValidationException(MISSING_DESTINATION_WALLET_ADDRESS, VALIDATION_ERROR, "destination_wallet_address", "Field Destination Wallet Address is required for transfer");
        }

        // Validate Check Transaction id
        if (TransactionOperations.INTERBANK.name().equalsIgnoreCase(type) && StringUtils.isBlank(checkTransactionId)) {
            throw new ValidationException(INVALID_TRANSACTION_TYPE, VALIDATION_ERROR, "check_transaction_id", "Check Transaction Id is required for interbank transfers");
        }
    }
}
