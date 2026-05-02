package com.mtb.app.interceptor;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    private static final String API_KEY = "SECURE_TOKEN";
    private static final String MESSAGE_MISSING_KEY = "Incorrect or missing API key";

    private static final Logger logger = LoggerFactory.getLogger(RequestInterceptor.class);

    @Value("${api.gateway.key}")
    private String propertiesApiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestApiKey = request.getHeader(API_KEY);
        DispatcherType dispatcherType = request.getDispatcherType();

        if (dispatcherType == DispatcherType.REQUEST && !isEmptyOrNull(propertiesApiKey) && !propertiesApiKey.equals(requestApiKey)) {
            logger.debug("{} Rejected HTTP call: {}", MESSAGE_MISSING_KEY, request.getRequestURI());
            throw new Exception(MESSAGE_MISSING_KEY);
        }

        return true;
    }

    boolean isEmptyOrNull(String string) {
        return string == null || string.isEmpty();
    }
}
