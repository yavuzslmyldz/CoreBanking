package com.tuum.core.banking.controller;


import com.tuum.core.banking.entity.Transaction;
import com.tuum.core.banking.service.AccountService;
import com.tuum.core.banking.service.TransactionService;
import com.tuum.core.banking.serviceparam.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static com.tuum.core.banking.constants.Messages.INVALID_ACCOUNT;


@RestController
@RequestMapping("/api/v1")
public class TransactionController {
    private TransactionService transactionService;
    private AccountService accountService;

    @Autowired
    public TransactionController(TransactionService _transactionService, AccountService _accountService){
        transactionService = _transactionService;
        accountService = _accountService;
    }

    @GetMapping("/transaction")
    public ResponseEntity<ApiResponse> getTransactions(@RequestParam("accountId") long accountId) {
        try {

            //checking account
            if(!accountService.isExist(accountId)){
                return new ResponseEntity<>(new ApiResponse(null,Arrays.asList(INVALID_ACCOUNT)),
                        HttpStatus.NO_CONTENT);
            }
            // getting transactions for given account
            List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
            return new ResponseEntity<>(new ApiResponse(transactions,null), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/transaction")
    public ResponseEntity<ApiResponse> createTransaction(@RequestBody Transaction transaction){
       try {

           List<String> violations = transactionService.validateTransaction(transaction);
           if(!violations.isEmpty()){
               return new ResponseEntity<>(new ApiResponse(null,violations), HttpStatus.BAD_REQUEST);
           }

           transactionService.createTransactionMessage(transaction);
           return new ResponseEntity<>(new ApiResponse(transaction,null), HttpStatus.OK);
       }
       catch(Exception e){
           return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }
}
