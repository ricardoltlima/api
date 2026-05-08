package com.mtb.app.handler;

import com.mtb.app.model.Transaction;
import com.mtb.app.service.CdaAccountService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TransferHandlerTest {

    @Test
    void moveFundsCurrentlyReturnsNull() {
        TransferHandler handler = new TransferHandler(mock(CdaAccountService.class));

        assertThat(handler.moveFunds(new Transaction())).isNull();
    }
}
