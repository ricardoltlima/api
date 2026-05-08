package com.mtb.app.model;

public class CdaAccount {

    private String bankCustomerId;
    private String bankCdaId;
    private Boolean bankDdaLinked;
    private String bankDdaLinkedId;
    private String bankCustomerLegalName;
    private String bankCustomerEin;

    public CdaAccount() {
    }

    public CdaAccount(String bankCustomerId, String bankCdaId, Boolean bankDdaLinked, String bankDdaLinkedId, String bankCustomerLegalName, String bankCustomerEin) {
        this.bankCustomerId = bankCustomerId;
        this.bankCdaId = bankCdaId;
        this.bankDdaLinked = bankDdaLinked;
        this.bankDdaLinkedId = bankDdaLinkedId;
        this.bankCustomerLegalName = bankCustomerLegalName;
        this.bankCustomerEin = bankCustomerEin;
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
}
