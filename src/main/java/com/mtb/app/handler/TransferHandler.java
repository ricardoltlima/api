package com.mtb.app.handler;

import com.mtb.app.model.Transaction;
import com.mtb.app.model.dto.transaction.CreateTransactionRequest;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;
import com.mtb.app.service.CdaAccountService;
import org.springframework.stereotype.Component;

@Component("transferHandler")
public class TransferHandler implements TransactionHandler {

    private final CdaAccountService cdaAccountService;

    public TransferHandler(CdaAccountService cdaAccountService) {
        this.cdaAccountService = cdaAccountService;
    }

    @Override
    public CreateTransactionResponse moveFunds(Transaction transaction) {

        // Validate Balance
//        ValidateTransaction validateCreateTransaction = new ValidateTransaction(cdaAccountService);
//        validateCreateTransaction.validateBalance(request);

        return null;
    }
}
