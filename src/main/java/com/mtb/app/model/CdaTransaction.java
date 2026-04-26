package com.mtb.app.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class CdaTransaction {
    private String cariTxnId;
    private TransactionOperations operation;
    private String bankCdaId;
    private String sourceWalletAddress;
    private String destinationWalletAddress;
    private BigDecimal tokenAmount;
    private TransactionStatus status;
    private String odfiStatus;
    private String rdfiStatus;
    private String checkTransactionId;
    private String bankTransactionId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public String getCariTxnId() {
        return cariTxnId;
    }

    public void setCariTxnId(String cariTxnId) {
        this.cariTxnId = cariTxnId;
    }

    public TransactionOperations getOperation() {
        return operation;
    }

    public void setOperation(TransactionOperations operation) {
        this.operation = operation;
    }

    public String getBankCdaId() {
        return bankCdaId;
    }

    public void setBankCdaId(String bankCdaId) {
        this.bankCdaId = bankCdaId;
    }

    public String getSourceWalletAddress() {
        return sourceWalletAddress;
    }

    public void setSourceWalletAddress(String sourceWalletAddress) {
        this.sourceWalletAddress = sourceWalletAddress;
    }

    public String getDestinationWalletAddress() {
        return destinationWalletAddress;
    }

    public void setDestinationWalletAddress(String destinationWalletAddress) {
        this.destinationWalletAddress = destinationWalletAddress;
    }

    public BigDecimal getTokenAmount() {
        return tokenAmount;
    }

    public void setTokenAmount(BigDecimal tokenAmount) {
        this.tokenAmount = tokenAmount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getOdfiStatus() {
        return odfiStatus;
    }

    public void setOdfiStatus(String odfiStatus) {
        this.odfiStatus = odfiStatus;
    }

    public String getRdfiStatus() {
        return rdfiStatus;
    }

    public void setRdfiStatus(String rdfiStatus) {
        this.rdfiStatus = rdfiStatus;
    }

    public String getCheckTransactionId() {
        return checkTransactionId;
    }

    public void setCheckTransactionId(String checkTransactionId) {
        this.checkTransactionId = checkTransactionId;
    }

    public String getBankTransactionId() {
        return bankTransactionId;
    }

    public void setBankTransactionId(String bankTransactionId) {
        this.bankTransactionId = bankTransactionId;
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
