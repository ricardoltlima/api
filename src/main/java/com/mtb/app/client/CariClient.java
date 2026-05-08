package com.mtb.app.client;

import com.mtb.app.model.dto.cda.ProvisionWalletRequest;
import com.mtb.app.model.dto.cda.ProvisionWalletResponse;
import com.mtb.app.model.dto.transaction.CariTransactionRequest;
import com.mtb.app.model.dto.transaction.CariTransactionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class CariClient {

    private static final Logger LOG = LoggerFactory.getLogger(CariClient.class);
    public static final String LIVE_MODE = "Cari Client running in Live mode";
    public static final String MOCK_MODE = "Cari Client running in MOCK mode";
    public static final String ERROR_TRYING_TO_CONNECT_TO_CARI = "Error trying to connect to Cari: {}";

    private final RestClient restClient;
    private final boolean mockEnabled;
    private final String cariApiKey;
    private final MockResponder mockResponder;

    public CariClient(RestClient restClient,
                      @Value("${cari.wallet.mock-enabled:true}") boolean mockEnabled,
                      @Value("${cari.base-url:http://localhost:8080}") String cariBaseUrl,
                      @Value("${cari.x-api-key}") String cariApiKey,
                      MockResponder mockResponder) {

        this.mockEnabled = mockEnabled;
        this.cariApiKey = cariApiKey;
        this.mockResponder = mockResponder;

        this.restClient = restClient
                .mutate()
                .baseUrl(cariBaseUrl)
                .build();
    }

    public ProvisionWalletResponse provisionWallet(ProvisionWalletRequest provisionWalletRequest) {
        if (mockEnabled) {
            LOG.info(MOCK_MODE);
            return mockResponder.getJsonResponse(provisionWalletRequest);
        }

        try {
            LOG.info(LIVE_MODE);

            HttpHeaders walletHeader = createHeaders();

            return restClient.post().uri("/v1/cda-accounts")
                    .headers(headers -> headers.addAll(walletHeader))
                    .body(provisionWalletRequest)
                    .retrieve()
                    .body(ProvisionWalletResponse.class);
        } catch (RestClientException ex) {
            LOG.error(ERROR_TRYING_TO_CONNECT_TO_CARI, ex.getMessage());
            throw new RestClientException("Cari wallet provisioning request failed");
        }
    }

    public CariTransactionResponse executeTransaction(CariTransactionRequest cariTransactionRequest) {
        try {
            LOG.info(LIVE_MODE);

            HttpHeaders walletHeader = createHeaders();

            return restClient.post().uri("/v1/transactions")
                    .headers(headers -> headers.addAll(walletHeader))
                    .body(cariTransactionRequest)
                    .retrieve()
                    .body(CariTransactionResponse.class);
        } catch (RestClientException ex) {
            LOG.error(ERROR_TRYING_TO_CONNECT_TO_CARI, ex.getMessage());
            throw new RestClientException("Cari wallet provisioning request failed");
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders walletHeader = new HttpHeaders();
        walletHeader.setContentType(MediaType.APPLICATION_JSON);
        walletHeader.set("x-api-key", cariApiKey);
        return walletHeader;
    }
}
