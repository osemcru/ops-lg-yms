package com.frubana.operations.warehouse.change_me.common.clients.report_data_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frubana.operations.test.utils.ValidationUtils;
import com.frubana.operations.warehouse.change_me.common.configuration
        .FormattedLogger;
import com.frubana.operations.warehouse.change_me.common.utils.JsonUtils;
import com.frubana.operations.warehouse.change_me.yard.domain.Yard;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/** Tests the client to request the orders.
 */
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {FormattedLogger.class,
        Client.class, RestTemplate.class})
@TestPropertySource(properties = {
        "frubana.report_data_service.url_co = host/",
        "frubana.report_data_service.orders = path/",
})
public class ClientTest {

    // Subject.
    @SpyBean private Client client;
    // Resource loader to get the JSON files for testing.
    @Autowired private ResourceLoader resourceLoader;

    // Mocks
    @MockBean private RestTemplate restTemplate;

    // Captors
    @Captor private ArgumentCaptor<String> stringCaptor;
    @Captor private ArgumentCaptor<HttpMethod> methodCaptor;

    // Test data
    private String frubanaOrdersJson;
    private String SomeObjectsFromOrdersJson;

    @BeforeAll
    void prepareTestData() throws IOException {
        try (InputStream stream = resourceLoader.getResource("classpath:" +
                "frubana/orders/reports_main_orders.json")
                .getInputStream()) {
            frubanaOrdersJson = new String(
                    FileCopyUtils.copyToByteArray(stream),
                    StandardCharsets.UTF_8);
        }

        try (InputStream stream = resourceLoader.getResource("classpath:" +
                "change_me/some/some_objects_from_orders.json")
                .getInputStream()) {
            SomeObjectsFromOrdersJson = new String(
                    FileCopyUtils.copyToByteArray(stream),
                    StandardCharsets.UTF_8);
        }
    }

    /**
     * Test for the requestFrubanaProducts method, when all goes well
     */
    @Test
    public void requestFrubanaOrdersTest() throws JsonProcessingException {
        //Prepare the correct json response
        ResponseEntity<String> response =
                new ResponseEntity<>(frubanaOrdersJson,
                        HttpStatus.OK);
        when(restTemplate.exchange(anyString(),
                any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        //Subject of the test
        List<Yard> result = client.requireSomeObjects("PA", "mah");

        // Validate that the logic is being called as expected.
        verify(restTemplate, times(1))
                .exchange(stringCaptor.capture(), methodCaptor.capture(),
                        any(), any(ParameterizedTypeReference.class));

        // Validating response
        assertTrue(stringCaptor.getValue()
                        .contains("host/path/BOG/mah"),
                "The Url is not the one expected");
        assertEquals(HttpMethod.GET, methodCaptor.getValue(),
                "The method used to call the API is not the expected.");
        ObjectMapper objectMapper= JsonUtils.instance;
        JsonNode expectedNode = objectMapper.readValue(
                SomeObjectsFromOrdersJson, JsonNode.class);
        JsonNode generatedNode = objectMapper.readValue(
                objectMapper.writeValueAsString(result), JsonNode.class);

        ValidationUtils.compareWithExpected(expectedNode,
                generatedNode, null);
    }

    /**
     * Test for the requestFrubanaProducts method, when all goes well
     */
    @Test
    public void requestFrubanaOrdersErrorTest()
            throws JsonProcessingException {
        //Prepare the correct json response
        ResponseEntity<String> response =
                new ResponseEntity<>("error",
                        HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(anyString(),
                any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        //Subject of the test
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> client.requireSomeObjects("PA", "mah"));

        // Validate that the logic is being called as expected.
        verify(restTemplate, times(1))
                .exchange(stringCaptor.capture(), methodCaptor.capture(),
                        any(), any(ParameterizedTypeReference.class));

        // Validating response
        assertTrue(stringCaptor.getValue()
                        .contains("host/path/BOG/mah"),
                "The Url is not the one expected");
        assertEquals(HttpMethod.GET, methodCaptor.getValue(),
                "The method used to call the API is not the expected.");
        assertTrue(exception.getMessage().contains("Unexpected Frubana response"),
                "The exception message is not the expected one");

        assertSame(IllegalArgumentException.class, exception.getClass(),
                "Unexpected exception type");
    }

    /**
     * Test for the requestFrubanaProducts method, when all goes well
     */
    @Test
    public void requestFrubanaOrdersBadResponseTest()
            throws JsonProcessingException {
        //Prepare the correct json response
        ResponseEntity<String> response =
                new ResponseEntity<>("Invalid Body",
                        HttpStatus.OK);
        when(restTemplate.exchange(anyString(),
                any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        //Subject of the test
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> {client.requireSomeObjects("PA", "mah");});

        // Validate that the logic is being called as expected.
        verify(restTemplate, times(1))
                .exchange(stringCaptor.capture(), methodCaptor.capture(),
                        any(), any(ParameterizedTypeReference.class));

        // Validating response
        assertTrue(stringCaptor.getValue().contains("host/path/BOG/mah"),
                "The Url is not the one expected");
        assertEquals(HttpMethod.GET, methodCaptor.getValue(),
                "The method used to call the API is not the expected.");
        assertTrue(exception.getMessage().contains("is not a valid"),
                "The exception message is not the expected one");
        assertSame(IllegalArgumentException.class, exception.getClass(),
                "Unexpected exception type");
    }
}
