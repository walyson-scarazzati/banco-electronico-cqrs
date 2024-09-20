package com.banco.electronico.commands.aggregates;

import com.banco.electronico.commonapi.commands.CreateAccountCommand;
import com.banco.electronico.commonapi.commands.CreditAccountCommand;
import com.banco.electronico.commonapi.commands.DebitAccountCommand;
import com.banco.electronico.commonapi.enums.AccountStatus;
import com.banco.electronico.commonapi.events.AccountCreatedEvent;
import com.banco.electronico.commonapi.events.AccountCreditEvent;
import com.banco.electronico.commonapi.events.AccountDebitEvent;
import com.banco.electronico.commonapi.exceptions.NegativeInitialBalanceException;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Locale;

@Aggregate
@Slf4j
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;
    private String currency;
    private double balance;
    private AccountStatus accountStatus;

    public AccountAggregate() {

    }

    @CommandHandler
    public AccountAggregate(CreateAccountCommand createAccountCommand){
        log.info("CreateAccountCommand recibido");
        if(createAccountCommand.getInitialBalance() < 0){
            throw new NegativeInitialBalanceException("Error, no se puede tener un saldo negativo");
        }
        AggregateLifecycle.apply(new AccountCreatedEvent(
                createAccountCommand.getId(),
                createAccountCommand.getCurrency(),
                createAccountCommand.getInitialBalance(),
                AccountStatus.CREATED
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent accountCreatedEvent){
        log.info("Evento AccountCreatedEvent");
        this.accountId = accountCreatedEvent.getId();
        this.balance = accountCreatedEvent.getBalance();
        this.accountStatus = accountCreatedEvent.getStatus();
        this.currency = accountCreatedEvent.getCurrency();
    }

    @CommandHandler
    public void handle(CreditAccountCommand creditAccountCommand){
        log.info("CreditAccountCommand recibido");
        if(creditAccountCommand.getAmount() < 0){
            throw new NegativeInitialBalanceException("Error, no se puede tener un saldo negativo");
        }
        AggregateLifecycle.apply(new AccountCreditEvent(
                creditAccountCommand.getId(),
                creditAccountCommand.getCurrency(),
                creditAccountCommand.getAmount()
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreditEvent accountCreditEvent){
        log.info("Evento AccountCreditEvent");
        this.balance += accountCreditEvent.getAmount();
    }

    @CommandHandler
    public void handle(DebitAccountCommand debitAccountCommand){
        log.info("DebitAccountCommand recibido");
        if(debitAccountCommand.getAmount() < 0){
            throw new NegativeInitialBalanceException("No se puede retirar montos negativos");
        }
        if(debitAccountCommand.getAmount() > this.balance){
            throw new RuntimeException("Saldo insuficiente");
        }

        AggregateLifecycle.apply(new AccountDebitEvent(
                debitAccountCommand.getId(),
                debitAccountCommand.getCurrency(),
                debitAccountCommand.getAmount()
        ));
    }

    @EventSourcingHandler
    public void on(AccountDebitEvent accountDebitEvent){
        log.info("Evento AccountDebitEvent");
        this.balance -= accountDebitEvent.getAmount();
    }
}
