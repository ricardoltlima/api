package com.mtb.app.service;

import com.mtb.app.handler.TransactionFactory;
import com.mtb.app.handler.TransactionHandler;
import com.mtb.app.mapper.TransactionMapper;
import com.mtb.app.model.Transaction;
import com.mtb.app.model.dto.transaction.CreateTransactionRequest;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;
import org.springframework.stereotype.Service;

@Service
public class TransactionsService {

    private final TransactionFactory transactionFactory;
    private final TransactionMapper transactionMapper;

    public TransactionsService(TransactionFactory transactionFactory, TransactionMapper transactionMapper) {
        this.transactionFactory = transactionFactory;
        this.transactionMapper = transactionMapper;
    }

    public CreateTransactionResponse createTransaction(CreateTransactionRequest createTransactionRequest) {

        Transaction transaction = transactionMapper.toCdaTransaction(createTransactionRequest);
        TransactionHandler transactionHandler = transactionFactory.getTransactionType(transaction);

        return transactionHandler.moveFunds(transaction);
    }
}
