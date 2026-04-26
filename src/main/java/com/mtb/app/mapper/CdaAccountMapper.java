package com.mtb.app.mapper;

import com.mtb.app.model.CdaAccount;
import com.mtb.app.model.dto.cda.CreateCdaAccountResponse;
import com.mtb.app.model.dto.cda.UpdateCdaStateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CdaAccountMapper {

    @Mapping(source = "cariCdaId", target = "cdaId")
    @Mapping(source = "walletAddress", target = "cariWalletAddress")
    @Mapping(source = "walletStatus", target = "cariWalletStatus")
    CreateCdaAccountResponse toCreateCdaAccountResponse(CdaAccount account);

    @Mapping(source = "state", target = "cdaState")
    UpdateCdaStateResponse toUpdateCdaStateResponse(CdaAccount account);
}
