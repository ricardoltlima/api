package com.mtb.app.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mtb.app.model.TransactionOperations;
import com.mtb.app.model.dto.cda.CreateCdaAccountRequest;
import com.mtb.app.model.dto.cda.ProvisionWalletResponse;
import com.mtb.app.model.dto.transaction.CariTransactionRequest;
import com.mtb.app.model.dto.transaction.CariTransactionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class MockResponder {

    private static final Logger logger = LoggerFactory.getLogger(MockResponder.class);
    private final ObjectMapper mapper = new ObjectMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());

    public ProvisionWalletResponse getJsonResponse(CreateCdaAccountRequest createCdaAccountRequest) {
        String fileName = getWalletFileName(createCdaAccountRequest);
        ClassPathResource resource = new ClassPathResource("/mock-responses/" + fileName);

        try (InputStream inputStream = resource.getInputStream()) {
            return mapper.readValue(inputStream, ProvisionWalletResponse.class);
        } catch (IOException e) {
            logger.error("Error reading Provision Wallet JSON: {}", fileName);
        }

        return null;
    }

    public CariTransactionResponse getJsonResponse(CariTransactionRequest cariTransactionRequest) {
        String fileName = getWalletFileName(cariTransactionRequest);
        ClassPathResource resource = new ClassPathResource("/mock-responses/" + fileName);

        try (InputStream inputStream = resource.getInputStream()) {
            return mapper.readValue(inputStream, CariTransactionResponse.class);
        } catch (IOException e) {
            logger.error("Error reading Wallet Transaction JSON: {}", fileName);
        }

        return null;
    }

    private String getWalletFileName(CreateCdaAccountRequest createCdaAccountRequest) {
        return switch (createCdaAccountRequest.bankCdaId()) {
            case "CDA001" -> "wallet-active.json";
            case "CDA002" -> "wallet-restricted.json";
            default -> "wallet-closed.json";
        };
    }

    private String getWalletFileName(CariTransactionRequest cariTransactionRequest) {

        TransactionOperations type = cariTransactionRequest.type();
        return switch (type) {
            case MINT -> "transaction-mint-submitted.json";
            case BURN -> "transaction-burn-confirmed.json";
            default -> "transaction-failed.json";
        };
    }
}
