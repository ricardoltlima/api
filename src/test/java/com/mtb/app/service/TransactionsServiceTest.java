package com.mtb.app.service;

import com.mtb.app.handler.TransactionFactory;
import com.mtb.app.handler.TransactionHandler;
import com.mtb.app.mapper.TransactionMapper;
import com.mtb.app.model.Transaction;
import com.mtb.app.model.TransactionOperations;
import com.mtb.app.model.dto.transaction.CreateTransactionRequest;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;
import com.mtb.app.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionsServiceTest {

    @Mock
    private TransactionFactory transactionFactory;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private TransactionHandler transactionHandler;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionsService transactionsService;

    @Test
    void createTransactionMapsRequestGetsHandlerAndReturnsHandlerResponse() {
        CreateTransactionRequest request = validMintRequest();
        Transaction transaction = mintTransaction();
        CreateTransactionResponse expectedResponse = new CreateTransactionResponse(
                "mint",
                "1000.00",
                "submitted"
        );

        when(transactionMapper.toCdaTransaction(request)).thenReturn(transaction);
        when(accountRepository.findByIdBankCdaIdAndIdBankCustomerId(transaction.getBankCdaId(), transaction.getCustomerId()))
                .thenReturn(Optional.of(org.mockito.Mockito.mock(com.mtb.app.entity.AccountEntity.class)));
        when(transactionFactory.getTransactionType(transaction)).thenReturn(transactionHandler);
        when(transactionHandler.moveFunds(transaction)).thenReturn(expectedResponse);

        CreateTransactionResponse response = transactionsService.createTransaction(request);

        assertThat(response).isSameAs(expectedResponse);
        assertThat(response.operation()).isEqualTo("mint");
        assertThat(response.tokenAmount()).isEqualTo("1000.00");
        assertThat(response.odfiStatus()).isEqualTo("submitted");

        verify(transactionMapper).toCdaTransaction(request);
        verify(accountRepository).findByIdBankCdaIdAndIdBankCustomerId("bank-cda-1", "bank-customer-1");
        verify(transactionFactory).getTransactionType(transaction);
        verify(transactionHandler).moveFunds(transaction);
    }

    @Test
    void createTransactionDoesNotCallFactoryWhenMappingFails() {
        CreateTransactionRequest request = validMintRequest();
        RuntimeException mappingException = new IllegalArgumentException("Invalid transaction");

        when(transactionMapper.toCdaTransaction(request)).thenThrow(mappingException);

        assertThatThrownBy(() -> transactionsService.createTransaction(request))
                .isSameAs(mappingException);

        verify(transactionMapper).toCdaTransaction(request);
        verify(transactionFactory, never()).getTransactionType(org.mockito.ArgumentMatchers.any(Transaction.class));
        verify(transactionHandler, never()).moveFunds(org.mockito.ArgumentMatchers.any(Transaction.class));
    }

    @Test
    void createTransactionDoesNotMoveFundsWhenFactoryFails() {
        CreateTransactionRequest request = validMintRequest();
        Transaction transaction = mintTransaction();
        RuntimeException factoryException = new IllegalStateException("Unsupported transaction type");

        when(transactionMapper.toCdaTransaction(request)).thenReturn(transaction);
        when(accountRepository.findByIdBankCdaIdAndIdBankCustomerId(transaction.getBankCdaId(), transaction.getCustomerId()))
                .thenReturn(Optional.of(org.mockito.Mockito.mock(com.mtb.app.entity.AccountEntity.class)));
        when(transactionFactory.getTransactionType(transaction)).thenThrow(factoryException);

        assertThatThrownBy(() -> transactionsService.createTransaction(request))
                .isSameAs(factoryException);

        verify(transactionMapper).toCdaTransaction(request);
        verify(accountRepository).findByIdBankCdaIdAndIdBankCustomerId("bank-cda-1", "bank-customer-1");
        verify(transactionFactory).getTransactionType(transaction);
        verify(transactionHandler, never()).moveFunds(org.mockito.ArgumentMatchers.any(Transaction.class));
    }

    @Test
    void createTransactionPropagatesHandlerFailure() {
        CreateTransactionRequest request = validMintRequest();
        Transaction transaction = mintTransaction();
        RestClientException handlerException = new RestClientException("Cari transaction request failed");

        when(transactionMapper.toCdaTransaction(request)).thenReturn(transaction);
        when(accountRepository.findByIdBankCdaIdAndIdBankCustomerId(transaction.getBankCdaId(), transaction.getCustomerId()))
                .thenReturn(Optional.of(org.mockito.Mockito.mock(com.mtb.app.entity.AccountEntity.class)));
        when(transactionFactory.getTransactionType(transaction)).thenReturn(transactionHandler);
        when(transactionHandler.moveFunds(transaction)).thenThrow(handlerException);

        assertThatThrownBy(() -> transactionsService.createTransaction(request))
                .isSameAs(handlerException);

        verify(transactionMapper).toCdaTransaction(request);
        verify(accountRepository).findByIdBankCdaIdAndIdBankCustomerId("bank-cda-1", "bank-customer-1");
        verify(transactionFactory).getTransactionType(transaction);
        verify(transactionHandler).moveFunds(transaction);
    }

    private CreateTransactionRequest validMintRequest() {
        return new CreateTransactionRequest(
                "mint",
                "bank-dda-1",
                "bank-cda-1",
                "1000.00",
                "bank-customer-1",
                null,
                null
        );
    }

    private Transaction mintTransaction() {
        Transaction transaction = new Transaction();
        transaction.setOperation(TransactionOperations.MINT);
        transaction.setBankDdaLinkedId("bank-dda-1");
        transaction.setBankCdaId("bank-cda-1");
        transaction.setTokenAmount(new BigDecimal("1000.00"));
        transaction.setCustomerId("bank-customer-1");
        return transaction;
    }
}
