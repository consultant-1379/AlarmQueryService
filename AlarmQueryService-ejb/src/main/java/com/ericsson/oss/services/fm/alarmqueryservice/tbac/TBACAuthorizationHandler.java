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
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.NETWORK_ELEMENT;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.OSS_NE_DEF;
import static com.ericsson.oss.services.fm.common.tbac.FMTBACConstants.INSUFFICIENT_ACCESS_RIGHTS_ERROR_MSG;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.query.ObjectField;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.ProjectionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;
import com.ericsson.oss.services.fm.common.tbac.FMTBACAccessControl;

/**
 * Class used to filter authorized nodes from the requested nodes.
 */
@Stateless
public class TBACAuthorizationHandler {
    public static final String MANAGEMENT_SYSTEM = "ManagementSystem";
    public static final String VNFM = "VirtualNetworkFunctionManager";
    public static final String VIM = "VirtualInfrastructureManager";
    private static final Logger LOGGER = LoggerFactory.getLogger(FMTBACCompositeEventTimeCriteriaHandler.class);

    @Inject
    private DPSProxy dpsProxy;

    public List<String> getNodesFromDb() {
        final List<String> allFdns = new ArrayList<String>();
        try {
            final DataPersistenceService dpsInstance = dpsProxy.getService();
            dpsInstance.setWriteAccess(false);
            final QueryBuilder queryBuilder = dpsInstance.getQueryBuilder();
            final DataBucket liveBucket = dpsInstance.getLiveBucket();
            final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(OSS_NE_DEF, NETWORK_ELEMENT);
            final Projection fdnProjection = ProjectionBuilder.field(ObjectField.MO_FDN);

            final Query<TypeRestrictionBuilder> msTypeQuery = queryBuilder.createTypeQuery(OSS_NE_DEF, MANAGEMENT_SYSTEM);
            final Projection msProjection = ProjectionBuilder.field(ObjectField.MO_FDN);

            final Query<TypeRestrictionBuilder> vmTypeQuery = queryBuilder.createTypeQuery(OSS_NE_DEF, VNFM);
            final Projection vmProjection = ProjectionBuilder.field(ObjectField.MO_FDN);

            final Query<TypeRestrictionBuilder> vimTypeQuery = queryBuilder.createTypeQuery(OSS_NE_DEF, VIM);
            final Projection vimProjection = ProjectionBuilder.field(ObjectField.MO_FDN);

            final List<String> networkElements = queryExecutor.executeProjection(typeQuery, fdnProjection);

            final List<String> managementSystems = liveBucket.getQueryExecutor().executeProjection(msTypeQuery, msProjection);
            final List<String> virtualNetworkFunctionManager = liveBucket.getQueryExecutor().executeProjection(vmTypeQuery, vmProjection);
            final List<String> virtualInfrastructureManager = liveBucket.getQueryExecutor().executeProjection(vimTypeQuery, vimProjection);

            allFdns.addAll(new ArrayList<String>(networkElements));
            allFdns.addAll(new ArrayList<String>(managementSystems));
            allFdns.addAll(new ArrayList<String>(virtualNetworkFunctionManager));
            allFdns.addAll(new ArrayList<String>(virtualInfrastructureManager));
        } catch (final Exception exception) {
            LOGGER.error("Exception occured while fetching nodes {}", exception);
            throw new RuntimeException("Failed to fetch nodes from db. Exception details are:" + exception.getMessage());
        }
        return allFdns;
    }

    public void validateNodesAuthorization(final List<String> fdnsInCriteria, final Set<String> filteredFdns,
                                           final FMTBACAccessControl accessControlManager) {
        final Iterator<String> nodesIterator = fdnsInCriteria.iterator();
        while (nodesIterator.hasNext()) {
            final String neFdn = nodesIterator.next();
            final boolean authorized = accessControlManager.isAuthorizedFromFdn(neFdn);
            if (!authorized) {
                LOGGER.trace("{} is not authorized", neFdn);
            } else {
                filteredFdns.add(neFdn);
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
    public Set<String> filterAuthorisedNodes(final List<String> fdnsInCriteria, final List<AlarmAttributeCriteria> inputAttributeCriteria,
                                             final List<String> fdnsInDb,
                                             final Set<String> filteredFdns,
                                             final FMTBACAccessControl accessControl) {
        Boolean isAttributefiltercontainsFdn = false;
        final Set<String> alarmAttributeFilteredFdns = new HashSet<String>();
        final Set<String> fdnsWithEqualOperator = new HashSet<String>();
        final Set<String> failedFdnsWithEqualOperator = new HashSet<String>();
        validateNodesAuthorization(fdnsInCriteria, filteredFdns, accessControl);
        if (inputAttributeCriteria == null || inputAttributeCriteria.isEmpty()) {
            alarmAttributeFilteredFdns.addAll(filteredFdns);
        } else {
            final Iterator<AlarmAttributeCriteria> inputCriteria = inputAttributeCriteria.listIterator();
            while (inputCriteria.hasNext()) {
                final AlarmAttributeCriteria inputAttibutes = inputCriteria.next();
                final String attributeName = inputAttibutes.getAttributeName();
                final Operator operator = inputAttibutes.getOperator();
                if (attributeName.equals(FDN)) {
                    isAttributefiltercontainsFdn = true;
                    alarmAttributeFilteredFdns.addAll(filteredFdns);
                    fdnsWithEqualOperator.addAll(filteredFdns);
                    final String attributeValue = (String) inputAttibutes.getAttributeValue();
                    if (operator == Operator.EQ) {
                        inputCriteria.remove();
                        fdnsWithEqualOperator.add(attributeValue);
                        final boolean authorized = accessControl.isAuthorizedFromFdn(attributeValue);
                        if (!authorized) {
                            LOGGER.trace("{} is not authorized", attributeValue);
                            failedFdnsWithEqualOperator.add(attributeValue);
                            if (!fdnsInDb.contains(attributeValue)) {
                                alarmAttributeFilteredFdns.add(attributeValue);
                                fdnsWithEqualOperator.remove(attributeValue);
                                failedFdnsWithEqualOperator.remove(attributeValue);
                            }
                        } else {
                            alarmAttributeFilteredFdns.add(attributeValue);
                        }
                    } else if (operator == Operator.CONTAINS) {
                        inputCriteria.remove();
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
                        inputCriteria.remove();
                        if (attributeValue.isEmpty()) {
                            validateNodesAuthorization(fdnsInDb, filteredFdns, accessControl);
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
                        inputCriteria.remove();
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
                }
            }
            if (!isAttributefiltercontainsFdn) {
                if (fdnsInCriteria.isEmpty()) {
                    validateNodesAuthorization(fdnsInDb, filteredFdns, accessControl);
                }
                alarmAttributeFilteredFdns.addAll(filteredFdns);
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
