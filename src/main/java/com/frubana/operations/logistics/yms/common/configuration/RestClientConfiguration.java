package com.frubana.operations.logistics.yms.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/** Configures the REST client for the WMS service.
 */
@Configuration
public class RestClientConfiguration {

    /** Configures the template to use in the app.
     *
     * @return The configured template.
     */
    @Bean
    public RestTemplate restTemplate() { return new RestTemplate(); }
}
