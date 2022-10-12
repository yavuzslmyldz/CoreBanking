package com.tuum.core.banking.serviceparam;

import com.tuum.core.banking.entity.Transaction;

public class CreateTransactionOutput {

    private Transaction transaction;


    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
