package com.mtb.app.interceptor;

import jakarta.servlet.DispatcherType;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestInterceptorTest {

    @Test
    void preHandleAllowsRequestWhenConfiguredKeyIsEmpty() {
        RequestInterceptor interceptor = new RequestInterceptor();
        ReflectionTestUtils.setField(interceptor, "propertiesApiKey", "");

        assertThatCode(() -> interceptor.preHandle(request(null), new MockHttpServletResponse(), new Object()))
                .doesNotThrowAnyException();
    }

    @Test
    void preHandleAllowsRequestWhenHeaderMatchesConfiguredKey() throws Exception {
        RequestInterceptor interceptor = new RequestInterceptor();
        ReflectionTestUtils.setField(interceptor, "propertiesApiKey", "secret");

        assertThat(interceptor.preHandle(request("secret"), new MockHttpServletResponse(), new Object())).isTrue();
    }

    @Test
    void preHandleRejectsRequestWhenHeaderDoesNotMatchConfiguredKey() {
        RequestInterceptor interceptor = new RequestInterceptor();
        ReflectionTestUtils.setField(interceptor, "propertiesApiKey", "secret");

        assertThatThrownBy(() -> interceptor.preHandle(request("wrong"), new MockHttpServletResponse(), new Object()))
                .isInstanceOf(Exception.class)
                .hasMessage("Incorrect or missing API key");
    }

    private MockHttpServletRequest request(String apiKey) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setDispatcherType(DispatcherType.REQUEST);
        if (apiKey != null) {
            request.addHeader("SECURE_TOKEN", apiKey);
        }
        return request;
    }
}
