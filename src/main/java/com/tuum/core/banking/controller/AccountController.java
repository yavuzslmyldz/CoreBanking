package com.tuum.core.banking.controller;

import com.tuum.core.banking.entity.Account;
import com.tuum.core.banking.entity.Balance;
import com.tuum.core.banking.service.AccountService;
import com.tuum.core.banking.service.BalanceService;
import com.tuum.core.banking.serviceparam.ApiResponse;
import com.tuum.core.banking.serviceparam.CreateAccountInput;
import com.tuum.core.banking.serviceparam.CreateAccountOutput;
import com.tuum.core.banking.serviceparam.GetAccountOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.tuum.core.banking.constants.Messages.ACCOUNT_NOT_FOUND;


@RestController
@RequestMapping("/api/v1")
public class AccountController {

    private AccountService accountService;
    private BalanceService balanceService;

    @Autowired
    public AccountController(AccountService _accountService, BalanceService _balanceService){
        accountService = _accountService;
        balanceService = _balanceService;
    }

    @Transactional
    @PostMapping("/account")
    public ResponseEntity<ApiResponse> createAccount(@RequestBody CreateAccountInput payload) {
        try {

            List<String> violations = accountService.validateAccount(payload);
            if(!violations.isEmpty()){
                return new ResponseEntity<>(new ApiResponse(null,violations), HttpStatus.BAD_REQUEST);
            }

            // account creating message for mq
             CreateAccountOutput message = accountService.createAccountMessage(payload);

            return new ResponseEntity<>(new ApiResponse(message,null),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping ("/account")
    public ResponseEntity<ApiResponse> getAccount(@RequestParam("accountId") long accountId) {
        try {

            //getting account
            Optional<Account> account = accountService.getAccount(accountId);

            if(!account.isPresent()){
                return new ResponseEntity<>(new ApiResponse(null,Arrays.asList(ACCOUNT_NOT_FOUND)),
                        HttpStatus.NO_CONTENT);
            }

            GetAccountOutput output = new GetAccountOutput();
            output.setAccount(account.get());

            // getting balances for given account
            output.setBalances(balanceService.getBalances(accountId));

            return new ResponseEntity<>(new ApiResponse<>(output,null) , HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
