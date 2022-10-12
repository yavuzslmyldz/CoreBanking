package com.tuum.core.banking.service;

import com.tuum.core.banking.constants.Currency;
import com.tuum.core.banking.entity.Account;
import com.tuum.core.banking.entity.Transaction;
import com.tuum.core.banking.repository.AccountRepository;
import com.tuum.core.banking.serviceparam.CreateAccountInput;
import com.tuum.core.banking.serviceparam.CreateAccountOutput;
import com.tuum.core.banking.utils.EnumUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.*;

import static com.tuum.core.banking.constants.Messages.INVALID_CURRENCY;

@Service
public class AccountService {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
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

        Set<ConstraintViolation<Account>> violations = validator.validate(input.getAccount());
        Iterator<ConstraintViolation<Account>> violationsItr = violations.iterator();

        while(violationsItr.hasNext()) {
            ConstraintViolation<Account> violation = violationsItr.next();
            violationMessages.add(violation.getMessage());
        }

        if(currencies == null || currencies.isEmpty()){
            violationMessages.add(INVALID_CURRENCY);
        }

        Iterator<String> currenciesItr = input.getCurrencies().iterator();

        while (currenciesItr.hasNext()) {
            String currency = currenciesItr.next();
            if(!EnumUtils.isMember(currency, Currency.class)){
                violationMessages.add(INVALID_CURRENCY + ": " + currency);
            }
        }

        return violationMessages;
    }
    public CreateAccountOutput createAccountMessage(CreateAccountInput payload) throws AmqpException {
            CreateAccountOutput message = new CreateAccountOutput();

            // getting next id for service output
            payload.getAccount().setId(accountReporsitory.getNextAccountId());

            message.setBalances(balanceService.createBalancesViaAccount(payload.getAccount(),payload.getCurrencies()));
            message.setAccount(payload.getAccount());

            mqTemplate.convertAndSend(exchange, accountRoutingKey, message);


            return message;
    }

    @Transactional
    @RabbitListener(queues = {"${core.banking.rabbitmq.account.queue}"})
    public void accountConsumer(CreateAccountOutput unAcked){
        accountReporsitory.save(unAcked.getAccount());
        balanceService.saveBalances(unAcked.getBalances());
    }

    public Optional<Account> getAccount(long accountId){

        return accountReporsitory.findById(accountId);
    }

    public boolean isExist(long accountId){
        return accountReporsitory.existsById(accountId);
    }

}
