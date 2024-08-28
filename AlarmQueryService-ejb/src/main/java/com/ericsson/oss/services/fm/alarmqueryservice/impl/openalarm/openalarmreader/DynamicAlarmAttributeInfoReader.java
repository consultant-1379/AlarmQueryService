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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.DYNAMIC_ALARM_ATTRIBUTE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.DYNAMIC_ALARM_ATTRIBUTE_MODEL;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.FM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.POIDS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.ProjectionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmPoIdCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.AttributeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DynamicAlarmAttributeValidator;
import com.ericsson.oss.services.models.alarm.DynamicAlarmAttributeInformation;

/**
 * A class responsible for reading the additional attribute information from DPS.
 */
public class DynamicAlarmAttributeInfoReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicAlarmAttributeInfoReader.class);

    @Inject
    private DPSProxy dpsProxy;

    @Inject
    private AlarmPoIdCriteriaHandler alarmPoIdCriteriaHandler;

    @Inject
    private AttributeRestrictionBuilder attributeRestrictionBuilder;

    @Inject
    private DynamicAlarmAttributeValidator alarmAttributeValidator;

    /**
     * Reads the additional attributes from DB.
     *
     * @return {@link List<String>} - Returns the list of additional attributes.
     */
    public List<String> readDynamicAlarmAttributes() {
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, DYNAMIC_ALARM_ATTRIBUTE_MODEL);
        final Projection projection = ProjectionBuilder.attribute(DYNAMIC_ALARM_ATTRIBUTE);
        final List<String> dynamicAlarmAttributes = dpsProxy.getService().getLiveBucket().getQueryExecutor().executeProjection(typeQuery, projection);
        return new ArrayList<String>(dynamicAlarmAttributes);
    }

    /**
     * Method takes dynamic alarm attributes and fetches List of poids which matches the dynamic alarm attribute.
     *
     * @param dynamicSortingAttributes
     *            -- dynamic alarm attribute names.
     * @return - Returns the list of poids {@link List<Long>}.
     */
    public List<Long> readPoIdsForDynamicAlarmAttribute(final List<String> dynamicSortingAttributes) {
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, DYNAMIC_ALARM_ATTRIBUTE_MODEL);

        final List<Restriction> dynamicAttributeRestrictions = new ArrayList<Restriction>();
        for (final String dynamicAttribute : dynamicSortingAttributes) {
            dynamicAttributeRestrictions.add(typeQuery.getRestrictionBuilder().equalTo(DYNAMIC_ALARM_ATTRIBUTE, dynamicAttribute));
        }
        final Restriction finalRestriction = typeQuery.getRestrictionBuilder().anyOf(
                dynamicAttributeRestrictions.toArray(new Restriction[dynamicAttributeRestrictions.size()]));

        if (finalRestriction != null) {
            typeQuery.setRestriction(finalRestriction);
        }
        final DataBucket liveBucket = dpsProxy.getService().getLiveBucket();

        final Iterator<PersistenceObject> dynamicAlarmAttributeInformationPOs = liveBucket.getQueryExecutor().execute(typeQuery);

        final Set<Long> poIds = new HashSet<Long>();

        while (dynamicAlarmAttributeInformationPOs.hasNext()) {
            final PersistenceObject dynamicAlarmAttributeInformationPO = dynamicAlarmAttributeInformationPOs.next();
            final List<Long> PersistenceObjectPoIds = dynamicAlarmAttributeInformationPO.getAttribute(POIDS);
            poIds.addAll(PersistenceObjectPoIds);

        }
        return new ArrayList<Long>(poIds);
    }

    /**
     * Method fetches alarms matched to the dynamic search attributes.
     *
     * @param dynamicAlarmAttributes
     *            {@link List<AlarmAttributeCriteria>}
     * @param expectedOutputAttributes
     * @param sortOrder
     * @param sortAttribute
     * @return {@link AlarmAttributeResponse}
     */
    public AlarmAttributeResponse readDynamicAttributesMatchedSearchAlarms(final List<AlarmAttributeCriteria> dynamicAlarmAttributes,
                                                                           final ExpectedOutputAttributes expectedOutputAttributes) {
        final List<AlarmRecord> dynamicAlarmAttributeMatchedAlarmRecords = new ArrayList<AlarmRecord>();

        final List<Long> attributePoIds = new ArrayList<Long>(readPoidsforDynamicAttributeSearchCriteria(dynamicAlarmAttributes));

        final AlarmPoIdCriteria alarmPoIdCriteria = new AlarmPoIdCriteria();
        alarmPoIdCriteria.setPoIds(attributePoIds);

        final AlarmAttributeResponse dynamicAlarmAttributeResponse = alarmPoIdCriteriaHandler.getAlarmsForAdditionalAttributeSearchSort(
                alarmPoIdCriteria, expectedOutputAttributes);

        LOGGER.debug("Total number of dynamic alarm attribute matched serach alarms: {}", dynamicAlarmAttributeMatchedAlarmRecords.size());

        return dynamicAlarmAttributeResponse;
    }

    /**
     * Method takes all search criteria and reads only fmx additional attribute matched search criteria.
     *
     * @param alarmAttributeCriterias
     *            search criteria with all the normal and additional attribute criteria's.
     * @return {@link List<AlarmAttributeCriteria>}.
     * @throws Exception
     */
    public List<AlarmAttributeCriteria> readDynamicSearchAttributes(final List<AlarmAttributeCriteria> alarmAttributeCriterias) throws Exception {
        final List<AlarmAttributeCriteria> dynamicAlarmAttributes = new ArrayList<AlarmAttributeCriteria>();
        final Iterator<AlarmAttributeCriteria> iterator = alarmAttributeCriterias.iterator();

        while (iterator.hasNext()) {
            final AlarmAttributeCriteria alarmAttributeCriteria = iterator.next();
            final String attributeName = alarmAttributeCriteria.getAttributeName();

            final List<String> dynamicAttributeName = new ArrayList<String>();
            dynamicAttributeName.add(attributeName);

            if (!alarmAttributeValidator.filterFmxAdditionalAttributes(dynamicAttributeName).isEmpty()) {
                dynamicAlarmAttributes.add(alarmAttributeCriteria);
                iterator.remove();
            }
        }
        LOGGER.debug("{} criteria on dynamic attributes found.", dynamicAlarmAttributes.size());
        return dynamicAlarmAttributes;
    }

    /**
     * Method takes dynamic attribute criteria's and check whether only one criteria and it also with not equalto(NE) operator than method return
     * true.
     *
     * @param alarmAttributeCriterias
     *            additional attribute criteria's.
     * @return true/false.
     */
    public boolean isSingleAdditionalAttributeWithNotEquals(final List<AlarmAttributeCriteria> alarmAttributeCriterias) {

        boolean isSingleAdditionalAttributeWithNotEquals = false;
        if (alarmAttributeCriterias.size() == 1) {
            if (Operator.NE.equals(alarmAttributeCriterias.get(0).getOperator())) {
                isSingleAdditionalAttributeWithNotEquals = true;
            }
        }
        return isSingleAdditionalAttributeWithNotEquals;
    }

    /**
     * Method that set of poIds matching with the given dynamic alarm attribute criteria {@linkplain AlarmAttributeCriteria} 1.It consolidate
     * different criteria set on a single additional attribute and do a OR operation 2.All the criteria set on same additional attribute are clubbed
     * with OR operator, <br>
     * criteria defined on different attributes are clubbed with AND. Example DynamicAlarmAttributeInformation po contains the following attributes
     * {@linkplain DynamicAlarmAttributeInformation}
     * <p>
     * dynamicAlarmAttribute--dynamicAlarmAttribute name
     * <p>
     * dynamicAlarmAttributeValue--dynamicAlarmAttribute value
     * <p>
     * poIds --list of poids
     * <p>
     * For example if the below are two DynamicAlarmAttributeInformation pos
     * <p>
     * 1. dynamicAlarmAttribute ="field2"
     * <p>
     * dynamicAlarmAttributeValue 5="fmxtest"
     * <p>
     * poIds =( 281474977768833 281474977761909 281474977768575)
     * <P>
     * 2.
     * <p>
     * dynamicAlarmAttribute ="field1"
     * <p>
     * dynamicAlarmAttributeValue ="fmxtest1"
     * <p>
     * poIds =( 281474977768833 281474977761909 281474977768598) If the user searches with field2=fmxtest and field1=fmxtest1 then the set of poids
     * obtained will be (281474977768833 281474977761909)
     *
     * @param list
     *            of dynamic alarm attribute criteria {@linkplain AlarmAttributeCriteria} --dynamicAlarmAttributeCriterias It does the following.
     * @return-- returns set of poids
     */
    private Set<Long> readPoidsforDynamicAttributeSearchCriteria(final List<AlarmAttributeCriteria> dynamicAlarmAttributeCriterias) {

        final Set<Long> poIds = new HashSet<Long>();

        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, DYNAMIC_ALARM_ATTRIBUTE_MODEL);
        final DataBucket liveBucket = dpsProxy.getService().getLiveBucket();

        if (dynamicAlarmAttributeCriterias != null && !dynamicAlarmAttributeCriterias.isEmpty()) {
            final Map<String, List<AlarmAttributeCriteria>> additionalAttributeNameToCriteria = attributeRestrictionBuilder
                    .groupAlarmAttributes(dynamicAlarmAttributeCriterias);
            for (final String additionalAttributeName : additionalAttributeNameToCriteria.keySet()) {
                final Restriction restriction = attributeRestrictionBuilder.buildAdditionalAttributesRestriction(typeQuery.getRestrictionBuilder(),
                        additionalAttributeNameToCriteria.get(additionalAttributeName), additionalAttributeName);
                typeQuery.setRestriction(restriction);

                final Iterator<PersistenceObject> dynamicAlarmAttributeInformationPOs = liveBucket.getQueryExecutor().execute(typeQuery);
                final List<Long> finalPersistenceObjectPoIds = new ArrayList<Long>();

                while (dynamicAlarmAttributeInformationPOs.hasNext()) {
                    final PersistenceObject dynamicAlarmAttributeInformationPO = dynamicAlarmAttributeInformationPOs.next();
                    final List<Long> persistenceObjectPoIds = dynamicAlarmAttributeInformationPO.getAttribute(POIDS);
                    finalPersistenceObjectPoIds.addAll(persistenceObjectPoIds);
                }

                if (poIds.isEmpty()) {
                    poIds.addAll(finalPersistenceObjectPoIds);
                } else {
                    // intersection of poids obtained with the different additional attribute names
                    poIds.retainAll(finalPersistenceObjectPoIds);
                }
            }
        }
        return poIds;
    }
}