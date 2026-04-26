package com.mtb.app.controller.v1;

import com.mtb.app.error.ApiKeyException;
import com.mtb.app.mapper.CdaAccountMapper;
import com.mtb.app.model.CdaAccount;
import com.mtb.app.model.dto.cda.CreateCdaAccountRequest;
import com.mtb.app.model.dto.cda.CreateCdaAccountResponse;
import com.mtb.app.model.dto.cda.UpdateCdaStateRequest;
import com.mtb.app.model.dto.cda.UpdateCdaStateResponse;
import com.mtb.app.service.CdaAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class CdaAccountController {

    private final CdaAccountService cdaAccountService;
    private final CdaAccountMapper cdaAccountMapper;

    public CdaAccountController(CdaAccountService cdaAccountService, CdaAccountMapper cdaAccountMapper) {
        this.cdaAccountService = cdaAccountService;
        this.cdaAccountMapper = cdaAccountMapper;
    }

    @PostMapping("/cda-accounts")
    public ResponseEntity<CreateCdaAccountResponse> consolidateOnboarding(@RequestHeader("x-api-key") String apiKey,
                                                                          @Valid @RequestBody CreateCdaAccountRequest createCdaAccountRequest) {

        validateApiKey(apiKey);
        String bankId = extractBankId(apiKey);
        CdaAccount account = cdaAccountService.createCdaAccount(createCdaAccountRequest, bankId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cdaAccountMapper.toCreateCdaAccountResponse(account));
    }

//    @PutMapping("/cda-accounts/{bankCdaId}/state")
//    public ResponseEntity<UpdateCdaStateResponse> updateState(@RequestHeader("x-api-key") String apiKey,
//                                                              @PathVariable String bankCdaId,
//                                                              @Valid @RequestBody UpdateCdaStateRequest request) {
//        validateApiKey(apiKey);
//        CdaAccount account = cdaAccountService.updateState(bankCdaId, request.cdaState());
//        return ResponseEntity.ok(cdaAccountMapper.toUpdateCdaStateResponse(account));
//    }

    private void validateApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ApiKeyException("x-api-key", "must be valid and not null");
        }
    }

    // Mock method
    private String extractBankId(String apiKey) {
        return "bank_from_api_key";
    }

}
