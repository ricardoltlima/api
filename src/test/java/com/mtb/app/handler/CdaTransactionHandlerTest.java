package com.mtb.app.handler;

import com.mtb.app.client.CariClient;
import com.mtb.app.entity.TransactionEntity;
import com.mtb.app.mapper.TransactionMapper;
import com.mtb.app.model.Transaction;
import com.mtb.app.model.TransactionOperations;
import com.mtb.app.model.dto.transaction.CariTransactionRequest;
import com.mtb.app.model.dto.transaction.CariTransactionResponse;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;
import com.mtb.app.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CdaTransactionHandlerTest {

    @Mock
    private CariClient cariClient;

    @Mock
    private TransactionMapper mapper;

    @Mock
    private TransactionRepository repository;

    @InjectMocks
    private CdaTransactionHandler handler;

    @Test
    void moveFundsCreatesCariRequestCallsCariPersistsEntityAndReturnsResponse() {
        Transaction transaction = mintTransaction();
        CariTransactionRequest cariRequest = new CariTransactionRequest(
                TransactionOperations.MINT,
                "TDN-generated",
                "1000.00"
        );
        CariTransactionResponse cariResponse = new CariTransactionResponse(
                "mint",
                "1000.00",
                "submitted"
        );
        TransactionEntity transactionEntity = transactionEntity();
        CreateTransactionResponse expectedResponse = new CreateTransactionResponse(
                "mint",
                "1000.00",
                "submitted"
        );

        when(mapper.toCariTransactionRequest(transaction)).thenReturn(cariRequest);
        when(cariClient.executeTransaction(cariRequest)).thenReturn(cariResponse);
        when(mapper.toTransactionEntity(transaction, cariResponse, cariRequest)).thenReturn(transactionEntity);
        when(repository.save(transactionEntity)).thenReturn(transactionEntity);
        when(mapper.toCdaTransaction(cariResponse)).thenReturn(expectedResponse);

        CreateTransactionResponse response = handler.moveFunds(transaction);

        assertThat(response).isSameAs(expectedResponse);
        assertThat(response.operation()).isEqualTo("mint");
        assertThat(response.tokenAmount()).isEqualTo("1000.00");
        assertThat(response.odfiStatus()).isEqualTo("submitted");

        verify(mapper).toCariTransactionRequest(transaction);
        verify(cariClient).executeTransaction(cariRequest);
        verify(mapper).toTransactionEntity(transaction, cariResponse, cariRequest);
        verify(repository).save(transactionEntity);
        verify(mapper).toCdaTransaction(cariResponse);
    }

    @Test
    void moveFundsDoesNotCallCariWhenRequestMappingFails() {
        Transaction transaction = mintTransaction();
        RuntimeException mappingException = new IllegalArgumentException("Invalid transaction");

        when(mapper.toCariTransactionRequest(transaction)).thenThrow(mappingException);

        assertThatThrownBy(() -> handler.moveFunds(transaction))
                .isSameAs(mappingException);

        verify(cariClient, never()).executeTransaction(org.mockito.ArgumentMatchers.any(CariTransactionRequest.class));
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any(TransactionEntity.class));
        verify(mapper, never()).toCdaTransaction(org.mockito.ArgumentMatchers.any(CariTransactionResponse.class));
    }

    @Test
    void moveFundsDoesNotPersistWhenCariCallFails() {
        Transaction transaction = mintTransaction();
        CariTransactionRequest cariRequest = new CariTransactionRequest(
                TransactionOperations.MINT,
                "TDN-generated",
                "1000.00"
        );
        RestClientException cariException = new RestClientException("Cari transaction request failed");

        when(mapper.toCariTransactionRequest(transaction)).thenReturn(cariRequest);
        when(cariClient.executeTransaction(cariRequest)).thenThrow(cariException);

        assertThatThrownBy(() -> handler.moveFunds(transaction))
                .isSameAs(cariException);

        verify(mapper).toCariTransactionRequest(transaction);
        verify(cariClient).executeTransaction(cariRequest);
        verify(mapper, never()).toTransactionEntity(
                org.mockito.ArgumentMatchers.any(Transaction.class),
                org.mockito.ArgumentMatchers.any(CariTransactionResponse.class),
                org.mockito.ArgumentMatchers.any(CariTransactionRequest.class)
        );
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any(TransactionEntity.class));
        verify(mapper, never()).toCdaTransaction(org.mockito.ArgumentMatchers.any(CariTransactionResponse.class));
    }

    @Test
    void moveFundsDoesNotMapResponseWhenPersistenceFails() {
        Transaction transaction = mintTransaction();
        CariTransactionRequest cariRequest = new CariTransactionRequest(
                TransactionOperations.MINT,
                "TDN-generated",
                "1000.00"
        );
        CariTransactionResponse cariResponse = new CariTransactionResponse(
                "mint",
                "1000.00",
                "submitted"
        );
        TransactionEntity transactionEntity = transactionEntity();
        RuntimeException repositoryException = new IllegalStateException("Database save failed");

        when(mapper.toCariTransactionRequest(transaction)).thenReturn(cariRequest);
        when(cariClient.executeTransaction(cariRequest)).thenReturn(cariResponse);
        when(mapper.toTransactionEntity(transaction, cariResponse, cariRequest)).thenReturn(transactionEntity);
        when(repository.save(transactionEntity)).thenThrow(repositoryException);

        assertThatThrownBy(() -> handler.moveFunds(transaction))
                .isSameAs(repositoryException);

        verify(mapper).toCariTransactionRequest(transaction);
        verify(cariClient).executeTransaction(cariRequest);
        verify(mapper).toTransactionEntity(transaction, cariResponse, cariRequest);
        verify(repository).save(transactionEntity);
        verify(mapper, never()).toCdaTransaction(cariResponse);
    }

    private Transaction mintTransaction() {
        Transaction transaction = new Transaction();
        transaction.setOperation(TransactionOperations.MINT);
        transaction.setBankDdaId("bank-dda-1");
        transaction.setBankCdaId("bank-cda-1");
        transaction.setTokenAmount(new BigDecimal("1000.00"));
        transaction.setCustomerId("bank-customer-1");
        return transaction;
    }

    private TransactionEntity transactionEntity() {
        return new TransactionEntity(
                null,
                "bank-dda-1",
                "bank-cda-1",
                "TDN-generated",
                new BigInteger("1000"),
                "bank-customer-1",
                "submitted",
                OffsetDateTime.now()
        );
    }
}
