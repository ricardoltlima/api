package com.mtb.app.mapper;

import com.mtb.app.entity.TransactionEntity;
import com.mtb.app.model.Transaction;
import com.mtb.app.model.TransactionOperations;
import com.mtb.app.model.dto.transaction.CariTransactionRequest;
import com.mtb.app.model.dto.transaction.CariTransactionResponse;
import com.mtb.app.model.dto.transaction.CreateTransactionRequest;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {

    private final TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);

    @Test
    void mapsCreateTransactionRequestToTransaction() {
        Transaction transaction = mapper.toCdaTransaction(new CreateTransactionRequest(
                "mint", "dda-1", "CDA001", "1000.50", "customer-1", null, null));

        assertThat(transaction.getOperation()).isEqualTo(TransactionOperations.MINT);
        assertThat(transaction.getBankDdaLinkedId()).isEqualTo("dda-1");
        assertThat(transaction.getBankCdaId()).isEqualTo("CDA001");
        assertThat(transaction.getTokenAmount()).isEqualByComparingTo("1000.50");
        assertThat(transaction.getCustomerId()).isEqualTo("customer-1");
    }

    @Test
    void mapsTransactionToCariTransactionRequest() {
        Transaction transaction = transaction();

        CariTransactionRequest request = mapper.toCariTransactionRequest(transaction, "TDN-generated");

        assertThat(request.type()).isEqualTo("MINT");
        assertThat(request.bankCdaId()).isEqualTo("TDN-generated");
        assertThat(request.tokenAmount()).isEqualTo("1000.00");
    }

    @Test
    void mapsTransactionToTransactionEntity() {
        TransactionEntity entity = mapper.toTransactionEntity(
                transaction(),
                new CariTransactionResponse("mint", "1000.00", "submitted"),
                new CariTransactionRequest("MINT", "TDN-generated", "1000.00")
        );

        assertThat(entity.getBankCdaId()).isEqualTo("CDA001");
        assertThat(entity.getTokenAmount()).isEqualTo(new BigInteger("1000"));
        assertThat(entity.getCustomerId()).isEqualTo("customer-1");
        assertThat(entity.getTransactionStatus()).isEqualTo("submitted");
        assertThat(entity.getInternalBankCdaId()).isEqualTo("TDN-generated");
        assertThat(entity.getCreatedAt()).isNotNull();
    }

    @Test
    void mapsCariTransactionResponseToCreateTransactionResponse() {
        CreateTransactionResponse response = mapper.toCdaTransaction(new CariTransactionResponse("mint", "1000.00", "submitted"));

        assertThat(response.operation()).isEqualTo("mint");
        assertThat(response.tokenAmount()).isEqualTo("1000.00");
        assertThat(response.odfiStatus()).isEqualTo("submitted");
    }

    private Transaction transaction() {
        Transaction transaction = new Transaction();
        transaction.setOperation(TransactionOperations.MINT);
        transaction.setBankDdaLinkedId("dda-1");
        transaction.setBankCdaId("CDA001");
        transaction.setTokenAmount(new BigDecimal("1000.00"));
        transaction.setCustomerId("customer-1");
        return transaction;
    }
}
