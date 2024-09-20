package com.banco.electronico.query.services;

import com.banco.electronico.commonapi.enums.TransactionType;
import com.banco.electronico.commonapi.events.AccountCreatedEvent;
import com.banco.electronico.commonapi.events.AccountCreditEvent;
import com.banco.electronico.commonapi.events.AccountDebitEvent;
import com.banco.electronico.query.dto.AccountWatchEvent;
import com.banco.electronico.query.entities.Account;
import com.banco.electronico.query.entities.AccountTransaction;
import com.banco.electronico.query.queries.GetAccountBalanceStream;
import com.banco.electronico.query.queries.GetAccountById;
import com.banco.electronico.query.queries.GetAllAccounts;
import com.banco.electronico.query.repository.AccountRepository;
import com.banco.electronico.query.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class AccountEventHandlerService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private QueryUpdateEmitter queryUpdateEmitter;

    @EventHandler
    public void on(AccountCreatedEvent accountCreatedEvent, EventMessage<AccountCreatedEvent> eventEventMessage){
        log.info("*********************************");
        log.info("AccountCreatedEvent recibido");
        Account account = new Account();
        account.setId(accountCreatedEvent.getId());
        account.setBalance(accountCreatedEvent.getBalance());
        account.setStatus(accountCreatedEvent.getStatus());
        account.setCurrency(accountCreatedEvent.getCurrency());
        account.setCreatedAt(eventEventMessage.getTimestamp());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountCreditEvent accountCreditEvent,EventMessage<AccountCreatedEvent> eventMessage){
        log.info("*********************************");
        log.info("AccountCreditEvent recibido");
        Account account = accountRepository.findById(accountCreditEvent.getId()).get();
        AccountTransaction accountTransaction = AccountTransaction.builder()
                .account(account)
                .amount(accountCreditEvent.getAmount())
                .transactionType(TransactionType.CREDIT)
                .timestamp(eventMessage.getTimestamp())
                .build();
        transactionRepository.save(accountTransaction);
        account.setBalance(account.getBalance() + accountCreditEvent.getAmount());
        accountRepository.save(account);

        AccountWatchEvent accountWatchEvent = new AccountWatchEvent(
                accountTransaction.getTimestamp(),account.getId(),account.getBalance(),accountTransaction.getTransactionType(),accountTransaction.getAmount()
        );
        queryUpdateEmitter.emit(GetAccountBalanceStream.class,(query)->(query.getAccountId().equals(account.getId())),accountWatchEvent);
    }

    @EventHandler
    public void on(AccountDebitEvent accountDebitEvent, EventMessage<AccountCreatedEvent> eventMessage){
        log.info("*********************************");
        log.info("AccountDebitEvent recibido");
        Account account = accountRepository.findById(accountDebitEvent.getId()).get();
        AccountTransaction accountTransaction = AccountTransaction.builder()
                .account(account)
                .amount(accountDebitEvent.getAmount())
                .transactionType(TransactionType.DEBIT)
                .timestamp(eventMessage.getTimestamp())
                .build();
        transactionRepository.save(accountTransaction);
        account.setBalance(account.getBalance() - accountDebitEvent.getAmount());
        accountRepository.save(account);

        AccountWatchEvent accountWatchEvent = new AccountWatchEvent(
                accountTransaction.getTimestamp(),account.getId(),account.getBalance(),accountTransaction.getTransactionType(),accountTransaction.getAmount()
        );
        queryUpdateEmitter.emit(GetAccountBalanceStream.class,(query)->(query.getAccountId().equals(account.getId())),accountWatchEvent);
    }

    @QueryHandler
    public List<Account> on(GetAllAccounts query){
        return accountRepository.findAll();
    }

    @QueryHandler
    public Account on(GetAccountById query){
        return accountRepository.findById(query.getAccountId()).get();
    }

    @QueryHandler
    public AccountWatchEvent on(GetAccountBalanceStream query){
        Account account = accountRepository.findById(query.getAccountId()).get();
        AccountTransaction accountTransaction = transactionRepository.findTop1ByAccountIdOrderByTimestampDesc(query.getAccountId());

        if(accountTransaction != null){
            return new AccountWatchEvent(
                    accountTransaction.getTimestamp(),
                    account.getId(),
                    account.getBalance(),
                    accountTransaction.getTransactionType(),
                    accountTransaction.getAmount()
            );
        }
        return null;
    }
}