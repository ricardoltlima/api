package com.mtb.app.handler;

import com.mtb.app.client.CariClient;
import com.mtb.app.client.HoganClient;
import com.mtb.app.entity.AccountEntity;
import com.mtb.app.entity.TransactionEntity;
import com.mtb.app.error.AccountServicesException;
import com.mtb.app.error.ElementNotFoundException;
import com.mtb.app.mapper.TransactionMapper;
import com.mtb.app.model.Transaction;
import com.mtb.app.model.dto.transaction.CariTransactionRequest;
import com.mtb.app.model.dto.transaction.CariTransactionResponse;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;
import com.mtb.app.model.dto.transaction.FundTransferInput;
import com.mtb.app.repository.AccountRepository;
import com.mtb.app.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;

@Component("burnTransactionHandler")
public class BurnTransactionHandler implements TransactionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BurnTransactionHandler.class);
    private static final String PAYMENT_SERIAL_NUMBER_KEY = "payment_serial_number";

    private final CariClient cariClient;
    private final TransactionMapper mapper;
    private final TransactionRepository repository;
    private final HoganClient hoganClient;
    private final AccountRepository accountRepository;

    public BurnTransactionHandler(CariClient cariClient,
                                  TransactionMapper mapper,
                                  TransactionRepository repository,
                                  HoganClient hoganClient,
                                  AccountRepository accountRepository) {
        this.cariClient = cariClient;
        this.mapper = mapper;
        this.repository = repository;
        this.hoganClient = hoganClient;
        this.accountRepository = accountRepository;
    }

    @Override
    public CreateTransactionResponse moveFunds(Transaction transaction) {

        // Gets the internalBankCdaId to send to Cari. The real bank CDA id should not be sent to Cari.
        AccountEntity accountEntity = accountRepository
                .findByIdBankCdaIdAndIdBankCustomerId(transaction.getBankCdaId(), transaction.getCustomerId())
                .orElseThrow(() -> {
                        logger.error("Account not found with id {}", transaction.getBankCdaId());
                        return new ElementNotFoundException("No CDA Found", "CDA_NOT_FOUND", "bank_cda_id", "Account not found for bank CDA and customer");
                });

        // Calls Hogan to credit the account. Transaction Code 9700 is for credit.
        updateAccountBalance(transaction, 9700L);

        // Creates a Request to Cari
        CariTransactionRequest cariTransactionRequest = mapper.toCariTransactionRequest(transaction, accountEntity.getInternalBankCdaId());

        // Calls Cari endpoint
        CariTransactionResponse cariTransactionResponse;
        try {
            cariTransactionResponse = cariClient.executeTransaction(cariTransactionRequest);
        } catch (RestClientException e) {
            // Calls Hogan to debit the account. Transaction Code 9702 is for debit.
            updateAccountBalance(transaction, 9702L);
            logger.error("Error trying to burn account. {}", e.getMessage());
            throw new RestClientException("Error trying to burn account. " + e.getMessage());
        }

        // Populates Entity with Cari Response and Persists database
        TransactionEntity transactionEntity = mapper.toTransactionEntity(transaction, cariTransactionResponse, cariTransactionRequest);
        repository.save(transactionEntity);
        logger.info("burn transaction persisted into database: {}", transactionEntity);

        // Returns Cari Response to the Controller
        return mapper.toCdaTransaction(cariTransactionResponse);
    }

    private void updateAccountBalance(Transaction transaction, Long transactionCode) {
        try {
            Map<String, String> context = Map.of(PAYMENT_SERIAL_NUMBER_KEY, "12345");
            FundTransferInput request = new FundTransferInput(null, null, transaction.getTokenAmount(), context);
            logger.info("Request info: {}", request);

            Long effectiveDate = OffsetDateTime.now(ZoneId.systemDefault()).toInstant().toEpochMilli();
            hoganClient.transferMoneyUsingHoganRest(
                    request,
                    BigInteger.valueOf(transactionCode),
                    effectiveDate,
                    new BigInteger(transaction.getBankDdaLinkedId())
            );
        } catch (RuntimeException | AccountServicesException ex) {
            logger.error("Error trying to connect to Hogan: {}", ex.getMessage());
        }
    }
}
