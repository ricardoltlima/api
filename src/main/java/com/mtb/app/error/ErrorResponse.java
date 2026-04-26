package com.mtb.app.error;

import java.util.List;

public record ErrorResponse(
        String error,
        String code,
        List<ErrorDetail> details
) {
    public record ErrorDetail(
            String field,
            String message
    ) {
    }
}
