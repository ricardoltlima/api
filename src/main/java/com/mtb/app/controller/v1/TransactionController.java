package com.mtb.app.controller.v1;

import com.mtb.app.mapper.TransactionMapper;
import com.mtb.app.model.CdaTransaction;
import com.mtb.app.model.dto.transaction.ConfirmTransactionRequest;
import com.mtb.app.model.dto.transaction.ConfirmTransactionResponse;
import com.mtb.app.model.dto.transaction.CreateTransactionRequest;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;
import com.mtb.app.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
@Validated
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    public TransactionController(TransactionService transactionService, TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }

    @PostMapping("/cda-accounts/{bankCdaId}/mint")
    public CreateTransactionResponse mint(@RequestHeader("x-api-key") String apiKey,
                                          @RequestHeader("Idempotency-Key") String idempotencyKey,
                                          @PathVariable String bankCdaId,
                                          @Valid @RequestBody CreateTransactionRequest request) {
        CdaTransaction transaction = transactionService.createMint(bankCdaId, request);
        return transactionMapper.toCreateTransactionResponse(transaction);
    }

    @PostMapping("/cda-accounts/{bankCdaId}/burn")
    public CreateTransactionResponse burn(@RequestHeader("x-api-key") String apiKey,
                                          @RequestHeader("Idempotency-Key") String idempotencyKey,
                                          @PathVariable String bankCdaId,
                                          @Valid @RequestBody CreateTransactionRequest request) {
        CdaTransaction transaction = transactionService.createBurn(bankCdaId, request);
        return transactionMapper.toCreateTransactionResponse(transaction);
    }

    @PostMapping("/cda-accounts/{bankCdaId}/transfer")
    public CreateTransactionResponse transfer(@RequestHeader("x-api-key") String apiKey,
                                              @RequestHeader("Idempotency-Key") String idempotencyKey,
                                              @PathVariable String bankCdaId,
                                              @Valid @RequestBody CreateTransactionRequest request) {
        CdaTransaction transaction = transactionService.createTransfer(bankCdaId, request);
        return transactionMapper.toCreateTransactionResponse(transaction);
    }

    @PostMapping("/cda-accounts/{bankCdaId}/transactions/{cariTxnId}/confirm")
    public ConfirmTransactionResponse confirm(@RequestHeader("x-api-key") String apiKey,
                                              @PathVariable String bankCdaId,
                                              @PathVariable String cariTxnId,
                                              @Valid @RequestBody ConfirmTransactionRequest request) {
        CdaTransaction transaction = transactionService.confirm(bankCdaId, cariTxnId, request);
        return new ConfirmTransactionResponse(transaction.getCariTxnId(), true, transaction.getStatus().name());
    }

    @GetMapping("/reconciliation/unconfirmed-transactions")
    public List<CreateTransactionResponse> unconfirmedTransactions(@RequestHeader("x-api-key") String apiKey) {
        return transactionService.getUnconfirmedTransactions().stream()
                .map(transactionMapper::toCreateTransactionResponse)
                .toList();
    }

}
