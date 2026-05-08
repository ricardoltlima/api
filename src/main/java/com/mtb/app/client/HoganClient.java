package com.mtb.app.client;

import com.mtb.app.error.AccountServicesException;
import com.mtb.app.model.dto.transaction.FundTransferInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class HoganClient {

    private static final Logger logger = LoggerFactory.getLogger(HoganClient.class);

    public void transferMoneyUsingHoganRest(FundTransferInput request,
                                            BigInteger transactionCode,
                                            Long effectiveDate,
                                            BigInteger accountId) throws AccountServicesException {
        logger.info("Hogan transfer request: {}, transactionCode={}, effectiveDate={}, accountId={}",
                request, transactionCode, effectiveDate, accountId);
    }
}
