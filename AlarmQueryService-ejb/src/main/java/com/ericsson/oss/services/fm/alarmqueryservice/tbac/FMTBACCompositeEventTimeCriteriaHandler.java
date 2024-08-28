/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.tbac;

import static com.ericsson.oss.services.fm.common.tbac.FMTBACConstants.INSUFFICIENT_ACCESS_RIGHTS_ERROR_MSG;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmCountResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.common.tbac.FMTBACAccessControl;
import com.ericsson.oss.services.fm.common.tbac.FMTBACHandler;
import com.ericsson.oss.services.fm.common.tbac.FMTBACParamHandler;

/**
 * TBAC handler to filter authorized nodes from compositeEventTimeCriteria.
 */
@FMTBACHandler(handlerId = "FMTBACCompositeEventTimeCriteriaHandler")
public class FMTBACCompositeEventTimeCriteriaHandler implements FMTBACParamHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FMTBACCompositeEventTimeCriteriaHandler.class);

    @Inject
    private TBACAuthorizationHandler tbacAuthorizationHandler;

    @Override
    public boolean preProcess(final FMTBACAccessControl accessControl, final Object paramList) {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = (CompositeEventTimeCriteria) paramList;
        List<AlarmAttributeCriteria> inputAttributeCriteria = new ArrayList<AlarmAttributeCriteria>();
        List<String> fdnsInSearchCriteria = new ArrayList<String>();
        Set<String> filteredFdns = new HashSet<String>();
        try {
            final List<String> fdnsInDb = tbacAuthorizationHandler.getNodesFromDb();
            // if nodes/alarmcritera equals to null we must pull all NetworkElements from DB, extract fdns and attach to our
            // compositeEventTimeCriteria
            if ((compositeEventTimeCriteria.getNodes() == null || compositeEventTimeCriteria.getNodes().isEmpty())
                    && ((compositeEventTimeCriteria.getAlarmAttributeCriteria() == null) || compositeEventTimeCriteria.getAlarmAttributeCriteria()
                            .isEmpty())) {
                fdnsInSearchCriteria = fdnsInDb;
                filteredFdns = tbacAuthorizationHandler.filterAuthorisedNodes(fdnsInSearchCriteria, inputAttributeCriteria, fdnsInDb, filteredFdns,
                        accessControl);
             // if nodes not equal to null and alarmattribute criteria is not null
            } else if ((compositeEventTimeCriteria.getNodes() != null && !compositeEventTimeCriteria.getNodes().isEmpty())
                    && (compositeEventTimeCriteria.getAlarmAttributeCriteria() != null && !compositeEventTimeCriteria.getAlarmAttributeCriteria()
                            .isEmpty())) {
                inputAttributeCriteria = compositeEventTimeCriteria.getAlarmAttributeCriteria();
                fdnsInSearchCriteria = compositeEventTimeCriteria.getNodes();
                fdnsInSearchCriteria.retainAll(fdnsInDb);
                if (!fdnsInSearchCriteria.isEmpty()) {
                    filteredFdns = tbacAuthorizationHandler.filterAuthorisedNodes(fdnsInSearchCriteria, inputAttributeCriteria, fdnsInDb,
                            filteredFdns, accessControl);
                    if (filteredFdns.isEmpty()) {
                        throw new RuntimeException(INSUFFICIENT_ACCESS_RIGHTS_ERROR_MSG + fdnsInSearchCriteria);
                    }
                } else {
                    filteredFdns = new HashSet<String>();
                }
            } else {
                inputAttributeCriteria = compositeEventTimeCriteria.getAlarmAttributeCriteria();
                // if nodes not equal to null and alarmattribute criteria null
                if (inputAttributeCriteria == null || inputAttributeCriteria.isEmpty()) {
                    fdnsInSearchCriteria = compositeEventTimeCriteria.getNodes();
                    fdnsInSearchCriteria.retainAll(fdnsInDb);
                    if (!fdnsInSearchCriteria.isEmpty()) {
                        filteredFdns = tbacAuthorizationHandler.filterAuthorisedNodes(fdnsInSearchCriteria, inputAttributeCriteria, fdnsInDb,
                                filteredFdns, accessControl);
                        if (filteredFdns.isEmpty()) {
                            throw new RuntimeException(INSUFFICIENT_ACCESS_RIGHTS_ERROR_MSG + fdnsInSearchCriteria);
                        }
                    } else {
                        filteredFdns = new HashSet<String>();
                    }
                } else {
                    // if nodes equal to null or empty and alarmattribute criteria is not null
                    filteredFdns = tbacAuthorizationHandler.filterAuthorisedNodes(fdnsInSearchCriteria, inputAttributeCriteria, fdnsInDb,
                            filteredFdns, accessControl);
                }
            }
        } catch (final SecurityViolationException securityViolationException) {
            LOGGER.error("SecurityViolationException occured while retrieving the access control : {}", securityViolationException);
        } catch (final Exception exception) {
            if (exception.getMessage() != null && exception.getMessage().contains(INSUFFICIENT_ACCESS_RIGHTS_ERROR_MSG)) {
                throw exception;
            }
            LOGGER.error("Unexpected Error occured while retrieving the access control : {}", exception);
        }
        LOGGER.debug("TBAC authorised nodes for the input compositeEventTimeCriteria is {}", filteredFdns.size());
        compositeEventTimeCriteria.setNodes(new ArrayList<String>(filteredFdns));
        compositeEventTimeCriteria.setAlarmAttributeCriteria(inputAttributeCriteria);
        final boolean result = !filteredFdns.isEmpty() ? true : false;
        return result;
    }

    @Override
    public Object postProcess(final FMTBACAccessControl accessControl, final Object response) {
        if (response instanceof AlarmAttributeResponse) {
            final AlarmAttributeResponse alarmAttributeResponse = (AlarmAttributeResponse) response;
            return alarmAttributeResponse;
        } else {
            final AlarmCountResponse alarmCountResponse = (AlarmCountResponse) response;
            return alarmCountResponse;
        }
    }

}
