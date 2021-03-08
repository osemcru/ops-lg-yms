package com.frubana.operations.logistics.yms.health.service;

/** Basic interface for all the services in change-me to check the readiness and
 *  health of each service.
 */
public interface HealthCheck {

    /** Checks the health of the service.
     *
     * @return true if everything is ok, false otherwise to restart the service.
     */
    boolean isServiceHealthy();

}
