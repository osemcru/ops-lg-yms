package com.frubana.operations.warehouse.change_me.health.domain;

import com.frubana.operations.warehouse.change_me.common.utils.SlackUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet
        .AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request
        .MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.status;

import com.frubana.operations.warehouse.change_me.common.clients.SomeClient;

/** Tests the health endpoint of the application
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SomeHealthVerifierIT {
    // Resource loader to get the JSON files for testing.
    @Autowired
    ResourceLoader resourceLoader;

    // Mock client to call the controller endpoints
    @Autowired private MockMvc mockMvc;

    // Mocks
    @MockBean private RestTemplate template;
    @MockBean private SomeClient ordersClient;
    @MockBean private SlackUtils notifier;
    // Captors
    @Captor private ArgumentCaptor<String> warehouseCaptor;
    @Captor private ArgumentCaptor<String> dateCaptor;
    @Captor private ArgumentCaptor<String> errorCaptor;

    /** Tests the normal flow when requesting the health using REST.
     */
    @Test
    void healthzCheckTest() throws Exception {
        when(ordersClient.requireSomeObjects(
                anyString(), anyString())).thenReturn(null);
        mockMvc.perform(get("/change-me/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(ordersClient, times(1)).requireSomeObjects(
                warehouseCaptor.capture(), dateCaptor.capture());
    }

    /** Tests the error flow when requesting the health using REST.
     */
    @Test
    void healthzCheckErrorTest() throws Exception {
        doNothing().when(notifier).logError(anyString());
        when(ordersClient.requireSomeObjects(
                anyString(), anyString())).thenThrow(
                        new IllegalArgumentException("ups!"));
        mockMvc.perform(get("/change-me/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable());

        verify(ordersClient, times(1)).requireSomeObjects(
                warehouseCaptor.capture(), dateCaptor.capture());
        verify(notifier, times(1)).logError(errorCaptor.capture());
    }
}
