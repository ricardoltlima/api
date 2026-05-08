package com.mtb.app.validation;

import com.mtb.app.entity.AccountEntity;
import com.mtb.app.entity.BankCariId;
import com.mtb.app.error.DuplicateActiveCDAException;
import com.mtb.app.error.ElementNotFoundException;
import com.mtb.app.model.CdaAccount;
import com.mtb.app.model.Transaction;
import com.mtb.app.repository.AccountRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidateAccountTest {

    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final ValidateAccount validateAccount = new ValidateAccount(accountRepository);

    @Test
    void validateDuplicateAccountDoesNothingWhenAccountDoesNotExist() {
        CdaAccount account = cdaAccount();
        when(accountRepository.findByIdBankCdaIdAndIdBankCustomerId("CDA001", "customer-1"))
                .thenReturn(Optional.empty());

        assertThatCode(() -> validateAccount.validateDuplicateAccount(account))
                .doesNotThrowAnyException();
    }

    @Test
    void validateDuplicateAccountThrowsWhenAccountExists() {
        CdaAccount account = cdaAccount();
        when(accountRepository.findByIdBankCdaIdAndIdBankCustomerId("CDA001", "customer-1"))
                .thenReturn(Optional.of(accountEntity()));

        assertThatThrownBy(() -> validateAccount.validateDuplicateAccount(account))
                .isInstanceOf(DuplicateActiveCDAException.class)
                .hasMessage("A CDA with that bank_cda_id already exists, or the customer already has an active or restricted CDA")
                .extracting("error", "code", "field")
                .containsExactly("Duplicate Active CDA", "DUPLICATE_ACTIVE_CDA", "cariCdaId");
    }

    @Test
    void validateExistingAccountDoesNothingWhenAccountExists() {
        Transaction transaction = transaction();
        when(accountRepository.findByIdBankCdaIdAndIdBankCustomerId("CDA001", "customer-1"))
                .thenReturn(Optional.of(accountEntity()));

        assertThatCode(() -> validateAccount.validateExistingAccount(transaction))
                .doesNotThrowAnyException();
    }

    @Test
    void validateExistingAccountThrowsWhenAccountDoesNotExist() {
        Transaction transaction = transaction();
        when(accountRepository.findByIdBankCdaIdAndIdBankCustomerId("CDA001", "customer-1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> validateAccount.validateExistingAccount(transaction))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessage("No CDA found for that bank_cda_id under your bank")
                .extracting("error", "code", "field")
                .containsExactly("No CDA Found", "CDA_NOT_FOUND", "bank_cda_id");
    }

    private CdaAccount cdaAccount() {
        CdaAccount account = new CdaAccount();
        account.setBankCdaId("CDA001");
        account.setBankCustomerId("customer-1");
        return account;
    }

    private Transaction transaction() {
        Transaction transaction = new Transaction();
        transaction.setBankCdaId("CDA001");
        transaction.setCustomerId("customer-1");
        return transaction;
    }

    private AccountEntity accountEntity() {
        return new AccountEntity(new BankCariId("CDA001", "customer-1"), "cari-customer", "wallet", "active");
    }
}
