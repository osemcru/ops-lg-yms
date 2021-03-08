package com.frubana.operations.logistics.yms.health.domain;

import com.frubana.operations.logistics.yms.health.service.HealthCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class to implement the readiness end point
 */
@Component
public class SomeHealthVerifier implements HealthIndicator {

    /** The services to verify if all is OK, it's never null. */
    private final List<HealthCheck> services;


    /** Constructor.
     *
     * @param services The services to verify if all is OK, cannot be null.
     */
    @Autowired
    public SomeHealthVerifier(List<HealthCheck> services) {
        this.services = services;
    }

    /** {@inheritDoc} */
    @Override
    public Health health() {
        int errorCode = check(); // perform some specific health check
        if (errorCode != 0) {
            return Health.down()
                    .withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

    /** Checks one by one the services registered for health check.
     *
     * @return 0 if everything was ok, 1 if something reported a health problem.
     */
    public int check() {
        // Check the services to see all are ok.
        for (HealthCheck service : services) {
            if (!service.isServiceHealthy()) {
                return 1;
            }
        }
        return 0;
    }
}
