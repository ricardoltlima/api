package com.mtb.app.handler;

import com.mtb.app.model.TransactionOperations;
import com.mtb.app.model.dto.transaction.CreateTransactionRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("transactionFactory")
public class TransactionFactory {

    @Qualifier("mintTransactionHandler")
    private final MintTransactionHandler mintTransactionHandler;

    @Qualifier("burnTransactionHandler")
    private final BurnTransactionHandler burnTransactionHandler;

    @Qualifier("transferTransactionHandler")
    private final TransferTransactionHandler transferTransactionHandler;

    public TransactionFactory(MintTransactionHandler mintTransactionHandler,
                              BurnTransactionHandler burnTransactionHandler,
                              TransferTransactionHandler transferTransactionHandler) {
        this.mintTransactionHandler = mintTransactionHandler;
        this.burnTransactionHandler = burnTransactionHandler;
        this.transferTransactionHandler = transferTransactionHandler;
    }

    public Transaction getTransactionType(CreateTransactionRequest request) {

        TransactionOperations type = TransactionOperations.valueOf(request.type().toUpperCase());
        return switch (type) {
            case MINT -> mintTransactionHandler;
            case BURN -> burnTransactionHandler;
            case ON_US, INTERBANK -> transferTransactionHandler;
        };
    }

}
