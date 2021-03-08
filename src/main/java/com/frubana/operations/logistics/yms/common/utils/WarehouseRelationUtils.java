package com.frubana.operations.logistics.yms.common.utils;

import java.util.HashMap;
import java.util.Map;

/** The legacy needed relations to identify by the warehouse the region and
 * country.
 * TODO: this is only while multi warehouse is released.
 */
public class WarehouseRelationUtils {
    /** The warehouse registered in our systems. */
    private static final Map<String, WarehouseRelation> warehouses =
            new HashMap<>();

    // Registering the warehouses.
    static {
        warehouses.put("PA",
                new WarehouseRelation("Puente aranda", "co", "BOG"));
    }

    /** Constructor, private as this must be accessed static. */
    private WarehouseRelationUtils() {}

    /** Obtain the warehouse relations using the unique ID used in the WMS to
     * identify the warehouse.
     *
     * @param warehouse The warehouse that we want to obtain the relation,
     *                  must not be none or empty.
     * @return the relation found for the given warehouse, None if the given
     * id does not exists.
     * @throws IllegalArgumentException if the given warehouse ID is null or
     * empty.
     */
    public static WarehouseRelation getRelation(String warehouse) {
        if (warehouse == null || warehouse.isBlank()) {
            throw new IllegalArgumentException("The warehouse id given to " +
                    "find the relation cannot be null or empty.");
        }
        return warehouses.get(warehouse);
    }

    /** The relation used to obtain the region and country of a warehouse.
     */
    public static class WarehouseRelation {
        /** The detailed name used in our systems. */
        private final String name;

        /** The country where this warehouse relation exists. */
        private final String country;

        /** The region or city where this warehouse relation exists. */
        private final String region;

        /** Constructor.
         *
         * @param name    The name of the warehouse, cannot be null or empty.
         * @param country The country of the warehouse, cannot be null or empty.
         * @param region  The region of the warehouse, cannot be null or empty.
         */
        public WarehouseRelation(String name, String country, String region) {
            this.name = name;
            this.country = country;
            this.region = region;
            validate();
        }

        /** Validates if this object is valid or not.
         *
         * @throws IllegalArgumentException If the region or country are null
         * or empty.
         */
        private void validate() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("The name cannot be " +
                        "null or empty.");
            }
            if (country == null || country.isBlank()) {
                throw new IllegalArgumentException("The country cannot be " +
                        "null or empty.");
            }
            if (region == null || region.isBlank()) {
                throw new IllegalArgumentException("The region cannot be " +
                        "null or empty.");
            }
        }

        /** Obtains the country of this relation.
         *
         * @return The country where the warehouse is related.
         */
        public String getCountry() { return country; }

        /** Obtains the region of this relation.
         *
         * @return The region where the warehouse is related.
         */
        public String getRegion() { return region; }
    }
}