package com.mtb.app.controller.v1;

import com.mtb.app.model.dto.transaction.CreateTransactionRequest;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;
import com.mtb.app.service.TransactionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionsControllerTests {

    @Mock
    private TransactionsService transactionsService;

    @InjectMocks
    private TransactionsController transactionsController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(transactionsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void createTransactionReturnsAcceptedResponse() throws Exception {
        CreateTransactionResponse response = new CreateTransactionResponse(
                "mint",
                "1000.00",
                "submitted"
        );

        when(transactionsService.createTransaction(any(CreateTransactionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/transactions")
                        .header("Idempotency-Key", "idem-transaction-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validMintRequestJson()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.operation").value("mint"))
                .andExpect(jsonPath("$.token_amount").value("1000.00"))
                .andExpect(jsonPath("$.odfi_status").value("submitted"));

        ArgumentCaptor<CreateTransactionRequest> requestCaptor = ArgumentCaptor.forClass(CreateTransactionRequest.class);
        verify(transactionsService).createTransaction(requestCaptor.capture());

        CreateTransactionRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.type()).isEqualTo("mint");
        assertThat(capturedRequest.bankDdaLinkedId()).isEqualTo("bank-dda-1");
        assertThat(capturedRequest.bankCdaId()).isEqualTo("bank-cda-1");
        assertThat(capturedRequest.tokenAmount()).isEqualTo("1000.00");
        assertThat(capturedRequest.bankCustomerId()).isEqualTo("bank-customer-1");
    }

    @Test
    void createTransactionReturnsBadRequestWhenIdempotencyKeyIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/transactions")
                        .header("Idempotency-Key", " ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validMintRequestJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Idempotency Key"))
                .andExpect(jsonPath("$.code").value("INVALID_IDEMPOTENCY_KEY"))
                .andExpect(jsonPath("$.details[0].field").value("idempotency_key"));

        verify(transactionsService, never()).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void createTransactionReturnsBadRequestWhenIdempotencyKeyIsTooLong() throws Exception {
        String idempotencyKey = "a".repeat(129);

        mockMvc.perform(post("/api/v1/transactions")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validMintRequestJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Idempotency Key"))
                .andExpect(jsonPath("$.code").value("INVALID_IDEMPOTENCY_KEY"))
                .andExpect(jsonPath("$.details[0].field").value("idempotency_key"));

        verify(transactionsService, never()).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void createTransactionReturnsBadRequestWhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/transactions")
                        .header("Idempotency-Key", "idem-transaction-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "mint",
                                  "bank_dda_linked_id": "",
                                  "bank_cda_id": "",
                                  "token_amount": "1000.555",
                                  "bank_customer_id": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray());

        verify(transactionsService, never()).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void createTransactionReturnsUnsupportedMediaTypeWhenContentTypeIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/transactions")
                        .header("Idempotency-Key", "idem-transaction-3")
                        .content(validMintRequestJson()))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.error").value("Request body sent without Content-Type: application/json"))
                .andExpect(jsonPath("$.code").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.details[0].field").value("Content-Type"));

        verify(transactionsService, never()).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void createTransactionReturnsBadGatewayWhenCariIsUnreachable() throws Exception {
        when(transactionsService.createTransaction(any(CreateTransactionRequest.class)))
                .thenThrow(new RestClientException("Cari transaction request failed"));

        mockMvc.perform(post("/api/v1/transactions")
                        .header("Idempotency-Key", "idem-transaction-4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validMintRequestJson()))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.error").value("Cari is unreachable"))
                .andExpect(jsonPath("$.code").value("SERVICE_UNAVAILABLE"));
    }

    private String validMintRequestJson() {
        return """
                {
                  "type": "mint",
                  "bank_dda_linked_id": "bank-dda-1",
                  "bank_cda_id": "bank-cda-1",
                  "token_amount": "1000.00",
                  "bank_customer_id": "bank-customer-1"
                }
                """;
    }
}
