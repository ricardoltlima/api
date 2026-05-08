package com.mtb.app.mapper;

import com.mtb.app.entity.TransactionEntity;
import com.mtb.app.model.Transaction;
import com.mtb.app.model.TransactionOperations;
import com.mtb.app.model.dto.transaction.CariTransactionRequest;
import com.mtb.app.model.dto.transaction.CariTransactionResponse;
import com.mtb.app.model.dto.transaction.CreateTransactionRequest;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.OffsetDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {OffsetDateTime.class, TransactionOperations.class})
public interface TransactionMapper {

    /**
     * Populates the Transaction object from the original request.
     */
    @Mapping(target = "operation", expression = "java(TransactionOperations.valueOf(createTransactionRequest.type().toUpperCase()))")
    @Mapping(target = "customerId", source = "bankCustomerId")
    Transaction toCdaTransaction(CreateTransactionRequest createTransactionRequest);

    @Mapping(target = "type", source = "transaction.operation")
    @Mapping(target = "bankCdaId", source = "bankCdaId")
    @Mapping(target = "tokenAmount", source = "transaction.tokenAmount")
    CariTransactionRequest toCariTransactionRequest(Transaction transaction, String bankCdaId);

    /**
     * Creates the Transaction Entity to be persisted after Cari's response.
     */
    @Mapping(target = "bankCdaId", source = "transaction.bankCdaId")
    @Mapping(target = "tokenAmount", source = "transaction.tokenAmount")
    @Mapping(target = "customerId", source = "transaction.customerId")
    @Mapping(target = "transactionStatus", source = "cariTransactionResponse.odfiStatus")
    @Mapping(target = "createdAt", expression = "java(OffsetDateTime.now())")
    @Mapping(target = "internalBankCdaId", source = "cariTransactionRequest.bankCdaId")
    TransactionEntity toTransactionEntity(Transaction transaction, CariTransactionResponse cariTransactionResponse, CariTransactionRequest cariTransactionRequest);

    /**
     * Receives the response from Cari and populates the endpoint return object
     *
     * @param cariTransactionResponse The response from Cari
     * @return The endpoint object
     */
    CreateTransactionResponse toCdaTransaction(CariTransactionResponse cariTransactionResponse);
}
