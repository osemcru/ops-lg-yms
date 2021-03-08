package com.frubana.operations.warehouse.change_me.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.frubana.operations.warehouse.change_me.common.configuration
        .FormattedLogger;

import java.util.HashMap;

/** Slack notification util
 * Sends messages to slack channels using a URI corresponding to a web hook.
 */
@Component
public class SlackUtils {
    /** Logger for the client, it's never null.  */
    private final Logger logger = LoggerFactory.getLogger(SlackUtils.class);

    /** Formatter to set the log in a specific format and add the body as part
     * of the same log. */
    private final FormattedLogger logFormatter;

    /** Rest client to call the external slack endpoints, it's never null. */
    private final RestTemplate restTemplate;

    /** Slack uri with the web hook, it's never null. */
    private final String uri;

    /** Constructor.
     *
     * @param uri Slack uri of the web hook.
     */
    @Autowired
    public SlackUtils(@Value("${log.slack.uri}") String uri,
                      RestTemplate restTemplate, FormattedLogger logFormatter) {
        this.uri = uri;
        this.restTemplate = restTemplate;
        this.logFormatter = logFormatter;
    }

    /** Send a message to slack.
     *
     * @param message message to be sent, cannot be null or empty.
     * @param icon    image string representation to set in the message,
     *                cannot be null or empty.
     */
    private void logMessage(String message, String icon) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Content-Type", "application/json");
            String body = "{\"text\":\"CHANGE ME :: " + icon +
                    message + "\"}";
            HttpEntity<String> entity =
                    new HttpEntity<>(body, headers);
            ParameterizedTypeReference<String> typeRef =
                    new ParameterizedTypeReference<>() {
                    };
            // Send the request to Frubana.
            ResponseEntity<String> response = restTemplate.exchange(
                    uri, HttpMethod.POST, entity, typeRef);
            int statusCode = response.getStatusCode().value();
            if (statusCode != 200) {
                throw new IllegalArgumentException(
                        "Slack responded with an unexpected status code " +
                                statusCode);
            }
        } catch (IllegalArgumentException | RestClientException exception) {
            HashMap<String, Object> logParams = new HashMap<>();
            logParams.put("url", uri);
            logParams.put("message", message);
            logFormatter.logError(logger, "logMessage",
                    "Couldn't send a message to Slack.", logParams, exception);
        }
    }

    /** Sends a error message to slack.
     *
     * @param message message to be sent.
     */
    public void logError(String message) { logMessage(message, ":boom:"); }

    /** Sends a info message to slack.
     *
     * @param message message to be sent.
     */
    public void logInfo(String message) { logMessage(message, ":robot_face:"); }
}
