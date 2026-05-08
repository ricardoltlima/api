package com.mtb.app.service;

import com.mtb.app.client.CariClient;
import com.mtb.app.entity.AccountEntity;
import com.mtb.app.entity.BankCariId;
import com.mtb.app.error.DuplicateActiveCDAException;
import com.mtb.app.mapper.CdaAccountMapper;
import com.mtb.app.model.dto.cda.CreateCdaAccountRequest;
import com.mtb.app.model.dto.cda.CreateCdaAccountResponse;
import com.mtb.app.model.dto.cda.ProvisionWalletRequest;
import com.mtb.app.model.dto.cda.ProvisionWalletResponse;
import com.mtb.app.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CdaAccountServiceTest {

    @Mock
    private CariClient cariClient;

    @Mock
    private AccountRepository accountRepository;

    private final CdaAccountMapper cdaAccountMapper = Mappers.getMapper(CdaAccountMapper.class);

    private CdaAccountService service;

    @BeforeEach
    void setUp() {
        service = new CdaAccountService(cariClient, cdaAccountMapper, accountRepository);
    }

    @Test
    void createCdaAccountProvisionsWalletPersistsBankCdaAndReturnsResponse() {
        CreateCdaAccountRequest request = validRequest();
        ProvisionWalletResponse wallet = provisionedWallet();

        when(accountRepository.findByIdBankCdaIdAndIdBankCustomerId(request.bankCdaId(), request.bankCustomerId()))
                .thenReturn(Optional.empty());
        when(cariClient.provisionWallet(any(ProvisionWalletRequest.class))).thenReturn(wallet);

        CreateCdaAccountResponse response = service.createCdaAccount(request);

        assertThat(response.cariCdaId()).isEqualTo("cda_8x9y0z1a");
        assertThat(response.bankCdaId()).isEqualTo("CDA001");
        assertThat(response.cariCustomerId()).isEqualTo("cust_7a8b9c0d");
        assertThat(response.cariWalletAddress()).isEqualTo("0x742d35Cc6634C0532925a3b8D6c0234E5aC1234D");
        assertThat(response.cariWalletStatus()).isEqualTo("active");

        ArgumentCaptor<ProvisionWalletRequest> requestCaptor = ArgumentCaptor.forClass(ProvisionWalletRequest.class);
        verify(cariClient).provisionWallet(requestCaptor.capture());
        assertThat(requestCaptor.getValue().bankCustomerId()).isEqualTo("bank-customer-1");
        assertThat(requestCaptor.getValue().bankCdaId()).startsWith("TDN");
        verify(accountRepository).findByIdBankCdaIdAndIdBankCustomerId("CDA001", "bank-customer-1");

        ArgumentCaptor<AccountEntity> entityCaptor = ArgumentCaptor.forClass(AccountEntity.class);
        verify(accountRepository).save(entityCaptor.capture());

        AccountEntity savedEntity = entityCaptor.getValue();
        assertThat(savedEntity.getId().getBankCdaId()).isEqualTo("CDA001");
        assertThat(savedEntity.getId().getBankCustomerId()).isEqualTo("bank-customer-1");
        assertThat(savedEntity.getBankDdaId()).isEqualTo("dda-1");
        assertThat(savedEntity.getInternalBankCdaId()).isEqualTo("CDA001");
        assertThat(savedEntity.getCariCustomerId()).isEqualTo("cust_7a8b9c0d");
        assertThat(savedEntity.getCariWalletAddress()).isEqualTo("0x742d35Cc6634C0532925a3b8D6c0234E5aC1234D");
        assertThat(savedEntity.getCariWalletStatus()).isEqualTo("active");
    }

    @Test
    void createCdaAccountThrowsDuplicateActiveCdaExceptionWhenWalletAlreadyExists() {
        CreateCdaAccountRequest request = validRequest();
        AccountEntity existingEntity = new AccountEntity(
                new BankCariId(request.bankCdaId(), request.bankCustomerId()),
                "cust_7a8b9c0d",
                "0x742d35Cc6634C0532925a3b8D6c0234E5aC1234D",
                "active"
        );

        when(accountRepository.findByIdBankCdaIdAndIdBankCustomerId(request.bankCdaId(), request.bankCustomerId()))
                .thenReturn(Optional.of(existingEntity));

        assertThatThrownBy(() -> service.createCdaAccount(request))
                .isInstanceOf(DuplicateActiveCDAException.class)
                .satisfies(exception -> {
                    DuplicateActiveCDAException duplicateException = (DuplicateActiveCDAException) exception;
                    assertThat(duplicateException.getError()).isEqualTo("Duplicate Active CDA");
                    assertThat(duplicateException.getCode()).isEqualTo("DUPLICATE_ACTIVE_CDA");
                    assertThat(duplicateException.getField()).isEqualTo("cariCdaId");
                    assertThat(duplicateException.getMessage()).isEqualTo("A CDA with that bank_cda_id already exists, or the customer already has an active or restricted CDA");
                });

        verify(accountRepository).findByIdBankCdaIdAndIdBankCustomerId("CDA001", "bank-customer-1");
        verify(cariClient, never()).provisionWallet(any(ProvisionWalletRequest.class));
        verify(accountRepository, never()).save(any(AccountEntity.class));
    }

    @Test
    void createCdaAccountDoesNotSaveWhenCariClientFails() {
        CreateCdaAccountRequest request = validRequest();

        when(accountRepository.findByIdBankCdaIdAndIdBankCustomerId(request.bankCdaId(), request.bankCustomerId()))
                .thenReturn(Optional.empty());
        when(cariClient.provisionWallet(any(ProvisionWalletRequest.class)))
                .thenThrow(new RestClientException("Cari wallet provisioning request failed"));

        assertThatThrownBy(() -> service.createCdaAccount(request))
                .isInstanceOf(RestClientException.class)
                .hasMessage("Cari wallet provisioning request failed");

        verify(accountRepository).findByIdBankCdaIdAndIdBankCustomerId("CDA001", "bank-customer-1");
        verify(accountRepository, never()).save(any(AccountEntity.class));
    }

    private CreateCdaAccountRequest validRequest() {
        return new CreateCdaAccountRequest(
                "bank-customer-1",
                "CDA001",
                true,
                "dda-1",
                "Acme Corp",
                "12-3456789"
        );
    }

    private ProvisionWalletResponse provisionedWallet() {
        return new ProvisionWalletResponse(
                "cda_8x9y0z1a",
                "CDA001",
                "cust_7a8b9c0d",
                "0x742d35Cc6634C0532925a3b8D6c0234E5aC1234D",
                "active"
        );
    }
}
