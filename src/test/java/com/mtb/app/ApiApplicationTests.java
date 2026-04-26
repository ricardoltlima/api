package com.mtb.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiApplicationTests {

    @LocalServerPort
    int port;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void contextLoads() {
    }

    @Test
    void createMintSimulateWebhookAndReconcile() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> createAccount = client.send(
                post("/v1/cda-accounts", Map.of(
                        "bank_customer_id", "cust-001",
                        "bank_cda_id", "cda-001",
                        "bank_dda_linked", true,
                        "bank_dda_linked_id", "dda-001",
                        "bank_customer_legal_name", "Acme Corp",
                        "bank_customer_ein", "12-3456789"
                )).build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertThat(createAccount.statusCode()).as(createAccount.body()).isEqualTo(201);
        JsonNode account = objectMapper.readTree(createAccount.body());
        assertThat(account.get("bank_cda_id").asText()).isEqualTo("cda-001");
        assertThat(account.get("cari_wallet_status").asText()).isEqualTo("active");

        HttpResponse<String> mint = client.send(
                post("/v1/cda-accounts/cda-001/mint", Map.of("token_amount", "100.00"))
                        .header("Idempotency-Key", "idem-mint-001")
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertThat(mint.statusCode()).isBetween(200, 299);
        JsonNode mintBody = objectMapper.readTree(mint.body());
        assertThat(mintBody.get("status").asText()).isEqualTo("PENDING_CONFIRMATION");

        HttpResponse<String> confirm = client.send(
                post("/internal/simulate/webhook/" + mintBody.get("cari_txn_id").asText(), Map.of()).build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertThat(confirm.statusCode()).isBetween(200, 299);
        JsonNode confirmBody = objectMapper.readTree(confirm.body());
        assertThat(confirmBody.get("received").asBoolean()).isTrue();
        assertThat(confirmBody.get("event").asText()).isEqualTo("wallet.minted");

        HttpResponse<String> unconfirmed = client.send(
                get("/v1/reconciliation/unconfirmed-transactions"),
                HttpResponse.BodyHandlers.ofString()
        );

        assertThat(unconfirmed.statusCode()).isBetween(200, 299);
        assertThat(objectMapper.readTree(unconfirmed.body())).isEmpty();
    }

    @Test
    void validatesTokenAmountPrecision() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> createAccount = client.send(
                post("/v1/cda-accounts", Map.of(
                        "bank_customer_id", "cust-amounts",
                        "bank_cda_id", "cda-amounts",
                        "bank_dda_linked", true,
                        "bank_dda_linked_id", "dda-amounts",
                        "bank_customer_legal_name", "Amounts Inc",
                        "bank_customer_ein", "98-7654321"
                )).build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertThat(createAccount.statusCode()).as(createAccount.body()).isEqualTo(201);

        for (String amount : new String[]{"1000.00", "1000.5", "1000"}) {
            HttpResponse<String> mint = client.send(
                    post("/v1/cda-accounts/cda-amounts/mint", Map.of("token_amount", amount))
                            .header("Idempotency-Key", "idem-" + amount)
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            assertThat(mint.statusCode()).as(mint.body()).isBetween(200, 299);
            JsonNode mintBody = objectMapper.readTree(mint.body());
            HttpResponse<String> webhook = client.send(
                    post("/internal/simulate/webhook/" + mintBody.get("cari_txn_id").asText(), Map.of()).build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            assertThat(webhook.statusCode()).as(webhook.body()).isBetween(200, 299);
        }

        for (String amount : new String[]{"1000.555", "-100.00"}) {
            HttpResponse<String> mint = client.send(
                    post("/v1/cda-accounts/cda-amounts/mint", Map.of("token_amount", amount))
                            .header("Idempotency-Key", "idem-invalid-" + amount)
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            assertThat(mint.statusCode()).as(mint.body()).isEqualTo(400);
        }
    }

    @Test
    void returnsUnsupportedMediaTypeWhenJsonContentTypeIsMissing() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder(URI.create(url("/v1/cda-accounts")))
                        .header("x-api-key", "sandbox-key")
                        .POST(HttpRequest.BodyPublishers.ofString("""
                                {
                                  "bank_customer_id": "cust-no-content-type",
                                  "bank_cda_id": "cda-no-content-type",
                                  "bank_dda_linked": true
                                }
                                """))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertThat(response.statusCode()).as(response.body()).isEqualTo(415);
        JsonNode body = objectMapper.readTree(response.body());
        assertThat(body.get("code").asText()).isEqualTo("UNSUPPORTED_MEDIA_TYPE");
        assertThat(body.get("error").asText()).isEqualTo("Request body sent without Content-Type: application/json");
        assertThat(body.get("details").get(0).get("field").asText()).isEqualTo("Content-Type");
    }

    @Test
    void returnsUnauthorizedWhenApiKeyIsBlank() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder(URI.create(url("/v1/cda-accounts")))
                        .header("x-api-key", " ")
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("""
                                {
                                  "bank_customer_id": "cust-blank-key",
                                  "bank_cda_id": "cda-blank-key",
                                  "bank_dda_linked": true
                                }
                                """))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertThat(response.statusCode()).as(response.body()).isEqualTo(401);
        JsonNode body = objectMapper.readTree(response.body());
        assertThat(body.get("code").asText()).isEqualTo("UNAUTHORIZED");
        assertThat(body.get("details").get(0).get("field").asText()).isEqualTo("x-api-key");
    }

    @Test
    void returnsNotFoundForUnknownWallet() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(
                get("/v1/wallets/0xmissing"),
                HttpResponse.BodyHandlers.ofString()
        );

        assertThat(response.statusCode()).as(response.body()).isEqualTo(404);
        JsonNode body = objectMapper.readTree(response.body());
        assertThat(body.get("code").asText()).isEqualTo("NOT_FOUND");
        assertThat(body.get("details").get(0).get("field").asText()).isEqualTo("walletAddress");
    }

    private HttpRequest.Builder post(String path, Map<String, Object> body) throws Exception {
        return HttpRequest.newBuilder(URI.create(url(path)))
                .header("x-api-key", "sandbox-key")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
    }

    private HttpRequest get(String path) {
        return HttpRequest.newBuilder(URI.create(url(path)))
                .header("x-api-key", "sandbox-key")
                .GET()
                .build();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

}
