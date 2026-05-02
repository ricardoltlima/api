package com.mtb.app.model;

import java.math.BigDecimal;

public class Transaction {

    private TransactionOperations operation;
    private String bankDdaId;
    private String bankCdaId;
    private BigDecimal tokenAmount;
    private String customerId;
    private String destinationWalletAddress;
    private String checkTransactionId;

    public TransactionOperations getOperation() {
        return operation;
    }

    public void setOperation(TransactionOperations operation) {
        this.operation = operation;
    }

    public String getBankDdaId() {
        return bankDdaId;
    }

    public void setBankDdaId(String bankDdaId) {
        this.bankDdaId = bankDdaId;
    }

    public String getBankCdaId() {
        return bankCdaId;
    }

    public void setBankCdaId(String bankCdaId) {
        this.bankCdaId = bankCdaId;
    }

    public BigDecimal getTokenAmount() {
        return tokenAmount;
    }

    public void setTokenAmount(BigDecimal tokenAmount) {
        this.tokenAmount = tokenAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDestinationWalletAddress() {
        return destinationWalletAddress;
    }

    public void setDestinationWalletAddress(String destinationWalletAddress) {
        this.destinationWalletAddress = destinationWalletAddress;
    }

    public String getCheckTransactionId() {
        return checkTransactionId;
    }

    public void setCheckTransactionId(String checkTransactionId) {
        this.checkTransactionId = checkTransactionId;
    }
}
