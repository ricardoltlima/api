package com.mtb.app.model;

public enum TransactionOperations {
    MINT("mint"),
    BURN("burn"),
    ON_US("onus-transfer"),
    INTERBANK("interbank-transfer");

    private final String transactionOperation;

    TransactionOperations(String operation) {
        this.transactionOperation = operation;
    }

    public String getTransactionOperation() {
        return transactionOperation;
    }
}
