package com.mtb.app.model.dto.transaction;

import java.math.BigDecimal;
import java.util.Map;

public record FundTransferInput(
        String fromAccount,
        String toAccount,
        BigDecimal amount,
        Map<String, String> context
) {
}
