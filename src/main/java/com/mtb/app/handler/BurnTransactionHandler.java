package com.mtb.app.handler;

import com.mtb.app.model.CdaTransaction;
import com.mtb.app.model.dto.transaction.CreateTransactionRequest;
import org.springframework.stereotype.Component;

@Component("burnTransactionHandler")
public class BurnTransactionHandler implements Transaction {

    @Override
    public CdaTransaction moveFunds(CreateTransactionRequest request) {
        return new CdaTransaction();
    }

}
