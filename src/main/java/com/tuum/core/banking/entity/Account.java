package com.tuum.core.banking.entity;

import com.sun.istack.NotNull;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import static com.tuum.core.banking.constants.Messages.*;

@Entity
@Table(name = "account")
public class Account extends CoreBankingBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_id_generator")
    @SequenceGenerator(name="account_id_generator",
            sequenceName = "account_seq", allocationSize=1, initialValue=100000)
    private long id;


    @Column(name="customer_id")
    @NotNull
    private int customerId;

    @Column(name="country")
    @Size(max = 255, message = COUNTRY_TOO_LONG)
    private String country;

    public Account(int _customerId, String _country) {
        customerId = _customerId;
        country = _country;
    }

    public Account() {

    }

    public long getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCountry() {
        return country;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setCountry(String country) {
        this.country = country;
    }


}
