package com.mtb.app.mapper;

import com.mtb.app.entity.AccountEntity;
import com.mtb.app.model.CdaAccount;
import com.mtb.app.model.dto.cda.CreateCdaAccountRequest;
import com.mtb.app.model.dto.cda.CreateCdaAccountResponse;
import com.mtb.app.model.dto.cda.ProvisionWalletRequest;
import com.mtb.app.model.dto.cda.ProvisionWalletResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CdaAccountMapper {

    CdaAccount toCdaAccount(CreateCdaAccountRequest createCdaAccountRequest);

    @Mapping(target = "bankCdaId", expression = "java(generateBankCdaId())")
    ProvisionWalletRequest toProvisionWalletRequest(CdaAccount cdaAccount);

    CdaAccount toCdaAccount(ProvisionWalletResponse wallet);

    CreateCdaAccountResponse toCreateCdaAccountResponse(CdaAccount account);

    @Mapping(target = "id.bankCdaId", source = "wallet.bankCdaId")
    @Mapping(target = "id.bankCustomerId", source = "wallet.cariCustomerId")
    @Mapping(target = "cariCdaId", source = "wallet.cariCdaId")
    AccountEntity toBankCda(ProvisionWalletResponse wallet);

    @Mapping(target = "id.bankCdaId", source = "cdaAccount.bankCdaId")
    @Mapping(target = "id.bankCustomerId", source = "cdaAccount.bankCustomerId")
    @Mapping(target = "internalBankCdaId", source = "wallet.bankCdaId")
    @Mapping(target = "bankDdaId", source = "cdaAccount.bankDdaLinkedId")
    AccountEntity toBankCda(ProvisionWalletResponse wallet, CdaAccount cdaAccount);

    @Mapping(target = "bankCdaId", source = "cdaAccount.bankCdaId")
    CreateCdaAccountResponse toCreateCdaAccountResponse(CdaAccount cdaAccount, ProvisionWalletResponse wallet);

    @Named("generateBankCdaId")
    default String generateBankCdaId() {
        return "TDN" + UUID.randomUUID();
    }
}
