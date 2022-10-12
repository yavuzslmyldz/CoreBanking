package com.tuum.core.banking.serviceparam;

import com.tuum.core.banking.entity.Account;
import com.tuum.core.banking.entity.Balance;

import java.util.List;

public class GetAccountOutput {
    private Account account;
    private List<Balance> balances;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }
}
