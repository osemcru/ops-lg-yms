package com.frubana.operations.logistics.yms.common.configuration;

import com.frubana.operations.logistics.yms.common.utils.JsonUtils;
import com.frubana.operations.logistics.yms.common.utils.SlackUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation
        .ResponseEntityExceptionHandler;

import java.util.HashMap;

import static org.springframework.http.ResponseEntity.status;

/** Handler that captures the exceptions and selects the correct exception and
 * code to be send to the one that requested the endpoint.
 */
@ControllerAdvice
public class RestHandlerConfiguration extends ResponseEntityExceptionHandler {

    /** Logger. */
    private final Logger logger =
            LoggerFactory.getLogger(RestHandlerConfiguration.class);

    /** Formatter to set the log in a specific format and add the body as part
     * of the same log, it's never null. */
    private final FormattedLogger logFormatter;

    /** Slack utility to send notifications, it's never null. */
    private final SlackUtils notificationUtil;

    /** Constructor.
     *
     * @param logFormatter     Formatter to set the log, required.
     * @param notificationUtil The Notification utility to communicate
     *                         errors, required.
     */
    @Autowired
    public RestHandlerConfiguration(
            FormattedLogger logFormatter, SlackUtils notificationUtil) {
        this.logFormatter = logFormatter;
        this.notificationUtil = notificationUtil;
    }

    /** {@inheritDoc} */
    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
            MissingPathVariableException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        //Logging the given info
        HashMap<String, Object> params = new HashMap<>();
        params.put("request", request.toString());
        params.put("headers", StringUtils.join(headers));
        params.put("status", status.toString());
        logFormatter.logError(logger, "handleMissingPathVariable",
                "Error in the path variables", params, ex);
        return status(HttpStatus.BAD_REQUEST).body(
                JsonUtils.jsonResponse(HttpStatus.BAD_REQUEST,
                        ex.getMessage()));
    }

    /** {@inheritDoc} */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        //Logging the given info
        HashMap<String, Object> params = new HashMap<>();
        params.put("request", request.toString());
        params.put("headers", StringUtils.join(headers));
        params.put("status", status.toString());
        logFormatter.logError(logger, "handleMissingServletRequestParameter",
                "Error in the request parameters", params, ex);
        return status(HttpStatus.BAD_REQUEST).body(
                JsonUtils.jsonResponse(HttpStatus.BAD_REQUEST,
                        ex.getMessage()));
    }

    /** {@inheritDoc} */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        //Logging the given info
        HashMap<String, Object> params = new HashMap<>();
        params.put("request", request.toString());
        params.put("headers", StringUtils.join(headers));
        params.put("status", status.toString());
        logFormatter.logError(logger, "handleHttpMessageNotReadable",
                "The request cannot be interpreted", params, ex);
        return status(HttpStatus.BAD_REQUEST).body(
                JsonUtils.jsonResponse(HttpStatus.BAD_REQUEST,
                        ex.getMessage()));
    }

    /** {@inheritDoc} */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        //Logging the given info
        HashMap<String, Object> params = new HashMap<>();
        params.put("request", request.toString());
        params.put("headers", StringUtils.join(headers));
        params.put("status", status.toString());
        logFormatter.logError(logger, "handleMethodArgumentNotValid",
                "The given arguments cannot be processed", params, ex);
        return status(HttpStatus.BAD_REQUEST).body(
                JsonUtils.jsonResponse(HttpStatus.BAD_REQUEST,
                        ex.getMessage()));
    }

    /** {@inheritDoc} */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        //Logging the given info
        HashMap<String, Object> params = new HashMap<>();
        params.put("request", request.toString());
        params.put("headers", StringUtils.join(headers));
        params.put("status", status.toString());
        logFormatter.logError(logger, "handleHttpRequestMethodNotSupported",
                "The REST method is not allowed", params, ex);
        return status(HttpStatus.METHOD_NOT_ALLOWED).body(
                JsonUtils.jsonResponse(HttpStatus.METHOD_NOT_ALLOWED,
                        ex.getMessage()));
    }

    /** Handler for a request with invalid data exception.
     *
     * @param ex The exception that caused the error
     * @return The HTTP response with the status and error message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> invalidRequestHandler(
            IllegalArgumentException ex) {

        //Logging the given info
        String message = "Error on illegal argument detected: " +
                ex.getMessage();
        logFormatter.logError(
                logger, "invalidRequestHandler", message, null, ex);

        notificationUtil.logError(message);

        return status(HttpStatus.BAD_REQUEST).body(
                JsonUtils.jsonResponse(HttpStatus.BAD_REQUEST,
                        ex.getMessage()));
    }

    /** Handler for a unexpected exception.
     *
     * @param ex The exception that caused the error.
     * @return The HTTP response with the status and error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> unexpectedExceptionHandler(Exception ex) {
        //Logging the given info
        String message = "Error on unexpected exception detected: " +
                ex.getMessage();

        logFormatter.logError(
                logger, "unexpectedExceptionHandler", message, null, ex);
        notificationUtil.logError(message);

        return status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                JsonUtils.jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                        ex.getMessage()));
    }
}
