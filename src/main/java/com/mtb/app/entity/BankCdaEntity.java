package com.mtb.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "bank_cda")
public class BankCdaEntity {

    @EmbeddedId
    private BankCdaId id;

    @Column(name = "cari_customer_id")
    private String cariCustomerId;

    @Column(name = "cari_wallet_address")
    private String cariWalletAddress;

    @Column(name = "cari_wallet_status")
    private String cariWalletStatus;

    public BankCdaEntity() {
    }

    public BankCdaEntity(BankCdaId id, String cariCustomerId, String cariWalletAddress, String cariWalletStatus) {
        this.id = id;
        this.cariCustomerId = cariCustomerId;
        this.cariWalletAddress = cariWalletAddress;
        this.cariWalletStatus = cariWalletStatus;
    }

    public BankCdaId getId() {
        return id;
    }

    public void setId(BankCdaId id) {
        this.id = id;
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
