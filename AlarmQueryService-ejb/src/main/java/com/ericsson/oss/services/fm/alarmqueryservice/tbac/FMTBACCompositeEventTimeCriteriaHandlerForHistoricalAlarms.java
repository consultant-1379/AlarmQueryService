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

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FDN;
import static com.ericsson.oss.services.fm.common.tbac.FMTBACConstants.INSUFFICIENT_ACCESS_RIGHTS_ERROR_MSG;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.common.tbac.FMTBACAccessControl;
import com.ericsson.oss.services.fm.common.tbac.FMTBACHandler;
import com.ericsson.oss.services.fm.common.tbac.FMTBACParamHandler;

/**
 * TBAC handler to filter authorized nodes from compositeEventTimeCriteria.
 */
@FMTBACHandler(handlerId = "FMTBACCompositeEventTimeCriteriaHandlerForHistoricalAlarms")
public class FMTBACCompositeEventTimeCriteriaHandlerForHistoricalAlarms implements FMTBACParamHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FMTBACCompositeEventTimeCriteriaHandlerForHistoricalAlarms.class);
    List<String> fdnsInSearchCriteriaAndDb = new ArrayList<String>();
    Set<String> filteredFdns = new HashSet<String>();
    List<String> fdnsInDb = new ArrayList<String>();

    @Inject
    private TBACAuthorizationHandler tbacAuthorizationHandler;

    @Override
    public boolean preProcess(final FMTBACAccessControl accessControl, final Object paramList) {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = (CompositeEventTimeCriteria) paramList;
        List<AlarmAttributeCriteria> inputAttributeCriteria = new ArrayList<AlarmAttributeCriteria>();
        List<String> fdnsInSearchCriteria = new ArrayList<String>();
        try {
            fdnsInDb = tbacAuthorizationHandler.getNodesFromDb();
            // if nodes/alarmcritera equals to null we must pull all NetworkElements from DB, extract fdns and attach to our
            // compositeEventTimeCriteria
            if ((compositeEventTimeCriteria.getNodes() == null || compositeEventTimeCriteria.getNodes().isEmpty())
                    && ((compositeEventTimeCriteria.getAlarmAttributeCriteria() == null) || compositeEventTimeCriteria.getAlarmAttributeCriteria()
                            .isEmpty())) {
                return true;
             // if nodes not equal to null and alarmattribute criteria is not null
            } else if ((compositeEventTimeCriteria.getNodes() != null && !compositeEventTimeCriteria.getNodes().isEmpty())
                    && (compositeEventTimeCriteria.getAlarmAttributeCriteria() != null && !compositeEventTimeCriteria.getAlarmAttributeCriteria()
                            .isEmpty())) {
                inputAttributeCriteria = compositeEventTimeCriteria.getAlarmAttributeCriteria();
                fdnsInSearchCriteria = compositeEventTimeCriteria.getNodes();
                fdnsInSearchCriteria.retainAll(fdnsInDb);
                if (!fdnsInSearchCriteria.isEmpty()) {
                    filteredFdns = filterAuthorisedNodes(fdnsInSearchCriteria, inputAttributeCriteria, fdnsInDb,
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
                    final List<String> deletedNodes = new ArrayList<String>(compositeEventTimeCriteria.getNodes());
                    fdnsInSearchCriteria = compositeEventTimeCriteria.getNodes();
                    fdnsInSearchCriteria.retainAll(fdnsInDb);
                    if (!fdnsInSearchCriteria.isEmpty()) {
                        filteredFdns = filterAuthorisedNodes(fdnsInSearchCriteria, inputAttributeCriteria, fdnsInDb,
                                filteredFdns, accessControl);
                        if (filteredFdns.isEmpty()) {
                            throw new RuntimeException(INSUFFICIENT_ACCESS_RIGHTS_ERROR_MSG + fdnsInSearchCriteria);
                        }
                    } else {
                        filteredFdns = new HashSet<String>(deletedNodes);
                    }
                } else {
                    // if nodes equal to null or empty and alarmattribute criteria is not null
                    filteredFdns = filterAuthorisedNodes(fdnsInSearchCriteria, inputAttributeCriteria, fdnsInDb,
                            filteredFdns, accessControl);
                    if (filteredFdns.isEmpty()) {
                        return true;
                    }
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
        AlarmAttributeResponse alarmAttributeResponse = null;
        if (response instanceof AlarmAttributeResponse) {
            alarmAttributeResponse = (AlarmAttributeResponse) response;
            filterOutAlarms(alarmAttributeResponse, accessControl);
        }
        return alarmAttributeResponse;
    }

    private void filterOutAlarms(final AlarmAttributeResponse alarmAttributeResponse, final FMTBACAccessControl accessControl) {
        final Iterator<AlarmRecord> iterator = alarmAttributeResponse.getAlarmRecords().iterator();
        if (filteredFdns.isEmpty()) {
            tbacAuthorizationHandler.validateNodesAuthorization(fdnsInDb, filteredFdns, accessControl);
        }
        while (iterator.hasNext()) {
            final AlarmRecord alarmRecord = iterator.next();
            if (!filteredFdns.contains(alarmRecord.getFdn()) && fdnsInDb.contains(alarmRecord.getFdn())) {
                iterator.remove();
            }
        }
    }

    /**
     * Method will filter nodes present in the AlarmAttributeCriteria of our compositeEventTimeCriteria FDNS present in the AlarmAttributeCritera will
     * compared against the user's Target Group to determine if user is authorized to perform actions on node.
     *
     * @param inputAttributeCriteria
     *            List of AlarmAttributeCritera, we must check if this list contains FDNS
     * @param fdnsInCriteria
     *            List of FDNs attached to the compositeEventTimeCriteria
     * @param fdnsInDb
     *            List of FDNs fetched from Database
     * @param filteredFdns
     *            List of FDN's user is authorized for
     * @param accessControl
     *            Security object used to verify user is authorized for node
     * @return returns set of authorized nodes
     */
    private Set<String> filterAuthorisedNodes(final List<String> fdnsInCriteria, final List<AlarmAttributeCriteria> inputAttributeCriteria,
                                              final List<String> fdnsInDb,
                                              final Set<String> filteredFdns,
                                              final FMTBACAccessControl accessControl) {
        final Set<String> alarmAttributeFilteredFdns = new HashSet<String>();
        final Set<String> fdnsWithEqualOperator = new HashSet<String>();
        final Set<String> failedFdnsWithEqualOperator = new HashSet<String>();
        tbacAuthorizationHandler.validateNodesAuthorization(fdnsInCriteria, filteredFdns, accessControl);
        if (inputAttributeCriteria == null || inputAttributeCriteria.isEmpty()) {
            alarmAttributeFilteredFdns.addAll(filteredFdns);
        } else {
            final Iterator<AlarmAttributeCriteria> inputCriteria = inputAttributeCriteria.listIterator();
            while (inputCriteria.hasNext()) {
                final AlarmAttributeCriteria inputAttibutes = inputCriteria.next();
                final String attributeName = inputAttibutes.getAttributeName();
                final Operator operator = inputAttibutes.getOperator();
                if (attributeName.equals(FDN)) {
                    alarmAttributeFilteredFdns.addAll(filteredFdns);
                    fdnsWithEqualOperator.addAll(filteredFdns);
                    final String attributeValue = (String) inputAttibutes.getAttributeValue();
                    if (operator == Operator.EQ) {
                        fdnsWithEqualOperator.add(attributeValue);
                        final boolean authorized = accessControl.isAuthorizedFromFdn(attributeValue);
                        if (!authorized) {
                            LOGGER.trace("{} is not authorized", attributeValue);
                            failedFdnsWithEqualOperator.add(attributeValue);
                            if (!fdnsInDb.contains(attributeValue)) {
                                alarmAttributeFilteredFdns.add(attributeValue);
                                failedFdnsWithEqualOperator.remove(attributeValue);
                            }
                        } else {
                            alarmAttributeFilteredFdns.add(attributeValue);
                        }
                    } else if (operator == Operator.CONTAINS) {
                        final Iterator<String> nodesIterator = fdnsInDb.iterator();
                        while (nodesIterator.hasNext()) {
                            final String node = nodesIterator.next();
                            if (node.contains(attributeValue)) {
                                if (accessControl.isAuthorizedFromFdn(node)) {
                                    alarmAttributeFilteredFdns.add(node);
                                }
                            }
                        }
                        fdnsWithEqualOperator.addAll(alarmAttributeFilteredFdns);
                    } else if (operator == Operator.STARTS_WITH) {
                        if (attributeValue.isEmpty()) {
                            alarmAttributeFilteredFdns.addAll(filteredFdns);
                        } else {
                            final Iterator<String> nodesIterator = fdnsInDb.iterator();
                            while (nodesIterator.hasNext()) {
                                final String node = nodesIterator.next();
                                if (node.startsWith(attributeValue)) {
                                    if (accessControl.isAuthorizedFromFdn(node)) {
                                        alarmAttributeFilteredFdns.add(node);
                                    }
                                }
                            }
                        }
                    } else {
                        final Iterator<String> nodesIterator = fdnsInDb.iterator();
                        while (nodesIterator.hasNext()) {
                            final String node = nodesIterator.next();
                            if (node.endsWith(attributeValue)) {
                                if (accessControl.isAuthorizedFromFdn(node)) {
                                    alarmAttributeFilteredFdns.add(node);
                                }
                            }
                        }
                    }
                } else {
                    alarmAttributeFilteredFdns.addAll(filteredFdns);
                }
            }
        }
        if ((!fdnsWithEqualOperator.isEmpty() && !failedFdnsWithEqualOperator.isEmpty())
                && (fdnsWithEqualOperator.size() == failedFdnsWithEqualOperator.size())) {
            LOGGER.debug("Insufficient access rights for the node(s):{}", failedFdnsWithEqualOperator);
            throw new RuntimeException(INSUFFICIENT_ACCESS_RIGHTS_ERROR_MSG + failedFdnsWithEqualOperator);
        }
        return alarmAttributeFilteredFdns;
    }

}
