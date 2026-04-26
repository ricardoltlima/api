package com.mtb.app.service;

import com.mtb.app.error.ApiException;
import com.mtb.app.error.ElementNotFoundException;
import com.mtb.app.model.CdaAccount;
import com.mtb.app.model.CdaTransaction;
import com.mtb.app.model.TransactionOperations;
import com.mtb.app.model.TransactionStatus;
import com.mtb.app.model.dto.transaction.ConfirmTransactionRequest;
import com.mtb.app.model.dto.transaction.CreateTransactionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransactionService {

    private final CdaAccountService cdaAccountService;
    private final Map<String, CdaTransaction> transactions = new ConcurrentHashMap<>();

    public TransactionService(CdaAccountService cdaAccountService) {
        this.cdaAccountService = cdaAccountService;
    }

    public CdaTransaction createMint(String bankCdaId, CreateTransactionRequest request) {
        CdaAccount account = requireActiveAccount(bankCdaId);
        BigDecimal amount = parseAmount(request.tokenAmount());
        return createPendingTransaction(TransactionOperations.MINT, account, amount, null, request.checkTransactionId());
    }

    public CdaTransaction createBurn(String bankCdaId, CreateTransactionRequest request) {
        CdaAccount account = requireActiveAccount(bankCdaId);
        BigDecimal amount = parseAmount(request.tokenAmount());
        if (account.getTokenBalance().compareTo(amount) < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INSUFFICIENT_BALANCE", "Wallet balance is less than requested burn amount");
        }
        return createPendingTransaction(TransactionOperations.BURN, account, amount, null, request.checkTransactionId());
    }

    public CdaTransaction createTransfer(String bankCdaId, CreateTransactionRequest request) {
        CdaAccount account = requireActiveAccount(bankCdaId);
        BigDecimal amount = parseAmount(request.tokenAmount());
        if (request.destinationWalletAddress() == null || request.destinationWalletAddress().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "destination_wallet_address is required");
        }
        if (account.getTokenBalance().compareTo(amount) < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INSUFFICIENT_BALANCE", "Source wallet balance is less than token amount");
        }
        return createPendingTransaction(TransactionOperations.INTERBANK, account, amount, request.destinationWalletAddress(), request.checkTransactionId());
    }

    public CdaTransaction confirm(String bankCdaId, String cariTxnId, ConfirmTransactionRequest request) {
        CdaTransaction transaction = transactions.get(cariTxnId);
        if (transaction == null || !transaction.getBankCdaId().equals(bankCdaId)) {
            throw new ElementNotFoundException("cariTxnId", "Transaction not found");
        }
        if (transaction.getStatus() == TransactionStatus.CONFIRMED) {
            return transaction;
        }

        CdaAccount source = new CdaAccount(); //TODO Mock it
        if (transaction.getOperation() == TransactionOperations.MINT) {
            source.setTokenBalance(source.getTokenBalance().add(transaction.getTokenAmount()));
        } else if (transaction.getOperation() == TransactionOperations.BURN) {
            source.setTokenBalance(source.getTokenBalance().subtract(transaction.getTokenAmount()));
        } else {
            source.setTokenBalance(source.getTokenBalance().subtract(transaction.getTokenAmount()));
        }

        transaction.setBankTransactionId(request.bankTransactionId());
        transaction.setStatus(TransactionStatus.CONFIRMED);
        transaction.setOdfiStatus("confirmed");
        transaction.setRdfiStatus("confirmed");
        transaction.setUpdatedAt(OffsetDateTime.now());
        return transaction;
    }

    public List<CdaTransaction> getUnconfirmedTransactions() {
        return transactions.values().stream()
                .filter(transaction -> transaction.getStatus() == TransactionStatus.PENDING_CONFIRMATION)
                .toList();
    }

    public CdaTransaction getTransaction(String cariTxnId) {
        CdaTransaction transaction = transactions.get(cariTxnId);
        if (transaction == null) {
            throw new ElementNotFoundException("cariTxnId", "Transaction not found");
        }
        return transaction;
    }

    private CdaTransaction createPendingTransaction(TransactionOperations operation,
                                                   CdaAccount account,
                                                   BigDecimal amount,
                                                   String destinationWalletAddress,
                                                   String checkTransactionId) {
        OffsetDateTime now = OffsetDateTime.now();
        CdaTransaction transaction = new CdaTransaction();
        transaction.setCariTxnId("ctxn_" + UUID.randomUUID());
        transaction.setOperation(operation);
        transaction.setBankCdaId(account.getBankCdaId());
        transaction.setSourceWalletAddress(account.getWalletAddress());
        transaction.setDestinationWalletAddress(destinationWalletAddress);
        transaction.setTokenAmount(amount);
        transaction.setStatus(TransactionStatus.PENDING_CONFIRMATION);
        transaction.setOdfiStatus("pending");
        transaction.setRdfiStatus(destinationWalletAddress == null ? null : "pending");
        transaction.setCheckTransactionId(checkTransactionId);
        transaction.setCreatedAt(now);
        transaction.setUpdatedAt(now);
        transactions.put(transaction.getCariTxnId(), transaction);
        return transaction;
    }

    private CdaAccount requireActiveAccount(String bankCdaId) {
        CdaAccount account = new CdaAccount();// TODO Mock it
        if (!"active".equals(account.getState())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "CDA_NOT_ACTIVE", "CDA account is not active");
        }
        return account;
    }

    private BigDecimal parseAmount(String tokenAmount) {
        if (tokenAmount == null || tokenAmount.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_AMOUNT", "token_amount is required");
        }
        if (!tokenAmount.matches("\\d+(\\.\\d{1,2})?")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_AMOUNT", "token_amount must be a non-negative decimal with up to two decimal places");
        }
        try {
            BigDecimal amount = new BigDecimal(tokenAmount).setScale(2, RoundingMode.UNNECESSARY);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_AMOUNT", "token_amount must be greater than zero");
            }
            return amount;
        } catch (ArithmeticException | NumberFormatException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_AMOUNT", "token_amount must be a non-negative decimal with up to two decimal places");
        }
    }
}
