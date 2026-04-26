package com.mtb.app.handler;

import com.mtb.app.model.CdaTransaction;
import com.mtb.app.model.dto.transaction.CreateTransactionRequest;

public interface Transaction {

    CdaTransaction moveFunds(CreateTransactionRequest request);
}
