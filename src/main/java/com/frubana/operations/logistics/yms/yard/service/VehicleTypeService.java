package com.frubana.operations.logistics.yms.yard.service;


import com.frubana.operations.logistics.yms.yard.domain.repository.VehicleTypeRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class VehicleTypeService {

    /** The name that represents this service. MUST NOT BE CHANGED. */
    public static final String SERVICE_NAME = "yms";


    /** The repository to ask for data, it's never null. */
    private final VehicleTypeRepository repository;


    /** Constructor.
     *
     * @param repository        Repository to persists or extract the needed
     *                          data of the tasks.
     */
    @Autowired
    public VehicleTypeService(VehicleTypeRepository repository) {
        this.repository = repository;
    }



    /**
     * get a list of vehicle types.
     */
    @Transactional
    @Retry(name = SERVICE_NAME)
    @CircuitBreaker(name = SERVICE_NAME)
    public List<String> getAllVehicleTypes() {
        return repository.getAll();
    }

    /**
     * Save a vehicle type in the repository
     * @param name the name of the vehicle type
     */
    @Transactional
    @Retry(name = SERVICE_NAME)
    @CircuitBreaker(name = SERVICE_NAME)
    public int registerVehicleType(String name) {
        return this.repository.register(name);
    }

}
