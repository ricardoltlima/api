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

import java.time.OffsetDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {OffsetDateTime.class, TransactionOperations.class})
public interface TransactionMapper {

    /**
     * Prepares the object to be sent to Cari.
     * The original bankCdaId won't be sent to Cari, but a generated String instead.
     *
     * @param transaction contains the request values
     * @return the request to Cari
     */
    @Mapping(target = "bankCdaId", expression = "java(generateBankCdaId())")
    @Mapping(target = "type", source = "operation")
    CariTransactionRequest toCariTransactionRequest(Transaction transaction);

    @Mapping(target = "bankCdaId", source = "transaction.bankCdaId")
    @Mapping(target = "tokenAmount", source = "transaction.tokenAmount")
    @Mapping(target = "customerId", source = "transaction.customerId")
    @Mapping(target = "transactionStatus", source = "cariTransactionResponse.odfiStatus")
    @Mapping(target = "createdAt", expression = "java(OffsetDateTime.now())")
    @Mapping(target = "internalBankCdaId", source = "cariTransactionRequest.bankCdaId")
    TransactionEntity toTransactionEntity(Transaction transaction, CariTransactionResponse cariTransactionResponse, CariTransactionRequest cariTransactionRequest);

    @Mapping(target = "operation", expression = "java(TransactionOperations.valueOf(cariTransactionResponse.type().toUpperCase()))")
    @Mapping(target = "customerId", source = "bankCustomerId")
    Transaction toCdaTransaction(CreateTransactionRequest cariTransactionResponse);

    /**
     * Receives the response from Cari and populates the endpoint return object
     *
     * @param cariTransactionResponse The response from Cari
     * @return The endpoint object
     */
    CreateTransactionResponse toCdaTransaction(CariTransactionResponse cariTransactionResponse);

    @Named("generateBankCdaId")
    default String generateBankCdaId() {
        return "TDN" + UUID.randomUUID();
    }
}
