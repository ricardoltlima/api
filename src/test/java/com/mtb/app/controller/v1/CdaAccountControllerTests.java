package com.mtb.app.controller.v1;

import com.mtb.app.model.dto.cda.CreateCdaAccountRequest;
import com.mtb.app.model.dto.cda.CreateCdaAccountResponse;
import com.mtb.app.error.DuplicateActiveCDAException;
import com.mtb.app.service.CdaAccountService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CdaAccountControllerTests {

    @Mock
    private CdaAccountService cdaAccountService;

    @InjectMocks
    private CdaAccountController cdaAccountController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(cdaAccountController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void createCdaAccountReturnsCreatedResponse() throws Exception {
        CreateCdaAccountResponse response = new CreateCdaAccountResponse(
                "cari-cda-1",
                "bank-cda-1",
                "cust-generated",
                "0xwallet-generated",
                "active"
        );

        when(cdaAccountService.createCdaAccount(any(CreateCdaAccountRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/cda-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bank_customer_id": "bank-customer-1",
                                  "bank_cda_id": "bank-cda-1",
                                  "bank_dda_linked": true,
                                  "bank_dda_linked_id": "dda-1",
                                  "bank_customer_legal_name": "Acme Corp",
                                  "bank_customer_ein": "12-3456789"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cari_cda_id").value("cari-cda-1"))
                .andExpect(jsonPath("$.bank_cda_id").value("bank-cda-1"))
                .andExpect(jsonPath("$.cari_customer_id").value("cust-generated"))
                .andExpect(jsonPath("$.cari_wallet_address").value("0xwallet-generated"))
                .andExpect(jsonPath("$.cari_wallet_status").value("active"));

        ArgumentCaptor<CreateCdaAccountRequest> requestCaptor = ArgumentCaptor.forClass(CreateCdaAccountRequest.class);
        verify(cdaAccountService).createCdaAccount(requestCaptor.capture());

        CreateCdaAccountRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.bankCustomerId()).isEqualTo("bank-customer-1");
        assertThat(capturedRequest.bankCdaId()).isEqualTo("bank-cda-1");
        assertThat(capturedRequest.bankDdaLinked()).isTrue();
        assertThat(capturedRequest.bankDdaLinkedId()).isEqualTo("dda-1");
        assertThat(capturedRequest.bankCustomerLegalName()).isEqualTo("Acme Corp");
        assertThat(capturedRequest.bankCustomerEin()).isEqualTo("12-3456789");
    }

    @Test
    void createCdaAccountReturnsBadRequestWhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/cda-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bank_customer_id": "",
                                  "bank_cda_id": "",
                                  "bank_dda_linked": null,
                                  "bank_customer_ein": "invalid"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void createCdaAccountReturnsConflictWhenCdaAlreadyExists() throws Exception {
        when(cdaAccountService.createCdaAccount(any(CreateCdaAccountRequest.class)))
                .thenThrow(new DuplicateActiveCDAException(
                        "Duplicate Active CDA",
                        "DUPLICATE_ACTIVE_CDA",
                        "cariCdaId",
                        "A CDA with that bank_cda_id already exists, or the customer already has an active or restricted CDA"
                ));

        mockMvc.perform(post("/api/v1/cda-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Duplicate Active CDA"))
                .andExpect(jsonPath("$.code").value("DUPLICATE_ACTIVE_CDA"))
                .andExpect(jsonPath("$.details[0].field").value("cariCdaId"));
    }

    @Test
    void createCdaAccountReturnsBadGatewayWhenCariIsUnreachable() throws Exception {
        when(cdaAccountService.createCdaAccount(any(CreateCdaAccountRequest.class)))
                .thenThrow(new RestClientException("Cari wallet provisioning request failed"));

        mockMvc.perform(post("/api/v1/cda-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.error").value("Cari is unreachable"))
                .andExpect(jsonPath("$.code").value("SERVICE_UNAVAILABLE"));
    }

    @Test
    void createCdaAccountReturnsUnsupportedMediaTypeWhenContentTypeIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/cda-accounts")
                        .content(validRequestJson()))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.error").value("Request body sent without Content-Type: application/json"))
                .andExpect(jsonPath("$.code").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.details[0].field").value("Content-Type"));
    }

    @Test
    void createCdaAccountReturnsUnsupportedMediaTypeWhenContentTypeIsNotJson() throws Exception {
        mockMvc.perform(post("/api/v1/cda-accounts")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(validRequestJson()))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.error").value("Request body sent without Content-Type: application/json"))
                .andExpect(jsonPath("$.code").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.details[0].field").value("Content-Type"));
    }

    private String validRequestJson() {
        return """
                {
                  "bank_customer_id": "bank-customer-1",
                  "bank_cda_id": "bank-cda-1",
                  "bank_dda_linked": true,
                  "bank_dda_linked_id": "dda-1",
                  "bank_customer_legal_name": "Acme Corp",
                  "bank_customer_ein": "12-3456789"
                }
                """;
    }
}
