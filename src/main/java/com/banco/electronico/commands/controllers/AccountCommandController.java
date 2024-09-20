package com.banco.electronico.commands.controllers;

import com.banco.electronico.commonapi.commands.CreateAccountCommand;
import com.banco.electronico.commonapi.commands.CreditAccountCommand;
import com.banco.electronico.commonapi.commands.DebitAccountCommand;
import com.banco.electronico.commonapi.dtos.CreateAccountRequestDTO;
import com.banco.electronico.commonapi.dtos.CreditAccountRequestDTO;
import com.banco.electronico.commonapi.dtos.DebitAccountRequestDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/commands/account")
public class AccountCommandController {

    @Autowired
    private CommandGateway commandGateway;

    @PostMapping("/create")
    public CompletableFuture<String> crearNuevaCuenta(@RequestBody CreateAccountRequestDTO createAccountRequestDTO){
        return commandGateway.send(new CreateAccountCommand(
                UUID.randomUUID().toString(),
                createAccountRequestDTO.getCurrency(),
                createAccountRequestDTO.getInitialBalance()
        ));
    }

    @PostMapping("/debit")
    public CompletableFuture<String> realizarDebito(@RequestBody DebitAccountRequestDTO debitAccountRequestDTO){
        return commandGateway.send(new DebitAccountCommand(
                debitAccountRequestDTO.getAccountId(),
                debitAccountRequestDTO.getCurrency(),
                debitAccountRequestDTO.getAmount()
        ));
    }

    @PostMapping("/credit")
    public CompletableFuture<String> realizarCredito(@RequestBody CreditAccountRequestDTO creditAccountRequestDTO){
        return commandGateway.send(new CreditAccountCommand(
                creditAccountRequestDTO.getAccountId(),
                creditAccountRequestDTO.getCurrency(),
                creditAccountRequestDTO.getAmount()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
