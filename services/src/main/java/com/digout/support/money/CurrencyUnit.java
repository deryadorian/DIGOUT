package com.digout.support.money;

/**
 * Represents a currency. Currencies are identified by their ISO 4217 currency codes. Visit the <a
 * href="http://www.bsi-global.com/">
 */
public enum CurrencyUnit {
    USD("840"),
    EUR("978"),
    TRY("949");

    public static boolean isValid(final String currency) {
        return of(currency) != null;
    }

    public static CurrencyUnit of(final String currencyCode) {
        try {
            return valueOf(currencyCode);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private final String num;

    private CurrencyUnit(final String num) {
        this.num = num;
    }

    public String getCode() {
        return this.num;
    }
}
