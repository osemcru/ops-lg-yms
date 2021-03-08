package com.frubana.operations.warehouse.change_me.common.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frubana.operations.warehouse.change_me.common.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/** Formats the logging to add specific fields to the log.
 */
@Component
public class FormattedLogger {
    /** Prepares the logger additional fields for the pattern.
     *
     * @param origin method that originated the log. Required.
     * @param data   map with all the data logged like params, results,
     *               objects, etc.
     * @throws JsonProcessingException If the given map cannot be converted
     *                                 into a JSON string.
     */
    private void prepareBaseLogger(String origin, Map<String, Object> data)
            throws JsonProcessingException {

        String threadName = Thread.currentThread().getName();
        String logId;
        if (!threadName.startsWith("CHANGE-ME-")) {
            logId = "CHANGE-ME-" + UUID.randomUUID().toString();
            Thread.currentThread().setName(logId);
        }

        MDC.put("origin", origin);
        MDC.put("service", "labels");

        ObjectMapper mapper = JsonUtils.instance;
        if (data != null && !data.isEmpty()) {
            String paramsJson = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(data);
            MDC.put("data", paramsJson);
        } else {
            MDC.put("data", "No data given");
        }
    }

    /** Logs the info level of the actual configured logger setting the MDC
     * to be used by the logger pattern with the service, data, logId and
     * origin.
     *
     * @param logger  logger of the class that wants to log.
     * @param origin  the origin method that wants to create the log.
     * @param message message to log.
     * @param params  the additional params, data, objects, etc.
     */
    public void logInfo(Logger logger, String origin, String message,
                          Map<String, Object> params) {
        try {
            prepareBaseLogger(origin, params);
            logger.info(message);
        }catch (JsonProcessingException e) {
            logger.info(message);
            logger.warn("Impossible to log the params and results as are " +
                    "not valid JSON formats.");
        } finally {
            MDC.clear();
        }
    }

    /** Logs the error level of the actual configured logger setting the MDC
     * to be used by the logger pattern with the service, data, logId and
     * origin.
     *
     * @param logger  logger of the class that wants to log.
     * @param origin  the origin method that wants to create the log. Required.
     * @param message message to log. Required.
     * @param params  the additional params, data, objects, etc.
     * @param ex      The generated exception when the error was catch, if
     *                null no exception is added.
     */
    public void logError(Logger logger, String origin, String message,
                           Map<String, Object> params, Throwable ex) {
        try {
            prepareBaseLogger(origin, params);
            if (ex != null) {
                logger.error(message, ex);
            } else {
                logger.error(message);
            }
        }catch (JsonProcessingException e) {
            logger.error(message, ex);
            logger.warn("Impossible to log the params and results as are " +
                    "not valid JSON formats.");
        } finally {
            MDC.clear();
        }
    }

    /** Logs the debug level of the actual configured logger setting the MDC
     * to be used by the logger pattern with the service, data, logId and
     * origin.
     *
     * @param logger  logger of the class that wants to log.
     * @param origin  the origin method that wants to create the log. Required.
     * @param message message to log. Required.
     * @param params  the additional params, data, objects, etc.
     * @param ex      The generated exception when the error was catch, if
     *                null no exception is added.
     */
    public void logDebug(Logger logger, String origin, String message,
                           Map<String, Object> params, Throwable ex) {
        try {
            prepareBaseLogger(origin, params);
            if (ex != null) {
                logger.debug(message, ex);
            } else {
                logger.debug(message);
            }
        }catch (JsonProcessingException e) {
            logger.error(message, ex);
            logger.warn("Impossible to log the params and results as are " +
                    "not valid JSON formats.");
        } finally {
            MDC.clear();
        }
    }
}
