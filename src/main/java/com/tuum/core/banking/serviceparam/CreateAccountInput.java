package com.tuum.core.banking.serviceparam;

import com.tuum.core.banking.entity.Account;

import java.util.List;

public class CreateAccountInput {
    private Account account;
    private List<String> currencies;

    public Account getAccount() {
        return account;
    }

    public List<String> getCurrencies() {
        return currencies;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setCurrencies(List<String> currencies) {
        this.currencies = currencies;
    }
}
