package com.mtb.app.service;

import com.mtb.app.client.WalletProvisionClient;
import com.mtb.app.error.ApiException;
import com.mtb.app.error.ValidationException;
import com.mtb.app.model.CdaAccount;
import com.mtb.app.model.dto.ProvisionWalletResponse;
import com.mtb.app.model.dto.cda.CreateCdaAccountRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CdaAccountService {

    private final WalletProvisionClient walletProvisionClient;
    private final Map<String, CdaAccount> accountsByBankCdaId = new ConcurrentHashMap<>();

    public CdaAccountService(WalletProvisionClient walletProvisionClient) {
        this.walletProvisionClient = walletProvisionClient;
    }

    public CdaAccount createCdaAccount(CreateCdaAccountRequest createCdaAccountRequest, String bankId) {
        if (accountsByBankCdaId.containsKey(createCdaAccountRequest.bankCdaId())) {
            throw new ApiException(HttpStatus.CONFLICT, "DUPLICATE_CDA", "CDA account already exists");
        }

        ProvisionWalletResponse wallet = walletProvisionClient.provisionWallet(createCdaAccountRequest, bankId);
        if (wallet == null || wallet.data() == null) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "WALLET_PROVISIONING_FAILED", "Wallet provisioning returned an empty response");
        }
        if (!StringUtils.equals(wallet.data().bankCdaId(), createCdaAccountRequest.bankCdaId())) {
            throw new ValidationException("bank_cda_id", "Provisioned wallet bank_cda_id does not match the request");
        }

        CdaAccount account = new CdaAccount();
        account.setCariCdaId(wallet.data().cdaId());
        account.setBankId(bankId);
        account.setBankCustomerId(createCdaAccountRequest.bankCustomerId());
        account.setBankCdaId(createCdaAccountRequest.bankCdaId());
        account.setCariCustomerId(wallet.data().cariCustomerId());
        account.setWalletAddress(wallet.data().cariWalletAddress());
        account.setWalletStatus(wallet.data().walletStatus());
        account.setBankDdaLinked(createCdaAccountRequest.bankDdaLinked());
        account.setBankDdaLinkedId(createCdaAccountRequest.bankDdaLinkedId());
        account.setBankCustomerLegalName(createCdaAccountRequest.bankCustomerLegalName());
        account.setBankCustomerEin(createCdaAccountRequest.bankCustomerEin());
        account.setState("active");
        accountsByBankCdaId.put(account.getBankCdaId(), account);

        return account;
    }
}
