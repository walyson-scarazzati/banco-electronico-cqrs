package com.banco.electronico.query.controllers;

import com.banco.electronico.query.dto.AccountWatchEvent;
import com.banco.electronico.query.entities.Account;
import com.banco.electronico.query.queries.GetAccountById;
import com.banco.electronico.query.queries.GetAllAccounts;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/query/account")
public class AccountQueryController {

    @Autowired
    private QueryGateway queryGateway;

    @GetMapping("/list")
    public CompletableFuture<List<Account>> listarCuentas(){
        return queryGateway.query(new GetAllAccounts(), ResponseTypes.multipleInstancesOf(Account.class));
    }

    @GetMapping("/byId/{id}")
    public CompletableFuture<Account> listarCuentaPorId(@PathVariable String id){
        return queryGateway.query(new GetAccountById(id),ResponseTypes.instanceOf(Account.class));
    }

}
