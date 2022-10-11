package com.tuum.core.banking.service;

import com.tuum.core.banking.constants.Currency;
import com.tuum.core.banking.constants.Direction;
import com.tuum.core.banking.constants.TransactionStatus;
import com.tuum.core.banking.entity.Account;
import com.tuum.core.banking.entity.Balance;
import com.tuum.core.banking.entity.Transaction;
import com.tuum.core.banking.repository.TransactionRepository;
import com.tuum.core.banking.utils.EnumUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.*;

import static com.tuum.core.banking.constants.Messages.*;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;

    private AmqpTemplate mqTemplate;

    @Value("${core.banking.rabbitmq.exchange}")
    String exchange;

    @Value("${core.banking.rabbitmq.transaction.routing.key}")
    String transactionRoutingKey;

    private AccountService accountService;
    private BalanceService  balanceService;
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    public TransactionService(TransactionRepository _transactionRepository, AccountService _accountService, BalanceService _balanceService, AmqpTemplate _mqTemplate){
        transactionRepository = _transactionRepository;
        accountService = _accountService;
        balanceService = _balanceService;
        mqTemplate = _mqTemplate;
    }

    public List<String> validateTransaction(Transaction transaction) {

        List<String> violationMessages = new ArrayList<>();

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        Iterator<ConstraintViolation<Transaction>> itr = violations.iterator();

        while(itr.hasNext()) {
            ConstraintViolation<Transaction> violation = itr.next();
            violationMessages.add(violation.getMessage());
        }

        if(!violationMessages.isEmpty()){
            return violationMessages;
        }

        // checking direction
            if(!EnumUtils.isMember(transaction.getDirection(), Direction.class)){
                violationMessages.add(INVALID_DIRECTION);
                return violationMessages;
            }

        // checking currency
            if(!EnumUtils.isMember(transaction.getCurrency(), Currency.class)){
                violationMessages.add(INVALID_CURRENCY);
                return violationMessages;
            }

        // checking account
        if(!accountService.isExist(transaction.getAccountId())){
            violationMessages.add(ACCOUNT_MISSING);
            return violationMessages;
        }

        // checking balance
        Optional<Balance> balanceContainer = balanceService.getByAccountAndCurrency(transaction.getAccountId(),
                transaction.getCurrency());

        if(!balanceContainer.isPresent()){
            violationMessages.add(BALANCE_MISSING);
            return violationMessages;
        }

        // checking funds
        Balance balance = balanceContainer.get();
        boolean isOut = transaction.getDirection().equals(Direction.OUT.name());

        if(isOut && balance.getAmount().compareTo(transaction.getAmount()) < 0){
            violationMessages.add(INSUFFICIENT_FUNDS);
        }

        return violationMessages;
    }
    public List<Transaction> getTransactionsByAccountId(long accountId){
        return transactionRepository.findByAccountId(accountId);
    }

    public void createTransactionMessage(Transaction transaction){
        mqTemplate.convertAndSend(exchange, transactionRoutingKey, transaction);
    }

    @Transactional
    @RabbitListener(queues = {"${core.banking.rabbitmq.transaction.queue}"})
    public void transactionConsumer(Transaction transaction){
        //LOGGER.info(String.format("Received message -> %s", message));

            transaction.setStatus(TransactionStatus.WAITING);
            transactionRepository.save(transaction);

            try
            {
                // re-checking transaction before processing...
                List<String> violations = validateTransaction(transaction);

                if (!violations.isEmpty()) {
                    transaction.setStatus(TransactionStatus.REJECTED);
                    transactionRepository.saveAndFlush(transaction);
                    return;
                }

                // transaction processing...
                Optional<Account> account = accountService.getAccount(transaction.getAccountId());
                Optional<Balance> balance = balanceService.getByAccountAndCurrency(account.get().getId(), transaction.getCurrency());

                boolean isOut = transaction.getDirection().equals(Direction.OUT.name());
                BigDecimal currentAmount = balance.get().getAmount();

                balance.get().setAmount(isOut ? currentAmount.subtract(transaction.getAmount())
                        : currentAmount.add(transaction.getAmount()));

                balanceService.save(balance.get());

                transaction.setStatus(TransactionStatus.COMPLETED);
                transaction.setBalanceId(balance.get().getId());
                transactionRepository.save(transaction);
            }
            catch(Exception e){
                transaction.setStatus(TransactionStatus.INTERRUPTED);
                transactionRepository.saveAndFlush(transaction);
            }
    }
}
