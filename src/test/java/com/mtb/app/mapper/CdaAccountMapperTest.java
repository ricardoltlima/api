package com.mtb.app.mapper;

import com.mtb.app.entity.AccountEntity;
import com.mtb.app.model.CdaAccount;
import com.mtb.app.model.dto.cda.CreateCdaAccountRequest;
import com.mtb.app.model.dto.cda.CreateCdaAccountResponse;
import com.mtb.app.model.dto.cda.ProvisionWalletRequest;
import com.mtb.app.model.dto.cda.ProvisionWalletResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class CdaAccountMapperTest {

    private final CdaAccountMapper mapper = Mappers.getMapper(CdaAccountMapper.class);

    @Test
    void mapsCreateRequestToCdaAccount() {
        CdaAccount account = mapper.toCdaAccount(request());

        assertThat(account.getBankCustomerId()).isEqualTo("customer-1");
        assertThat(account.getBankCdaId()).isEqualTo("CDA001");
        assertThat(account.getBankDdaLinked()).isTrue();
        assertThat(account.getBankDdaLinkedId()).isEqualTo("dda-1");
        assertThat(account.getBankCustomerLegalName()).isEqualTo("Acme Corp");
        assertThat(account.getBankCustomerEin()).isEqualTo("12-3456789");
    }

    @Test
    void mapsCdaAccountToProvisionWalletRequestWithGeneratedBankCdaId() {
        ProvisionWalletRequest walletRequest = mapper.toProvisionWalletRequest(cdaAccount());

        assertThat(walletRequest.bankCustomerId()).isEqualTo("customer-1");
        assertThat(walletRequest.bankCdaId()).startsWith("TDN");
        assertThat(walletRequest.bankDdaLinked()).isTrue();
        assertThat(walletRequest.bankDdaLinkedId()).isEqualTo("dda-1");
        assertThat(walletRequest.bankCustomerLegalName()).isEqualTo("Acme Corp");
        assertThat(walletRequest.bankCustomerEin()).isEqualTo("12-3456789");
    }

    @Test
    void mapsProvisionWalletResponseAndCdaAccountToEntity() {
        AccountEntity entity = mapper.toBankCda(wallet(), cdaAccount());

        assertThat(entity.getId().getBankCdaId()).isEqualTo("CDA001");
        assertThat(entity.getId().getBankCustomerId()).isEqualTo("customer-1");
        assertThat(entity.getBankDdaId()).isEqualTo("dda-1");
        assertThat(entity.getInternalBankCdaId()).isEqualTo("TDN-generated");
        assertThat(entity.getCariCustomerId()).isEqualTo("cari-customer-1");
        assertThat(entity.getCariWalletAddress()).isEqualTo("wallet-address");
        assertThat(entity.getCariWalletStatus()).isEqualTo("active");
    }

    @Test
    void mapsProvisionWalletResponseAndCdaAccountToCreateResponse() {
        CreateCdaAccountResponse response = mapper.toCreateCdaAccountResponse(cdaAccount(), wallet());

        assertThat(response.cariCdaId()).isEqualTo("cari-cda-1");
        assertThat(response.bankCdaId()).isEqualTo("CDA001");
        assertThat(response.cariCustomerId()).isEqualTo("cari-customer-1");
        assertThat(response.cariWalletAddress()).isEqualTo("wallet-address");
        assertThat(response.cariWalletStatus()).isEqualTo("active");
    }

    private CreateCdaAccountRequest request() {
        return new CreateCdaAccountRequest("customer-1", "CDA001", true, "dda-1", "Acme Corp", "12-3456789");
    }

    private CdaAccount cdaAccount() {
        return new CdaAccount("customer-1", "CDA001", true, "dda-1", "Acme Corp", "12-3456789");
    }

    private ProvisionWalletResponse wallet() {
        return new ProvisionWalletResponse("cari-cda-1", "TDN-generated", "cari-customer-1", "wallet-address", "active");
    }
}
