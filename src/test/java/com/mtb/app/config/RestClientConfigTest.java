package com.mtb.app.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class RestClientConfigTest {

    @Test
    void createsRestClientAndRestTemplateBeans() {
        RestClientConfig config = new RestClientConfig();
        ReflectionTestUtils.setField(config, "connectTimeoutMillis", 100);
        ReflectionTestUtils.setField(config, "readTimeoutMillis", 200);

        assertThat(config.restClient()).isNotNull();
        assertThat(config.restTemplate()).isNotNull();
    }
}
