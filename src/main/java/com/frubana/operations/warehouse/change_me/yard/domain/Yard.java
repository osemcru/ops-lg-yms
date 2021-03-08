package com.frubana.operations.warehouse.change_me.yard.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Represents a some object minimum required data to generate a label.
 * <p> This is just a DTO that will be created by Jackson within the
 * application layer.
 * <p> This is an example of the expected JSON.
 * <code>
 * {
 *    "id":"TSK0041377",
 *    "taskStatus":"some status",
 *    "orderId":"OS000001",
 *    "orderIndex":123,
 *    "closeDate":"2020-05-22",
 *    "warehouse":"PA",
 *    "waveLabel":"OLA0000432",
 *    "waveType":"VENTA NORMAL",
 *    "sku":"SKU000001",
 *    "productName":"Onion",
 *    "unit":"KG",
 *    "quantity":300.33,
 *    "zoneId":"1",
 *    "route":"1-1",
 *    "etd":"14:30:30",
 *    "priority":1234567,
 *    "location":"MEH00321",
 *    "customerId":"andersoncalle148",
 *    "customerName":"Krasty Burgers",
 *    "source":"WMS"
 * }
 * </code>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Yard {


    /**
     * Used for be created from the JSON, don't use it in the code!.
     */
    public Yard() {
    }


}