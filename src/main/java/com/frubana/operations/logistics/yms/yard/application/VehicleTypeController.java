package com.frubana.operations.logistics.yms.yard.application;

import com.frubana.operations.logistics.yms.common.configuration.FormattedLogger;
import com.frubana.operations.logistics.yms.common.utils.JsonUtils;
import com.frubana.operations.logistics.yms.yard.domain.Yard;
import com.frubana.operations.logistics.yms.yard.service.VehicleTypeService;
import com.frubana.operations.logistics.yms.yard.service.YardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.springframework.http.ResponseEntity.status;

/** Entry point for Some Controller.
 */
@RestController
@RequestMapping("/yms/vehicle-type")
public class VehicleTypeController {

    /** Logger. */
    private final Logger logger =
            LoggerFactory.getLogger(VehicleTypeController.class);

    /** Formatter to set the log in a specific format and add the body as part
     * of the same log. */
    private final FormattedLogger logFormatter;


    /** The jackson's object mapper, it's never null. */
    private final VehicleTypeService vehicleTypeService;



    /** Creates a new instance of the controller.
     *
     * @param vehicleTypeService   The service used to process the requests,
     *                         required.
     * @param logFormatter     The formatter utility to log errors, required.
     */
    @Autowired
    public VehicleTypeController(VehicleTypeService vehicleTypeService,
                                 FormattedLogger logFormatter) {
        this.vehicleTypeService = vehicleTypeService;
        this.logFormatter = logFormatter;

    }


    /**
     * Returns all the Vehicles Types
     * @return A JSON representing a some object:
     * <code>
     * {@link List}  of {@link String}
     * </code>
     */
    @GetMapping(
            value =  "/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getAllVehicleTypes() {
        logFormatter.logInfo(logger, "getAllVehicleTypes",
                "Received request", null);
        List<String> vehiclesTypes = vehicleTypeService.getAllVehicleTypes();
        if(vehiclesTypes != null)
            return status(HttpStatus.OK).body(vehiclesTypes);
        else
            return status(HttpStatus.NOT_FOUND)
                    .body("not vehicle types Found");
    }



    /** Generates the yard.
     *
     * @param vehicleType the Vehicle type json to be persisted in the
     *                   repository, cannot be  null.
     * @return A JSON response with a message and status:
     * <code>
     * {
     * "message": "created",
     * "status": 201
     * }
     * </code>
     */
    @PostMapping(
            value = "/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> register(
            @RequestBody final HashMap<String,Object> vehicleType) {
        //Logging the given info

        HashMap<String, Object> params = new HashMap<>();
        params.put("vehicleType", vehicleType);
        logFormatter.logInfo(logger, "registerVehicleType",
                "Received request", params);
        if (vehicleType == null) {
            return status(HttpStatus.BAD_REQUEST).body(
                    JsonUtils.jsonResponse(HttpStatus.BAD_REQUEST,
                            "The vehicleType cannot be null"));
        }
        return status(HttpStatus.CREATED).body(
                vehicleTypeService.registerVehicleType(vehicleType.get("name")
                        .toString())
        );


    }
}
