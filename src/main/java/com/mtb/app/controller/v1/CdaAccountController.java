package com.mtb.app.controller.v1;

import com.mtb.app.model.dto.cda.CreateCdaAccountRequest;
import com.mtb.app.model.dto.cda.CreateCdaAccountResponse;
import com.mtb.app.service.CdaAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CdaAccountController {

    private final CdaAccountService cdaAccountService;

    public CdaAccountController(CdaAccountService cdaAccountService) {
        this.cdaAccountService = cdaAccountService;
    }

    @PostMapping("/cda-accounts")
    public ResponseEntity<CreateCdaAccountResponse> createCdaAccount(@Valid @RequestBody CreateCdaAccountRequest request) {
        CreateCdaAccountResponse account = cdaAccountService.createCdaAccount(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(account);
    }
}
