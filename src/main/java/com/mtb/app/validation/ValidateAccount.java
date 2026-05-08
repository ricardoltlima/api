package com.mtb.app.validation;

import com.mtb.app.entity.AccountEntity;
import com.mtb.app.error.DuplicateActiveCDAException;
import com.mtb.app.error.ElementNotFoundException;
import com.mtb.app.model.CdaAccount;
import com.mtb.app.model.Transaction;
import com.mtb.app.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ValidateAccount {

    private static final Logger logger = LoggerFactory.getLogger(ValidateAccount.class);
    private final AccountRepository accountRepository;

    public ValidateAccount(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void validateDuplicateAccount(CdaAccount cdaAccount) {
        Optional<AccountEntity> accountNumber = accountRepository.findByIdBankCdaIdAndIdBankCustomerId(cdaAccount.getBankCdaId(), cdaAccount.getBankCustomerId());

        if (accountNumber.isPresent()) {
            logger.error("CDA already onboarded for bank_cda_id: {}. Use a different bank_cda_id or query the existing CDA.", cdaAccount.getBankCdaId());
            throw new DuplicateActiveCDAException("Duplicate Active CDA", "DUPLICATE_ACTIVE_CDA", "cariCdaId", "A CDA with that bank_cda_id already exists, or the customer already has an active or restricted CDA");
        }
    }

    public void validateExistingAccount(Transaction transaction) {
        Optional<AccountEntity> accountNumber = accountRepository.findByIdBankCdaIdAndIdBankCustomerId(transaction.getBankCdaId(), transaction.getCustomerId());

        if (accountNumber.isEmpty()) {
            logger.error("No CDA found for bank_cda_id {}.", transaction.getBankCdaId());
            throw new ElementNotFoundException("No CDA Found", "CDA_NOT_FOUND", "bank_cda_id", "No CDA found for that bank_cda_id under your bank");
        }
    }
}
