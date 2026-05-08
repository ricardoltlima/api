package com.mtb.app.handler;

import com.mtb.app.model.Transaction;
import com.mtb.app.model.TransactionOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("transactionFactory")
public class TransactionFactory {

    private static final Logger logger = LoggerFactory.getLogger(TransactionFactory.class);

    @Qualifier("mintTransactionHandler")
    private final MintTransactionHandler mintTransactionHandler;

    @Qualifier("burnTransactionHandler")
    private final BurnTransactionHandler burnTransactionHandler;

    @Qualifier("transferHandler")
    private final TransferHandler transferHandler;

    public TransactionFactory(MintTransactionHandler mintTransactionHandler,
                              BurnTransactionHandler burnTransactionHandler,
                              TransferHandler transferHandler) {

        this.mintTransactionHandler = mintTransactionHandler;
        this.burnTransactionHandler = burnTransactionHandler;
        this.transferHandler = transferHandler;
    }

    public TransactionHandler getTransactionType(Transaction transaction) {

        TransactionOperations type = transaction.getOperation();
        logger.info("Transaction Type: {}", type);

        return switch (type) {
            case MINT -> mintTransactionHandler;
            case BURN -> burnTransactionHandler;
            case ON_US, INTERBANK -> transferHandler;
        };
    }

}
