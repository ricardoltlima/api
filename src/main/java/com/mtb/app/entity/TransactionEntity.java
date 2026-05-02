package com.mtb.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigInteger;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "bank_dda_id")
    private String bankDdaId;

    @Column(name = "bank_cda_id")
    private String bankCdaId;

    @Column(name = "internal_bank_cda_id")
    private String internalBankCdaId;

    @Column(name = "token_amount")
    private BigInteger tokenAmount;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "transaction_status")
    private String transactionStatus;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public TransactionEntity() {
    }

    public TransactionEntity(Long id,
                             String bankDdaId,
                             String bankCdaId,
                             String internalBankCdaId,
                             BigInteger tokenAmount,
                             String customerId,
                             String transactionStatus,
                             OffsetDateTime createdAt) {
        this.id = id;
        this.bankDdaId = bankDdaId;
        this.bankCdaId = bankCdaId;
        this.internalBankCdaId = internalBankCdaId;
        this.tokenAmount = tokenAmount;
        this.customerId = customerId;
        this.transactionStatus = transactionStatus;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getInternalBankCdaId() {
        return internalBankCdaId;
    }

    public void setInternalBankCdaId(String internalBankCdaId) {
        this.internalBankCdaId = internalBankCdaId;
    }

    public BigInteger getTokenAmount() {
        return tokenAmount;
    }

    public void setTokenAmount(BigInteger tokenAmount) {
        this.tokenAmount = tokenAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
