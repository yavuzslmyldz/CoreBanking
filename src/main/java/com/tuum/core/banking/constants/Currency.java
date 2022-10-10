package com.tuum.core.banking.constants;

public enum Currency {
    EUR, SEK, GBP, USD;

    public static boolean isMember(String name) {
        for (Currency currency : Currency.values()) {
            if (currency.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
