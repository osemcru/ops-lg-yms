package com.frubana.operations.logistics.yms.common.utils;

import com.frubana.operations.logistics.yms.common.configuration.FormattedLogger;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/** Tests the client to send notifications to slack.
 */
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {FormattedLogger.class,
        SlackUtils.class, RestTemplate.class})
@TestPropertySource(properties = {
        "log.slack.uri = http://slack.host.com",
})
public class SlackUtilsTest {
    // Subject.
    @SpyBean private SlackUtils notifier;
    // Spy.
    @SpyBean private FormattedLogger logger;

    // Resource loader to get the JSON files for testing.
    @Autowired private ResourceLoader resourceLoader;

    // Mocks
    @MockBean private RestTemplate restTemplate;
    // Captors
    @Captor private ArgumentCaptor<String> stringCaptor;
    @Captor private ArgumentCaptor<String> errorMsgCaptor;
    @Captor private ArgumentCaptor<Map<String, Object>> paramsCaptor;
    @Captor private ArgumentCaptor<Exception> exceptionCaptor;
    @Captor private ArgumentCaptor<HttpMethod> methodCaptor;

    /** Notifications log info tests.
     */
    @Test
    public void notificationsLogInfoTest() {
        //Prepare the correct json response
        ResponseEntity<String> response =
                new ResponseEntity<>("Does not matter", HttpStatus.OK);
        when(restTemplate.exchange(anyString(),
                any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        //Subject of the test
        notifier.logInfo("some message");

        // Validate that the logic is being called as expected.
        verify(restTemplate, times(1))
                .exchange(stringCaptor.capture(), methodCaptor.capture(),
                        any(), any(ParameterizedTypeReference.class));

        // Validating response
        assertTrue(stringCaptor.getValue()
                        .contains("http://slack.host.com"),
                "The Url is not the one expected");
        assertEquals(HttpMethod.POST, methodCaptor.getValue(),
                "The method used to call the API is not the expected.");
    }

    /** Notifications log error tests.
     */
    @Test
    public void notificationsLogErrorTest() {
        //Prepare the correct json response
        ResponseEntity<String> response =
                new ResponseEntity<>("Does not matter", HttpStatus.OK);
        when(restTemplate.exchange(anyString(),
                any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        //Subject of the test
        notifier.logError("some message");

        // Validate that the logic is being called as expected.
        verify(restTemplate, times(1))
                .exchange(stringCaptor.capture(), methodCaptor.capture(),
                        any(), any(ParameterizedTypeReference.class));

        // Validating response
        assertTrue(stringCaptor.getValue()
                        .contains("http://slack.host.com"),
                "The Url is not the one expected");
        assertEquals(HttpMethod.POST, methodCaptor.getValue(),
                "The method used to call the API is not the expected.");
    }

    /** Notifications tests when slack responses other than 200.
     */
    @Test
    public void notificationsNot200ErrorTest() {
        //Prepare the correct json response
        ResponseEntity<String> response =
                new ResponseEntity<>("Does not matter", HttpStatus.NO_CONTENT);
        when(restTemplate.exchange(anyString(),
                any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        //Subject of the test
        notifier.logInfo("some message");

        // Validate that the logic is being called as expected.
        verify(restTemplate, times(1))
                .exchange(stringCaptor.capture(), methodCaptor.capture(),
                        any(), any(ParameterizedTypeReference.class));

        verify(logger, times(1))
                .logError(any(), anyString(),
                        errorMsgCaptor.capture(), paramsCaptor.capture(),
                        exceptionCaptor.capture());

        // Validating response
        assertTrue(stringCaptor.getValue()
                        .contains("http://slack.host.com"),
                "The Url is not the one expected");
        assertEquals(HttpMethod.POST, methodCaptor.getValue(),
                "The method used to call the API is not the expected.");
        // Validate exception was captured.
        Exception exception = exceptionCaptor.getValue();
        assertSame(IllegalArgumentException.class,
                exception.getClass(),
                "Unexpected exception type");
        assertTrue(exception.getMessage()
                        .contains("unexpected status code 204"),
                "Error does not contain the expected message, instead has " +
                        exception.getMessage());
        Map<String, Object> params = paramsCaptor.getValue();
        assertTrue(params.containsKey("url"),
                "The log message does not contains the expected params");
        assertTrue(params.containsKey("message"),
                "The log message does not contains the expected params");
        assertTrue(errorMsgCaptor.getValue()
                        .contains("Couldn't send a message to Slack"),
                "The log message is not the expected one");
    }

    /** Notifications tests when there is a spring rest exception like timeout.
     */
    @Test
    public void notificationsHttpExceptionErrorTest() {
        //Prepare the correct json response
        when(restTemplate.exchange(anyString(),
                any(), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ResourceAccessException("timeout exception"));

        //Subject of the test
        notifier.logError("some message");

        // Validate that the logic is being called as expected.
        verify(restTemplate, times(1))
                .exchange(stringCaptor.capture(), methodCaptor.capture(),
                        any(), any(ParameterizedTypeReference.class));

        verify(logger, times(1))
                .logError(any(), anyString(),
                        errorMsgCaptor.capture(), paramsCaptor.capture(),
                        exceptionCaptor.capture());

        // Validating response
        assertTrue(stringCaptor.getValue()
                        .contains("http://slack.host.com"),
                "The Url is not the one expected");
        assertEquals(HttpMethod.POST, methodCaptor.getValue(),
                "The method used to call the API is not the expected.");
        // Validate exception was captured.
        Exception exception = exceptionCaptor.getValue();
        assertSame(ResourceAccessException.class,
                exception.getClass(),
                "Unexpected exception type");
        assertTrue(exception.getMessage()
                        .contains("timeout exception"),
                "Error does not contain the expected message, instead has " +
                        exception.getMessage());
        Map<String, Object> params = paramsCaptor.getValue();
        assertTrue(params.containsKey("url"),
                "The log message does not contains the expected params");
        assertTrue(params.containsKey("message"),
                "The log message does not contains the expected params");
        assertTrue(errorMsgCaptor.getValue()
                        .contains("Couldn't send a message to Slack"),
                "The log message is not the expected one");
    }
}
