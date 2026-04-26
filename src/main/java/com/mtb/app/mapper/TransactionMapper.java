package com.mtb.app.mapper;

import com.mtb.app.model.CdaTransaction;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "success", constant = "true")
    @Mapping(source = "operation", target = "operation")
    CreateTransactionResponse toCreateTransactionResponse(CdaTransaction cdaTransaction);

}
