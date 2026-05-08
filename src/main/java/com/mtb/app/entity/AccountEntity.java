package com.mtb.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class AccountEntity {

    @EmbeddedId
    private BankCariId id;

    @Column(name = "bank_dda_id")
    private String bankDdaId;

    @Column(name = "cari_cda_id", insertable = false, updatable = false)
    private String cariCdaId;

    @Column(name = "internal_bank_cda_id")
    private String internalBankCdaId;

    @Column(name = "cari_customer_id")
    private String cariCustomerId;

    @Column(name = "cari_wallet_address")
    private String cariWalletAddress;

    @Column(name = "cari_wallet_status")
    private String cariWalletStatus;

    public AccountEntity() {
    }

    public AccountEntity(BankCariId id,
                         String bankDdaId,
                         String cariCdaId,
                         String internalBankCdaId,
                         String cariCustomerId,
                         String cariWalletAddress,
                         String cariWalletStatus) {
        this.id = id;
        this.bankDdaId = bankDdaId;
        this.cariCdaId = cariCdaId;
        this.internalBankCdaId = internalBankCdaId;
        this.cariCustomerId = cariCustomerId;
        this.cariWalletAddress = cariWalletAddress;
        this.cariWalletStatus = cariWalletStatus;
    }

    public AccountEntity(BankCariId id, String cariCustomerId, String cariWalletAddress, String cariWalletStatus) {
        this.id = id;
        this.cariCustomerId = cariCustomerId;
        this.cariWalletAddress = cariWalletAddress;
        this.cariWalletStatus = cariWalletStatus;
    }

    public BankCariId getId() {
        return id;
    }

    public void setId(BankCariId id) {
        this.id = id;
    }

    public String getBankDdaId() {
        return bankDdaId;
    }

    public void setBankDdaId(String bankDdaId) {
        this.bankDdaId = bankDdaId;
    }

    public String getCariCdaId() {
        return cariCdaId;
    }

    public void setCariCdaId(String cariCdaId) {
        this.cariCdaId = cariCdaId;
    }

    public String getInternalBankCdaId() {
        return internalBankCdaId;
    }

    public void setInternalBankCdaId(String internalBankCdaId) {
        this.internalBankCdaId = internalBankCdaId;
    }

    public String getCariCustomerId() {
        return cariCustomerId;
    }

    public void setCariCustomerId(String cariCustomerId) {
        this.cariCustomerId = cariCustomerId;
    }

    public String getCariWalletAddress() {
        return cariWalletAddress;
    }

    public void setCariWalletAddress(String cariWalletAddress) {
        this.cariWalletAddress = cariWalletAddress;
    }

    public String getCariWalletStatus() {
        return cariWalletStatus;
    }

    public void setCariWalletStatus(String cariWalletStatus) {
        this.cariWalletStatus = cariWalletStatus;
    }
}
