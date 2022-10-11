package com.tuum.core.banking.service;

import com.tuum.core.banking.entity.Account;
import com.tuum.core.banking.entity.Balance;
import com.tuum.core.banking.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BalanceService {
    private BalanceRepository balanceRepository;

    @Autowired
    public BalanceService(BalanceRepository _balanceRepository) {
        balanceRepository = _balanceRepository;
    }

    public Balance createBalance(Balance balance){
        return balanceRepository.save(balance);
    }

    public void save(Balance balance){
         balanceRepository.save(balance);
    }

    public void createBalancesViaAccount(Account account, List<String> currencies){

        if(currencies.isEmpty())
            return;

        List<Balance> registeredBalances = new ArrayList<Balance>(currencies.size());
        int size = currencies.size();

        for(int i = 0; i < size; i++){
            Balance balance = new Balance();
            balance.setAccountId(account.getId());
            balance.setCustomerId(account.getCustomerId());
            balance.setCurrency(currencies.get(i));
            balance.setAmount(BigDecimal.valueOf(750000));

            registeredBalances.add(balance);
        }

        balanceRepository.saveAll(registeredBalances);
    }

    public List<Balance> getBalances(long accountId){
        return balanceRepository.findByAccountId(accountId);
    }

    public Optional<Balance> getByAccountAndCurrency(long accountId, String currency){
       return balanceRepository.findByAccountId(accountId).stream()
                .filter(x -> x.getCurrency().equals(currency)).findFirst();
    }
}
