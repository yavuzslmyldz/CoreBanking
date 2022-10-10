package com.tuum.core.banking.entity;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "balance")
public class Balance extends CoreBankingBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "balance_id_generator")
    @SequenceGenerator(name="balance_id_generator",
            sequenceName = "balance_seq", allocationSize=1, initialValue=500000)
    private long id;

    @Column(name="customer_id")
    @NotNull
    private int customerId;

    @Column(name="account_id")
    @NotNull
    private long accountId;

    @Column(name="currency")
    @NotNull
    private String currency;

    @Column(name="amount")
    @NotNull
    private BigDecimal amount;



    public Balance(int _customerId, long _accountId, String _currency, BigDecimal _amount) {
        customerId = _customerId;
        accountId = _accountId;
        currency = _currency;
        amount = _amount;
    }

    public Balance() {

    }

    public long getId() {
        return id;
    }
    public int getCustomerId() {
        return customerId;
    }
    public long getAccountId() {
        return accountId;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }



    public enum validCurrencyTypes {
        EUR, SEK, GBP, USD
    }
}
