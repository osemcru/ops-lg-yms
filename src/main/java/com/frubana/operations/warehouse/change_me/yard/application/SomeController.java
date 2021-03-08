package com.frubana.operations.warehouse.change_me.yard.application;

import com.frubana.operations.warehouse.change_me.common.configuration
        .FormattedLogger;
import com.frubana.operations.warehouse.change_me.common.utils.JsonUtils;
import com.frubana.operations.warehouse.change_me.yard.domain.Yard;
import com.frubana.operations.warehouse.change_me.yard.service.SomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import static org.springframework.http.ResponseEntity.status;

/** Entry point for Some Controller.
 */
@RestController
@RequestMapping("/change-me/something")
public class SomeController {

    /** Logger. */
    private final Logger logger =
            LoggerFactory.getLogger(SomeController.class);

    /** Formatter to set the log in a specific format and add the body as part
     * of the same log. */
    private final FormattedLogger logFormatter;

    /** The task URL, it's never null. */
    public static final String TASK_URL = "/task";

    /** The jackson's object mapper, it's never null. */
    private final SomeService someService;

    /** Creates a new instance of the controller.
     *
     * @param someService   The service used to process the requests,
     *                         required.
     * @param logFormatter     The formatter utility to log errors, required.
     */
    @Autowired
    public SomeController(SomeService someService,
                          FormattedLogger logFormatter) {
        this.someService = someService;
        this.logFormatter = logFormatter;

    }

    /** Returns the task of the given id.
     *
     * @param id        The id of the task to request.
     * @param warehouse The warehouse where the task belongs.
     * @return A JSON representing a some object:
     * <code>
     * {@link Yard}
     * </code>
     */
    @GetMapping(
            value = TASK_URL + "/{warehouse}/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getTask(
            @PathVariable(value = "warehouse") String warehouse,
            @PathVariable(value = "id") String id) {
        //Logging the given info
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("warehouse", warehouse);
        logFormatter.logInfo(logger, "getTask", "Received request", params);

        if (id == null || id.isBlank()) {
            return status(HttpStatus.BAD_REQUEST).body(
                    JsonUtils.jsonResponse(HttpStatus.BAD_REQUEST,
                            "The task cannot be null or empty"));
        }
        if (warehouse == null || warehouse.isBlank()) {
            return status(HttpStatus.BAD_REQUEST).body(
                    JsonUtils.jsonResponse(HttpStatus.BAD_REQUEST,
                            "The warehouse cannot be null or empty"));
        }

        if (someService.exists(id, warehouse)) {
            // Register the task throws an error if something fails.
            Yard task = someService.obtainSomeObject(id, warehouse);
            params.put("task", task);
            logFormatter.logInfo(logger, "obtainSomeObject", "found the task",
                    params);
            return status(HttpStatus.OK).body(task);
        }

        return status(HttpStatus.NO_CONTENT).body(null);
    }

    /** Generates the given report.
     *
     * @param task the some object to be persisted in the repository, cannot be
     *             null.
     * @return A JSON response with a message and status:
     * <code>
     * {
     * "message": "created",
     * "status": 200
     * }
     * </code>
     */
    @PostMapping(
            value = TASK_URL,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> register(
            @RequestBody final Yard task) {
        //Logging the given info
        HashMap<String, Object> params = new HashMap<>();
        params.put("task", task);
        logFormatter.logInfo(logger, "registerSomeObject",
                "Received request", params);
        if (task == null) {
            return status(HttpStatus.BAD_REQUEST).body(
                    JsonUtils.jsonResponse(HttpStatus.BAD_REQUEST,
                            "The task cannot be null"));
        }

        task.validate();

        if (!someService.exists(task.getId(), task.getWarehouse())) {
            // Register the task throws an error if something fails.
            someService.registerSomeObject(task);
            logFormatter.logInfo(logger, "registerSomeObject",
                    "Persisted the task", params);
            return status(HttpStatus.OK).body(
                    JsonUtils.jsonResponse(HttpStatus.BAD_REQUEST,
                            "Registered Task"));
        }

        return status(HttpStatus.BAD_REQUEST).body(
                JsonUtils.jsonResponse(HttpStatus.BAD_REQUEST,
                        "Duplicated Task"));
    }
}
