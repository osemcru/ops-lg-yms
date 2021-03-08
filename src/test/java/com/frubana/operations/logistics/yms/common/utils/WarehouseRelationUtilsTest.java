package com.frubana.operations.logistics.yms.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests of the temporal warehouse relation util.
 */
public class WarehouseRelationUtilsTest {

    /** Tests the normal flow to request a relation.
     */
    @Test
    public void getRelationTest() {
        WarehouseRelationUtils.WarehouseRelation relation =
                WarehouseRelationUtils.getRelation("PA");

        assertEquals(relation.getCountry(), "co",
                "The country is not the expected for the warehouse");
        assertEquals(relation.getRegion(), "BOG",
                "The country is not the expected for the warehouse");

        // null test
        relation =
                WarehouseRelationUtils.getRelation("ERROR");

        assertNull(relation, "Returned a warehouse relation even when " +
                "the id is not registered");
    }

    /** Tests the error flow to request a relation.
     */
    @Test
    public void getRelationErrorTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                WarehouseRelationUtils.getRelation(""));
        assertSame(IllegalArgumentException.class, exception.getClass(),
                "Unexpected exception type");
        assertTrue(exception.getMessage()
                        .contains("relation cannot be null or empty."),
                "Error does not contain the expected message, instead has " +
                        exception.getMessage());
    }

    /** Tests the error flow to request a relation.
     */
    @Test
    public void generateRelationErrorTest() {
        // Name null or empty.
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new WarehouseRelationUtils.WarehouseRelation(null, "a", "b"));
        assertSame(IllegalArgumentException.class, exception.getClass(),
                "Unexpected exception type");
        assertTrue(exception.getMessage()
                        .contains("name cannot be null or empty."),
                "Error does not contain the expected message, instead has " +
                        exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () ->
                new WarehouseRelationUtils.WarehouseRelation("", "a", "b"));
        assertSame(IllegalArgumentException.class, exception.getClass(),
                "Unexpected exception type");
        assertTrue(exception.getMessage()
                        .contains("name cannot be null or empty."),
                "Error does not contain the expected message, instead has " +
                        exception.getMessage());

        // Country null or empty.
        exception = assertThrows(IllegalArgumentException.class, () ->
                new WarehouseRelationUtils.WarehouseRelation("a", null, "b"));
        assertSame(IllegalArgumentException.class, exception.getClass(),
                "Unexpected exception type");
        assertTrue(exception.getMessage()
                        .contains("country cannot be null or empty."),
                "Error does not contain the expected message, instead has " +
                        exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () ->
                new WarehouseRelationUtils.WarehouseRelation("a", "", "b"));
        assertSame(IllegalArgumentException.class, exception.getClass(),
                "Unexpected exception type");
        assertTrue(exception.getMessage()
                        .contains("country cannot be null or empty."),
                "Error does not contain the expected message, instead has " +
                        exception.getMessage());

        // Name null or empty.
        exception = assertThrows(IllegalArgumentException.class, () ->
                new WarehouseRelationUtils.WarehouseRelation("a", "b", null));
        assertSame(IllegalArgumentException.class, exception.getClass(),
                "Unexpected exception type");
        assertTrue(exception.getMessage()
                        .contains("region cannot be null or empty."),
                "Error does not contain the expected message, instead has " +
                        exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () ->
                new WarehouseRelationUtils.WarehouseRelation("a", "b", ""));
        assertSame(IllegalArgumentException.class, exception.getClass(),
                "Unexpected exception type");
        assertTrue(exception.getMessage()
                        .contains("region cannot be null or empty."),
                "Error does not contain the expected message, instead has " +
                        exception.getMessage());


    }
}
