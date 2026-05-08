package com.mtb.app.service;

import com.mtb.app.client.CariClient;
import com.mtb.app.entity.AccountEntity;
import com.mtb.app.mapper.CdaAccountMapper;
import com.mtb.app.model.CdaAccount;
import com.mtb.app.model.dto.cda.CreateCdaAccountRequest;
import com.mtb.app.model.dto.cda.CreateCdaAccountResponse;
import com.mtb.app.model.dto.cda.ProvisionWalletRequest;
import com.mtb.app.model.dto.cda.ProvisionWalletResponse;
import com.mtb.app.repository.AccountRepository;
import com.mtb.app.validation.ValidateAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CdaAccountService {

    private static final Logger logger = LoggerFactory.getLogger(CdaAccountService.class);

    private final CariClient cariClient;
    private final CdaAccountMapper cdaAccountMapper;
    private final AccountRepository accountRepository;

    public CdaAccountService(CariClient cariClient, CdaAccountMapper cdaAccountMapper, AccountRepository accountRepository) {
        this.cariClient = cariClient;
        this.cdaAccountMapper = cdaAccountMapper;
        this.accountRepository = accountRepository;
    }

    public CreateCdaAccountResponse createCdaAccount(CreateCdaAccountRequest createCdaAccountRequest) {
        // Creates new CdaAccount Object
        CdaAccount cdaAccount = cdaAccountMapper.toCdaAccount(createCdaAccountRequest);

        // Must verify if the Account already exists for this Bank CDA id
        ValidateAccount validateAccount = new ValidateAccount(accountRepository);
        validateAccount.validateDuplicateAccount(cdaAccount);

        // Creates a request to Cari
        ProvisionWalletRequest provisionWalletRequest = cdaAccountMapper.toProvisionWalletRequest(cdaAccount);
        logger.debug("provision wallet request: {}", provisionWalletRequest);

        // Calls Cari endpoint
        ProvisionWalletResponse provisionWalletResponse = cariClient.provisionWallet(provisionWalletRequest);
        logger.debug("provision wallet response: {}", provisionWalletResponse);

        // Populates Entity with Cari Response and Persists database
        AccountEntity accountEntity = cdaAccountMapper.toBankCda(provisionWalletResponse, cdaAccount);
        accountRepository.save(accountEntity);
        logger.info("Entity persisted into database: {}", accountEntity);

        // Returns Cari Response to the Controller
        CreateCdaAccountResponse createCdaAccountResponse = cdaAccountMapper.toCreateCdaAccountResponse(cdaAccount, provisionWalletResponse);
        logger.debug("create cdaAccount response: {}", createCdaAccountResponse);

        return createCdaAccountResponse;
    }
}
