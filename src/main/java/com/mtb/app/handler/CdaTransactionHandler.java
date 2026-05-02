package com.mtb.app.handler;

import com.mtb.app.client.CariClient;
import com.mtb.app.entity.TransactionEntity;
import com.mtb.app.mapper.TransactionMapper;
import com.mtb.app.model.Transaction;
import com.mtb.app.model.dto.transaction.CariTransactionRequest;
import com.mtb.app.model.dto.transaction.CariTransactionResponse;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;
import com.mtb.app.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("cdaTransactionHandler")
public class CdaTransactionHandler implements TransactionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CdaTransactionHandler.class);

    private final CariClient cariClient;
    private final TransactionMapper mapper;
    private final TransactionRepository repository;

    public CdaTransactionHandler(CariClient cariClient, TransactionMapper mapper, TransactionRepository repository) {
        this.cariClient = cariClient;
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public CreateTransactionResponse moveFunds(Transaction transaction) {

        // Creates a Request to Cari
        CariTransactionRequest cariTransactionRequest = mapper.toCariTransactionRequest(transaction);

        // Calls Cari endpoint
        CariTransactionResponse cariTransactionResponse = cariClient.executeTransaction(cariTransactionRequest);

        // Populates Entity with Cari Response and Persists database
        TransactionEntity transactionEntity = mapper.toTransactionEntity(transaction, cariTransactionResponse, cariTransactionRequest);
        repository.save(transactionEntity);
        logger.info("Entity persisted into database: {}", transactionEntity);

        // Returns Cari Response to the Controller
        return mapper.toCdaTransaction(cariTransactionResponse);
    }
}
