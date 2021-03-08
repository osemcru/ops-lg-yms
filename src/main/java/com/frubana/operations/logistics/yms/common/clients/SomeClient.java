package com.frubana.operations.logistics.yms.common.clients;

import com.frubana.operations.logistics.yms.yard.domain.Yard;

import java.util.List;

/** External some object client definition.
 */
public interface SomeClient {
    /** Extracts some object from other service to be used for the something.
     *
     * @param warehouse The warehouse from where the some object are going to
     *                  be extracted, required and cannot be null.
     * @param date      Day of the some object to extract, required in format
     *                  yyyy-MM-dd and cannot be null.
     * @return The list of some object for the given warehouse in the given
     * date.
     */
    List<Yard> requireSomeObjects(String warehouse, String date);
}
