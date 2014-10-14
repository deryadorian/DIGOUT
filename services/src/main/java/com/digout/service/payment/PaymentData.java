package com.digout.service.payment;

import org.joda.time.YearMonth;

import com.digout.support.money.CurrencyUnit;

public class PaymentData {

    private String cardNumber;
    private YearMonth cardExpireDate;
    private String cardSecurityNumber;
    private double orderPrice;
    private CurrencyUnit currencyUnit;
    private String userIpAddress;
    private String userEmail;

    public YearMonth getCardExpireDate() {
        return this.cardExpireDate;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public String getCardSecurityNumber() {
        return this.cardSecurityNumber;
    }

    public CurrencyUnit getCurrencyUnit() {
        return this.currencyUnit;
    }

    public double getOrderPrice() {
        return this.orderPrice;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public String getUserIpAddress() {
        return this.userIpAddress;
    }

    public void setCardExpireDate(final YearMonth cardExpireDate) {
        this.cardExpireDate = cardExpireDate;
    }

    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setCardSecurityNumber(final String cardSecurityNumber) {
        this.cardSecurityNumber = cardSecurityNumber;
    }

    public void setCurrencyUnit(final CurrencyUnit currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public void setOrderPrice(final double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public void setUserEmail(final String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserIpAddress(final String userIpAddress) {
        this.userIpAddress = userIpAddress;
    }
}
