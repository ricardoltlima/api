package com.mtb.app.handler;

import com.mtb.app.model.Transaction;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;

public interface TransactionHandler {

    CreateTransactionResponse moveFunds(Transaction transaction);
}
