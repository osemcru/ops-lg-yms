package com.frubana.operations.logistics.yms.yard.domain.repository;

import com.frubana.operations.logistics.yms.yard.domain.Yard;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.core.statement.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VehicleTypeRepository {

    private static final String insertInVehicleType  = "INSERT INTO " +
                                        "vehicle_type (name) VALUES (:name)  ";


    private static final String getInVehicleType  = "SELECT name " +
            "FROM vehicle_type ";

    /** The JDBI instance to request data to the database, it's never null. */
    private final Jdbi dbi;

    /** Base constructor of the repository.
     *
     * @param jdbi the JDBI instance to use in the queries.
     */
    @Autowired
    public VehicleTypeRepository(Jdbi jdbi) {
        this.dbi = jdbi;
    }


    /***
     * Retrieve all the names from the vehicle_type table
     * @return a list with the names.
     */
    public List<String> getAll()
    {
        String sql_query = VehicleTypeRepository.getInVehicleType;
        try (Handle handler = dbi.open();
            Query query_string = handler.createQuery(sql_query)) {
            List<String> types = query_string.mapTo(String.class).list();
            handler.close();
            return types;
        }
    }

    /***
     * Create an instance of vehicle type in DB
     * @param name the type name
     * @return the id generated on insert
     */
    public int register(String name){
        try(Handle handler=dbi.open();
            Update query_string = handler.createUpdate(VehicleTypeRepository.
                    insertInVehicleType)){
            query_string
                    .bind("name",name);
            int vehicle_type_id=query_string
                    .executeAndReturnGeneratedKeys("id")
                    .mapTo(int.class).first();
            handler.close();

            return vehicle_type_id ;

        }
    }
}