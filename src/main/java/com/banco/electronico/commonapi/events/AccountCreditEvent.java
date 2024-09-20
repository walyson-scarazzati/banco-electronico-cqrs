package com.banco.electronico.commonapi.events;

import lombok.Getter;

public class AccountCreditEvent extends BaseEvent<String>{

    @Getter
    private String currency;

    @Getter
    private double amount;

    public AccountCreditEvent(String id, String currency, double amount) {
        super(id);
        this.currency = currency;
        this.amount = amount;
    }
}
