package com.mtb.app.client;

import com.mtb.app.model.dto.transaction.FundTransferInput;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;

class HoganClientTest {

    @Test
    void transferMoneyUsingHoganRestLogsRequestWithoutThrowing() {
        HoganClient client = new HoganClient();
        FundTransferInput input = new FundTransferInput("from", "to", new BigDecimal("10.00"), Map.of("payment_serial_number", "12345"));

        assertThatCode(() -> client.transferMoneyUsingHoganRest(input, BigInteger.valueOf(9702L), 1L, new BigInteger("123456")))
                .doesNotThrowAnyException();
    }
}
