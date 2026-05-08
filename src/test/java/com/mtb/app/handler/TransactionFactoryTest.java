package com.mtb.app.handler;

import com.mtb.app.model.Transaction;
import com.mtb.app.model.TransactionOperations;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TransactionFactoryTest {

    private final MintTransactionHandler mintTransactionHandler = mock(MintTransactionHandler.class);
    private final BurnTransactionHandler burnTransactionHandler = mock(BurnTransactionHandler.class);
    private final TransferHandler transferHandler = mock(TransferHandler.class);
    private final TransactionFactory factory = new TransactionFactory(mintTransactionHandler, burnTransactionHandler, transferHandler);

    @Test
    void returnsMintHandlerForMintOperation() {
        assertThat(factory.getTransactionType(transaction(TransactionOperations.MINT))).isSameAs(mintTransactionHandler);
    }

    @Test
    void returnsBurnHandlerForBurnOperation() {
        assertThat(factory.getTransactionType(transaction(TransactionOperations.BURN))).isSameAs(burnTransactionHandler);
    }

    @Test
    void returnsTransferHandlerForTransferOperations() {
        assertThat(factory.getTransactionType(transaction(TransactionOperations.ON_US))).isSameAs(transferHandler);
        assertThat(factory.getTransactionType(transaction(TransactionOperations.INTERBANK))).isSameAs(transferHandler);
    }

    private Transaction transaction(TransactionOperations operation) {
        Transaction transaction = new Transaction();
        transaction.setOperation(operation);
        return transaction;
    }
}
