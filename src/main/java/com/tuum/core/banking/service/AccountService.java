package com.tuum.core.banking.service;

import com.tuum.core.banking.constants.Currency;
import com.tuum.core.banking.entity.Account;
import com.tuum.core.banking.repository.AccountRepository;
import com.tuum.core.banking.serviceparam.CreateAccountInput;
import com.tuum.core.banking.utils.EnumUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.tuum.core.banking.constants.Messages.INVALID_CURRENCY;

@Service
public class AccountService {

    private AccountRepository accountReporsitory;

    private BalanceService balanceService;
    private AmqpTemplate mqTemplate;

    @Value("${core.banking.rabbitmq.exchange}")
    String exchange;

    @Value("${core.banking.rabbitmq.account.routing.key}")
    String accountRoutingKey;


    @Autowired
    public AccountService(AccountRepository _accountRepository, BalanceService _balanceService, AmqpTemplate _mqTemplate) {
        accountReporsitory = _accountRepository;
        balanceService = _balanceService;
        mqTemplate = _mqTemplate;
    }

    public List<String> validateAccount(CreateAccountInput input){
        // checking currencies
        List<String> violationMessages = new ArrayList<>();
        List<String> currencies = input.getCurrencies();

        if(currencies == null || currencies.isEmpty()){
            violationMessages.add(INVALID_CURRENCY);
            return violationMessages;
        }

        Iterator<String> itr = input.getCurrencies().iterator();

        while (itr.hasNext()) {
            String currency = itr.next();
            if(!EnumUtils.isMember(currency, Currency.class)){
                violationMessages.add(INVALID_CURRENCY + ": " + currency);
            }
        }

        return violationMessages;
    }
    public void createAccountMessage(CreateAccountInput input){
            mqTemplate.convertAndSend(exchange, accountRoutingKey, input);
    }

    @Transactional
    @RabbitListener(queues = {"${core.banking.rabbitmq.account.queue}"})
    public void accountConsumer(CreateAccountInput input){
        accountReporsitory.save(input.getAccount());
        balanceService.createBalancesViaAccount(input.getAccount(), input.getCurrencies());
    }

    public Optional<Account> getAccount(long accountId){

        return accountReporsitory.findById(accountId);
    }

    public boolean isExist(long accountId){
        return accountReporsitory.existsById(accountId);
    }

}
