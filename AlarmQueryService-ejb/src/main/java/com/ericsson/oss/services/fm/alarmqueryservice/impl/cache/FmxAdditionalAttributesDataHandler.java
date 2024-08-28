/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.impl.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.services.fm.common.cache.FMXAdditionalAttributesCacheManager;
import com.ericsson.oss.services.fm.models.FmxAddtionalAttributesRecord;

/**
 * FmxAdditionalAttributesDataHandler provides functionality to handle all operation on FmxAdditionalAttributeCache.
 */
@ApplicationScoped
public class FmxAdditionalAttributesDataHandler {

    @Inject
    private FMXAdditionalAttributesCacheManager fmxAdditionalAttributesCacheManager;

    /**
     * Method that returns Enabled additional attribute data.
     *
     * @return FmxAddtionalAttributesRecord
     */
    public FmxAddtionalAttributesRecord getEnabledAttributesRecord() {
        return fmxAdditionalAttributesCacheManager.getEnabledAttributesRecord();
    }

    /**
     * Method that returns Disabled additional attribute data.
     *
     * @return FmxAddtionalAttributesRecord
     */
    public FmxAddtionalAttributesRecord getDisabledAttributesRecord() {
        return fmxAdditionalAttributesCacheManager.getDisabledAttributesRecord();
    }

}
