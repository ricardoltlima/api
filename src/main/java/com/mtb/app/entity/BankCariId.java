package com.mtb.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BankCariId implements Serializable {

    @Column(name = "bank_cda_id")
    private String bankCdaId;

    @Column(name = "bank_customer_id")
    private String bankCustomerId;

    public BankCariId(String bankCdaId, String bankCustomerId) {
        this.bankCdaId = bankCdaId;
        this.bankCustomerId = bankCustomerId;
    }

    public BankCariId() {
    }

    public String getBankCdaId() {
        return bankCdaId;
    }

    public void setBankCdaId(String bankCdaId) {
        this.bankCdaId = bankCdaId;
    }

    public String getBankCustomerId() {
        return bankCustomerId;
    }

    public void setBankCustomerId(String cariCdaId) {
        this.bankCustomerId = cariCdaId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BankCariId bankCariId1 = (BankCariId) o;
        return Objects.equals(bankCdaId, bankCariId1.bankCdaId)
                && Objects.equals(bankCustomerId, bankCariId1.bankCustomerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bankCdaId, bankCustomerId);
    }
}
