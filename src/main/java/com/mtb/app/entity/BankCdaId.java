package com.mtb.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BankCdaId implements Serializable {

    @Column(name = "bank_cda_id")
    private String bankCdaId;

    @Column(name = "cari_cda_id")
    private String cariCdaId;

    public BankCdaId(String bankCdaId, String cariCdaId) {
        this.bankCdaId = bankCdaId;
        this.cariCdaId = cariCdaId;
    }

    public BankCdaId() {
    }

    public String getBankCdaId() {
        return bankCdaId;
    }

    public void setBankCdaId(String bankCdaId) {
        this.bankCdaId = bankCdaId;
    }

    public String getCariCdaId() {
        return cariCdaId;
    }

    public void setCariCdaId(String cariCdaId) {
        this.cariCdaId = cariCdaId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BankCdaId bankCdaId1 = (BankCdaId) o;
        return Objects.equals(bankCdaId, bankCdaId1.bankCdaId)
                && Objects.equals(cariCdaId, bankCdaId1.cariCdaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bankCdaId, cariCdaId);
    }
}
