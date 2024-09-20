package com.banco.electronico.query.repository;

import com.banco.electronico.query.entities.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<AccountTransaction,Long> {

    AccountTransaction findTop1ByAccountIdOrderByTimestampDesc(String accountId);

}
