package com.mtb.app.controller.v1;

import com.mtb.app.model.dto.transaction.CreateTransactionRequest;
import com.mtb.app.model.dto.transaction.CreateTransactionResponse;
import com.mtb.app.service.TransactionsService;
import com.mtb.app.validation.ValidateTransaction;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TransactionsController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionsController.class);
    private final TransactionsService transactionsService;

    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @Operation(summary = "Transactions API", description = "Process mint, burn, and transfer operations.")
    @PostMapping(value = "/transactions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateTransactionResponse> createTransaction(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                                                       @Valid @RequestBody CreateTransactionRequest createTransactionRequest) {

        logger.info("Received Transaction Request {}", createTransactionRequest);

        // Idempotency-Key must not be empty  or longer than 128 characters
        ValidateTransaction.validateCreateTransactionHeader(idempotencyKey);

        CreateTransactionResponse createTransactionResponse = transactionsService.createTransaction(createTransactionRequest);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(createTransactionResponse);
    }
}
