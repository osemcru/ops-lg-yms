package com.frubana.operations.warehouse.change_me.yard.service;

import com.frubana.operations.warehouse.change_me.common.clients.SomeClient;
import com.frubana.operations.warehouse.change_me.common.utils.SlackUtils;
import com.frubana.operations.warehouse.change_me.health.service.HealthCheck;
import com.frubana.operations.warehouse.change_me.yard.domain.Yard;
import com.frubana.operations.warehouse.change_me.yard.domain.repository.YardRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Service of the some objects Logic.
 */
@Component
public class SomeService implements HealthCheck {
    /** The name that represents this service. MUST NOT BE CHANGED. */
    public static final String SERVICE_NAME = "someService";

    /** Slack utility to send notifications, it's never null. */
    private final SlackUtils notificationUtil;

    /** The client to require external some object, it's never null. */
    private final SomeClient SomeObjectClient;

    /** The repository to ask for data, it's never null. */
    private final YardRepository repository;


    /** Constructor.
     *
     * @param SomeObjectClient The client to require external some objects,
     *                          to update created ones or to register new
     *                          ones, required.
     * @param notificationUtil  The util to notify to a readable chat the
     *                          errors.
     * @param repository        Repository to persists or extract the needed
     *                          data of the tasks.
     */
    @Autowired
    public SomeService(
            @Qualifier("reportDataServiceClient")
                    SomeClient SomeObjectClient,
            SlackUtils notificationUtil, YardRepository repository) {
        this.SomeObjectClient = SomeObjectClient;
        this.notificationUtil = notificationUtil;
        this.repository = repository;
    }


    /** Returns the some object of the given id.
     *
     * @param id        The id of the wanted tasks.
     * @param warehouse The warehouse where the task belongs.
     * @return True if the tasks exists, false otherwise.
     */
    @Retry(name = SERVICE_NAME)
    @CircuitBreaker(name = SERVICE_NAME)
    public boolean exists(String id, String warehouse) {
        return repository.exists(id, warehouse);
    }

    /** Returns the some object of the given id.
     *
     * @param id        The id of the wanted tasks.
     * @param warehouse The warehouse where the task belongs.
     * @return The some object of the given id.
     */
    @Retry(name = SERVICE_NAME)
    @CircuitBreaker(name = SERVICE_NAME)
    public Yard obtainSomeObject(String id, String warehouse) {
        return repository.get(id, warehouse);
    }

    /** Creates the some object in the labels repository to generate its
     * some object when required.
     * @param task The task to register, cannot be null.
     */
    @Transactional
    @Retry(name = SERVICE_NAME)
    @CircuitBreaker(name = SERVICE_NAME)
    public void registerSomeObject(Yard task) {
        repository.storeSomeObject(task);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isServiceHealthy() {
        try {
            SomeObjectClient
                    .requireSomeObjects("PA", "1900-01-01");
        } catch (Exception e) {
            notificationUtil.logError(
                    "Some service is reporting a health error on its " +
                            "client to access the external tasks: " +
                            e.getMessage());
            return false;
        }
        return true;
    }
}
