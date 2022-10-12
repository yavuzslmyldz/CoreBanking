package com.tuum.core.banking.serviceparam;

import com.tuum.core.banking.entity.Transaction;

public class CreateTransactionInput {
    private Transaction transaction;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
