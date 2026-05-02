package com.mtb.app.model;

import java.time.OffsetDateTime;
import java.math.BigDecimal;

public class CdaAccount {

    private String cariCdaId;
    private String bankId;
    private String bankCustomerId;
    private String bankCdaId;
    private String cariCustomerId;
    private String cariWalletAddress;
    private String cariWalletStatus;
    private BigDecimal tokenBalance;

    private String state;
    private Boolean bankDdaLinked;
    private String bankDdaLinkedId;
    private String bankCustomerLegalName;
    private String bankCustomerEin;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public String getCariCdaId() {
        return cariCdaId;
    }

    public void setCariCdaId(String cariCdaId) {
        this.cariCdaId = cariCdaId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankCustomerId() {
        return bankCustomerId;
    }

    public void setBankCustomerId(String bankCustomerId) {
        this.bankCustomerId = bankCustomerId;
    }

    public String getBankCdaId() {
        return bankCdaId;
    }

    public void setBankCdaId(String bankCdaId) {
        this.bankCdaId = bankCdaId;
    }

    public String getCariCustomerId() {
        return cariCustomerId;
    }

    public void setCariCustomerId(String cariCustomerId) {
        this.cariCustomerId = cariCustomerId;
    }

    public String getWalletAddress() {
        return cariWalletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.cariWalletAddress = walletAddress;
    }

    public String getWalletStatus() {
        return cariWalletStatus;
    }

    public void setWalletStatus(String walletStatus) {
        this.cariWalletStatus = walletStatus;
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

    public BigDecimal getTokenBalance() {
        return tokenBalance;
    }

    public void setTokenBalance(BigDecimal tokenBalance) {
        this.tokenBalance = tokenBalance;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getBankDdaLinked() {
        return bankDdaLinked;
    }

    public void setBankDdaLinked(Boolean bankDdaLinked) {
        this.bankDdaLinked = bankDdaLinked;
    }

    public String getBankDdaLinkedId() {
        return bankDdaLinkedId;
    }

    public void setBankDdaLinkedId(String bankDdaLinkedId) {
        this.bankDdaLinkedId = bankDdaLinkedId;
    }

    public String getBankCustomerLegalName() {
        return bankCustomerLegalName;
    }

    public void setBankCustomerLegalName(String bankCustomerLegalName) {
        this.bankCustomerLegalName = bankCustomerLegalName;
    }

    public String getBankCustomerEin() {
        return bankCustomerEin;
    }

    public void setBankCustomerEin(String bankCustomerEin) {
        this.bankCustomerEin = bankCustomerEin;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
