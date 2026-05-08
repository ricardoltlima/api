package com.mtb.app.handler;

import com.mtb.app.client.CariClient;
import com.mtb.app.client.HoganClient;
import com.mtb.app.entity.AccountEntity;
import com.mtb.app.entity.BankCariId;
import com.mtb.app.entity.TransactionEntity;
import com.mtb.app.error.ElementNotFoundException;
import com.mtb.app.mapper.TransactionMapper;
import com.mtb.app.model.Transaction;
import com.mtb.app.model.TransactionOperations;
import com.mtb.app.model.dto.transaction.CariTransactionRequest;
import com.mtb.app.model.dto.transaction.CariTransactionResponse;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;
import com.mtb.app.repository.AccountRepository;
import com.mtb.app.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MintTransactionHandlerTest {

    @Mock
    private CariClient cariClient;

    @Mock
    private TransactionMapper mapper;

    @Mock
    private TransactionRepository repository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private HoganClient hoganClient;

    @Test
    void moveFundsDebitsHoganCallsCariPersistsEntityAndReturnsResponse() throws Exception {
        MintTransactionHandler handler = handler();
        Transaction transaction = mintTransaction();
        CariTransactionRequest cariRequest = new CariTransactionRequest("MINT", "TDN-generated", "1000.00");
        CariTransactionResponse cariResponse = new CariTransactionResponse("mint", "1000.00", "submitted");
        TransactionEntity transactionEntity = transactionEntity();
        CreateTransactionResponse expectedResponse = new CreateTransactionResponse("mint", "1000.00", "submitted");

        when(accountRepository.findByIdBankCdaIdAndIdBankCustomerId("bank-cda-1", "bank-customer-1"))
                .thenReturn(Optional.of(accountEntity()));
        when(mapper.toCariTransactionRequest(transaction, "TDN-generated")).thenReturn(cariRequest);
        when(cariClient.executeTransaction(cariRequest)).thenReturn(cariResponse);
        when(mapper.toTransactionEntity(transaction, cariResponse, cariRequest)).thenReturn(transactionEntity);
        when(repository.save(transactionEntity)).thenReturn(transactionEntity);
        when(mapper.toCdaTransaction(cariResponse)).thenReturn(expectedResponse);

        CreateTransactionResponse response = handler.moveFunds(transaction);

        assertThat(response).isSameAs(expectedResponse);
        verify(hoganClient).transferMoneyUsingHoganRest(any(), eq(BigInteger.valueOf(9702L)), any(Long.class), eq(new BigInteger("123456")));
        verify(cariClient).executeTransaction(cariRequest);
        verify(repository).save(transactionEntity);
    }

    @Test
    void moveFundsThrowsWhenAccountDoesNotExist() {
        MintTransactionHandler handler = handler();
        Transaction transaction = mintTransaction();

        when(accountRepository.findByIdBankCdaIdAndIdBankCustomerId("bank-cda-1", "bank-customer-1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.moveFunds(transaction))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessage("No CDA found for that bank_cda_id under your bank");

        verify(cariClient, never()).executeTransaction(any(CariTransactionRequest.class));
        verify(repository, never()).save(any(TransactionEntity.class));
    }

    @Test
    void moveFundsCreditsHoganAndThrowsWrappedExceptionWhenCariFails() throws Exception {
        MintTransactionHandler handler = handler();
        Transaction transaction = mintTransaction();
        CariTransactionRequest cariRequest = new CariTransactionRequest("MINT", "TDN-generated", "1000.00");

        when(accountRepository.findByIdBankCdaIdAndIdBankCustomerId("bank-cda-1", "bank-customer-1"))
                .thenReturn(Optional.of(accountEntity()));
        when(mapper.toCariTransactionRequest(transaction, "TDN-generated")).thenReturn(cariRequest);
        when(cariClient.executeTransaction(cariRequest)).thenThrow(new RestClientException("Cari failed"));

        assertThatThrownBy(() -> handler.moveFunds(transaction))
                .isInstanceOf(RestClientException.class)
                .hasMessage("Error trying to mint account. bank-cda-1");

        ArgumentCaptor<BigInteger> transactionCodeCaptor = ArgumentCaptor.forClass(BigInteger.class);
        verify(hoganClient, org.mockito.Mockito.times(2))
                .transferMoneyUsingHoganRest(any(), transactionCodeCaptor.capture(), any(Long.class), eq(new BigInteger("123456")));
        assertThat(transactionCodeCaptor.getAllValues()).containsExactly(BigInteger.valueOf(9702L), BigInteger.valueOf(9700L));
        verify(repository, never()).save(any(TransactionEntity.class));
    }

    private MintTransactionHandler handler() {
        return new MintTransactionHandler(cariClient, mapper, repository, accountRepository, hoganClient);
    }

    private Transaction mintTransaction() {
        Transaction transaction = new Transaction();
        transaction.setOperation(TransactionOperations.MINT);
        transaction.setBankDdaLinkedId("123456");
        transaction.setBankCdaId("bank-cda-1");
        transaction.setTokenAmount(new BigDecimal("1000.00"));
        transaction.setCustomerId("bank-customer-1");
        return transaction;
    }

    private AccountEntity accountEntity() {
        return new AccountEntity(
                new BankCariId("bank-cda-1", "bank-customer-1"),
                "123456",
                "cari-cda-1",
                "TDN-generated",
                "cari-customer-1",
                "wallet-address",
                "active"
        );
    }

    private TransactionEntity transactionEntity() {
        return new TransactionEntity(null, "123456", "bank-cda-1", "TDN-generated", new BigInteger("1000"), "bank-customer-1", "submitted", OffsetDateTime.now());
    }
}
