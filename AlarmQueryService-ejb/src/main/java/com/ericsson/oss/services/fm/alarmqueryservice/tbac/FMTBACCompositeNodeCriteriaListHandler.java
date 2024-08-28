/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.tbac;

import java.util.List;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.common.tbac.FMTBACAccessControl;
import com.ericsson.oss.services.fm.common.tbac.FMTBACHandler;

/**
 * TBAC handler to filter authorized nodes from compositeNodeCriteriaHandler.
 */
@FMTBACHandler(handlerId = "FMTBACCompositeNodeCriteriaListHandler")
public class FMTBACCompositeNodeCriteriaListHandler extends FMTBACCompositeNodeCriteriaHandler {

    @SuppressWarnings("unchecked")
    @Override
    public boolean preProcess(final FMTBACAccessControl accessControlManager, final Object paramIn) {
        boolean result = false;
        final List<Object> compositeNodeCriteriaList = (List<Object>) paramIn;
        for (final Object compositeNodeCriteria : compositeNodeCriteriaList) {
            result = super.preProcess(accessControlManager, compositeNodeCriteria);
            if (!result) {
                return result;
            }
        }
        return result;
    }

    @Override
    public Object postProcess(final FMTBACAccessControl accessControlManager, final Object response) {
        final AlarmAttributeResponse alarmPoIdResponse = (AlarmAttributeResponse) response;
        return alarmPoIdResponse;
    }

}