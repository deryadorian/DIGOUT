package com.digout.service.payment;

public class PaymentResult {

    private String responseCode;
    private String transactionReferenceId;
    private String errorMessage;
    private String sysErrorMessage;

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getResponseCode() {
        return this.responseCode;
    }

    public String getSysErrorMessage() {
        return this.sysErrorMessage;
    }

    public String getTransactionReferenceId() {
        return this.transactionReferenceId;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setResponseCode(final String responseCode) {
        this.responseCode = responseCode;
    }

    public void setSysErrorMessage(final String sysErrorMessage) {
        this.sysErrorMessage = sysErrorMessage;
    }

    public void setTransactionReferenceId(final String transactionReferenceId) {
        this.transactionReferenceId = transactionReferenceId;
    }
}
