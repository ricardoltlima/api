package com.mtb.app.service;

import com.mtb.app.client.CariClient;
import com.mtb.app.entity.BankCdaEntity;
import com.mtb.app.error.DuplicateActiveCDAException;
import com.mtb.app.mapper.CdaAccountMapper;
import com.mtb.app.model.CdaAccount;
import com.mtb.app.model.dto.cda.ProvisionWalletResponse;
import com.mtb.app.model.dto.cda.CreateCdaAccountRequest;
import com.mtb.app.model.dto.cda.CreateCdaAccountResponse;
import com.mtb.app.repository.BankCdaRepository;
import org.springframework.stereotype.Service;

@Service
public class CdaAccountService {

    private final CariClient cariClient;
    private final CdaAccountMapper cdaAccountMapper;
    private final BankCdaRepository bankCdaRepository;

    public CdaAccountService(CariClient cariClient, CdaAccountMapper cdaAccountMapper, BankCdaRepository bankCdaRepository) {
        this.cariClient = cariClient;
        this.cdaAccountMapper = cdaAccountMapper;
        this.bankCdaRepository = bankCdaRepository;
    }

    public CreateCdaAccountResponse createCdaAccount(CreateCdaAccountRequest createCdaAccountRequest) {
        ProvisionWalletResponse wallet = cariClient.provisionWallet(createCdaAccountRequest);

        // Must verify if the Cari CDA already exists for this Bank CDA Id
        if (bankCdaRepository.findByIdBankCdaIdAndIdCariCdaId(wallet.bankCdaId(), wallet.cariCdaId()).isPresent()) {
            throw new DuplicateActiveCDAException("Duplicate Active CDA", "DUPLICATE_ACTIVE_CDA", "cariCdaId", "A CDA with that bank_cda_id already exists, or the customer already has an active or restricted CDA");
        }

        BankCdaEntity bankCdaEntity = cdaAccountMapper.toBankCda(wallet);
        bankCdaRepository.save(bankCdaEntity);

        CdaAccount account = cdaAccountMapper.toCdaAccount(wallet);
        return cdaAccountMapper.toCreateCdaAccountResponse(account);
    }
}
