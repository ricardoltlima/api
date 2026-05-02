package com.mtb.app.mapper;

import com.mtb.app.entity.BankCdaEntity;
import com.mtb.app.model.CdaAccount;
import com.mtb.app.model.dto.cda.ProvisionWalletResponse;
import com.mtb.app.model.dto.cda.CreateCdaAccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CdaAccountMapper {

    CreateCdaAccountResponse toCreateCdaAccountResponse(CdaAccount account);

    CdaAccount toCdaAccount(ProvisionWalletResponse wallet);

    @Mapping(target = "id.bankCdaId", source = "wallet.bankCdaId")
    @Mapping(target = "id.cariCdaId", source = "wallet.cariCdaId")
    BankCdaEntity toBankCda(ProvisionWalletResponse wallet);

}