package com.mtb.app.client;

import com.mtb.app.model.dto.cda.ProvisionWalletRequest;
import com.mtb.app.model.dto.transaction.CariTransactionRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MockResponderTest {

    private final MockResponder mockResponder = new MockResponder();

    @Test
    void returnsProvisionWalletResponseFromMatchingMockFile() {
        var response = mockResponder.getJsonResponse(new ProvisionWalletRequest(
                "customer-1", "CUST001", true, "dda-1", "Acme Corp", "12-3456789"));

        assertThat(response).isNotNull();
        assertThat(response.cariWalletStatus()).isEqualTo("active");
    }

    @Test
    void returnsDefaultProvisionWalletResponseWhenBankCdaIdDoesNotMatch() {
        var response = mockResponder.getJsonResponse(new ProvisionWalletRequest(
                "customer-1", "unknown", true, "dda-1", "Acme Corp", "12-3456789"));

        assertThat(response).isNotNull();
        assertThat(response.cariWalletStatus()).isEqualTo("closed");
    }

    @Test
    void returnsTransactionResponseByOperationType() {
        var response = mockResponder.getJsonResponse(new CariTransactionRequest("MINT", "CDA001", "1000.00"));

        assertThat(response).isNotNull();
        assertThat(response.operation()).isEqualTo("BURN");
        assertThat(response.odfiStatus()).isEqualTo("confirmed");
    }
}
