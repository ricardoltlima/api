package com.mtb.app.model;

import com.mtb.app.entity.AccountEntity;
import com.mtb.app.entity.BankCariId;
import com.mtb.app.entity.TransactionEntity;
import com.mtb.app.error.AccountServicesException;
import com.mtb.app.error.ApiKeyException;
import com.mtb.app.error.ElementNotFoundException;
import com.mtb.app.error.ErrorResponse;
import com.mtb.app.error.ValidationException;
import com.mtb.app.model.dto.transaction.FundTransferInput;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ModelAndEntityTest {

    @Test
    void bankCariIdUsesBothFieldsForEquality() {
        BankCariId id = new BankCariId("CDA001", "customer-1");

        assertThat(id).isEqualTo(new BankCariId("CDA001", "customer-1"));
        assertThat(id).isNotEqualTo(new BankCariId("CDA001", "customer-2"));
        assertThat(id.hashCode()).isEqualTo(new BankCariId("CDA001", "customer-1").hashCode());
    }

    @Test
    void accountEntityGettersAndSettersStoreValues() {
        AccountEntity entity = new AccountEntity();
        BankCariId id = new BankCariId("CDA001", "customer-1");

        entity.setId(id);
        entity.setBankDdaId("dda-1");
        entity.setCariCdaId("cari-cda-1");
        entity.setInternalBankCdaId("TDN-generated");
        entity.setCariCustomerId("cari-customer-1");
        entity.setCariWalletAddress("wallet");
        entity.setCariWalletStatus("active");

        assertThat(entity.getId()).isSameAs(id);
        assertThat(entity.getBankDdaId()).isEqualTo("dda-1");
        assertThat(entity.getCariCdaId()).isEqualTo("cari-cda-1");
        assertThat(entity.getInternalBankCdaId()).isEqualTo("TDN-generated");
        assertThat(entity.getCariCustomerId()).isEqualTo("cari-customer-1");
        assertThat(entity.getCariWalletAddress()).isEqualTo("wallet");
        assertThat(entity.getCariWalletStatus()).isEqualTo("active");
    }

    @Test
    void transactionEntityStoresValuesAndIncludesFieldsInToString() {
        OffsetDateTime createdAt = OffsetDateTime.now();
        TransactionEntity entity = new TransactionEntity(1L, "dda-1", "CDA001", "TDN-generated", new BigInteger("1000"), "customer-1", "submitted", createdAt);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getBankDdaId()).isEqualTo("dda-1");
        assertThat(entity.getBankCdaId()).isEqualTo("CDA001");
        assertThat(entity.getInternalBankCdaId()).isEqualTo("TDN-generated");
        assertThat(entity.getTokenAmount()).isEqualTo(new BigInteger("1000"));
        assertThat(entity.getCustomerId()).isEqualTo("customer-1");
        assertThat(entity.getTransactionStatus()).isEqualTo("submitted");
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
        assertThat(entity.toString()).contains("TransactionEntity", "bankCdaId=CDA001");
    }

    @Test
    void cdaAccountGettersAndSettersStoreValues() {
        CdaAccount account = new CdaAccount();
        account.setBankCustomerId("customer-1");
        account.setBankCdaId("CDA001");
        account.setBankDdaLinked(true);
        account.setBankDdaLinkedId("dda-1");
        account.setBankCustomerLegalName("Acme Corp");
        account.setBankCustomerEin("12-3456789");

        assertThat(account.getBankCustomerId()).isEqualTo("customer-1");
        assertThat(account.getBankCdaId()).isEqualTo("CDA001");
        assertThat(account.getBankDdaLinked()).isTrue();
        assertThat(account.getBankDdaLinkedId()).isEqualTo("dda-1");
        assertThat(account.getBankCustomerLegalName()).isEqualTo("Acme Corp");
        assertThat(account.getBankCustomerEin()).isEqualTo("12-3456789");
    }

    @Test
    void transactionGettersAndSettersStoreValues() {
        Transaction transaction = new Transaction();
        transaction.setOperation(TransactionOperations.INTERBANK);
        transaction.setBankDdaLinkedId("dda-1");
        transaction.setBankCdaId("CDA001");
        transaction.setTokenAmount(new BigDecimal("1000.00"));
        transaction.setCustomerId("customer-1");
        transaction.setDestinationWalletAddress("wallet");
        transaction.setCheckTransactionId("check-1");

        assertThat(transaction.getOperation()).isEqualTo(TransactionOperations.INTERBANK);
        assertThat(transaction.getBankDdaLinkedId()).isEqualTo("dda-1");
        assertThat(transaction.getBankCdaId()).isEqualTo("CDA001");
        assertThat(transaction.getTokenAmount()).isEqualByComparingTo("1000.00");
        assertThat(transaction.getCustomerId()).isEqualTo("customer-1");
        assertThat(transaction.getDestinationWalletAddress()).isEqualTo("wallet");
        assertThat(transaction.getCheckTransactionId()).isEqualTo("check-1");
    }

    @Test
    void transactionOperationsExposeWireValues() {
        assertThat(TransactionOperations.MINT.getTransactionOperation()).isEqualTo("mint");
        assertThat(TransactionOperations.BURN.getTransactionOperation()).isEqualTo("burn");
        assertThat(TransactionOperations.ON_US.getTransactionOperation()).isEqualTo("onus-transfer");
        assertThat(TransactionOperations.INTERBANK.getTransactionOperation()).isEqualTo("interbank-transfer");
    }

    @Test
    void simpleRecordsAndExceptionsExposeValues() {
        ErrorResponse response = new ErrorResponse("error", "code", List.of(new ErrorResponse.ErrorDetail("field", "message")));
        FundTransferInput transferInput = new FundTransferInput("from", "to", new BigDecimal("10.00"), Map.of("key", "value"));
        ValidationException validationException = new ValidationException("field", "message");
        ApiKeyException apiKeyException = new ApiKeyException("SECURE_TOKEN", "missing");
        ElementNotFoundException elementNotFoundException = new ElementNotFoundException("id", "missing");
        AccountServicesException accountServicesException = new AccountServicesException("failed", new RuntimeException("cause"));

        assertThat(response.details()).singleElement().extracting(ErrorResponse.ErrorDetail::field).isEqualTo("field");
        assertThat(transferInput.amount()).isEqualByComparingTo("10.00");
        assertThat(validationException.getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(apiKeyException.getCode()).isEqualTo("UNAUTHORIZED");
        assertThat(elementNotFoundException.getCode()).isEqualTo("NOT_FOUND");
        assertThat(accountServicesException).hasMessage("failed").hasCauseInstanceOf(RuntimeException.class);
    }
}
