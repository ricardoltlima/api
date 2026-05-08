package com.mtb.app.service;

import com.mtb.app.handler.TransactionFactory;
import com.mtb.app.handler.TransactionHandler;
import com.mtb.app.mapper.TransactionMapper;
import com.mtb.app.model.Transaction;
import com.mtb.app.model.dto.transaction.CreateTransactionRequest;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;
import com.mtb.app.repository.AccountRepository;
import com.mtb.app.validation.ValidateAccount;
import org.springframework.stereotype.Service;

@Service
public class TransactionsService {

    private final TransactionFactory transactionFactory;
    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;

    public TransactionsService(TransactionFactory transactionFactory, TransactionMapper transactionMapper, AccountRepository accountRepository) {
        this.transactionFactory = transactionFactory;
        this.transactionMapper = transactionMapper;
        this.accountRepository = accountRepository;
    }

    public CreateTransactionResponse createTransaction(CreateTransactionRequest createTransactionRequest) {

        Transaction transaction = transactionMapper.toCdaTransaction(createTransactionRequest);

        // Verifies if the account exists before sending it to Cari
        ValidateAccount validateAccount = new ValidateAccount(accountRepository);
        validateAccount.validateExistingAccount(transaction);

        TransactionHandler transactionHandler = transactionFactory.getTransactionType(transaction);

        return transactionHandler.moveFunds(transaction);
    }
}
