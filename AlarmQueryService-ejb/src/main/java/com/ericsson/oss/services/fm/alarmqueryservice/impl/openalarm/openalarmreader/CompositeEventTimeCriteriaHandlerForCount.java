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

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.FM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.OPEN_ALARM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.SUCCESS;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.exception.model.ModelConstraintViolationException;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmCountResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.exception.AttributeConstraintViolationException;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.CompositeEventTimeCriteriaRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

/**
 * Responsible for retrieving the alarms count based on the conditions set in {@link CompositeEventTimeCriteria}.
 **/

@Stateless
public class CompositeEventTimeCriteriaHandlerForCount {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeEventTimeCriteriaHandlerForCount.class);

    @Inject
    private DPSProxy dpsProxy;

    @Inject
    private CompositeEventTimeCriteriaRestrictionBuilder compositeEventTimeCriteriaRestrictionBuilder;

    /**
     * Returns the AlarmCountResponse for the conditions set in {@link CompositeEventTimeCriteria} <br>
     * This method is useful to get alarm count, developer could use this method before calling getAlams and avoid queries response bigger then 5000
     * records.
     *
     * @param compositeEventTimeCriteria
     *            -- {@link CompositeEventTimeCriteria}
     * @param alarmAttributes
     *            -- ExpectedOutputAttributes
     * @return -- AlarmCountResponse
     */
    public AlarmCountResponse getAlarmCount(final CompositeEventTimeCriteria compositeEventTimeCriteria) {
        final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, OPEN_ALARM);
        AlarmCountResponse alarmCountResponse = null;
        try {
            final Restriction compositeRestriction = compositeEventTimeCriteriaRestrictionBuilder.build(typeQuery, compositeEventTimeCriteria);
            final List<Restriction> nodeRestrictions = compositeEventTimeCriteriaRestrictionBuilder.buildNodeRestrictions(typeQuery,
                    compositeEventTimeCriteria);
            final Long alarmCount = getAlarmCount(typeQuery, compositeRestriction, nodeRestrictions);
            LOGGER.debug("Total number of alarms : {} found with compositeEventTimeCriteria : {}  is ", alarmCount, compositeEventTimeCriteria);
            alarmCountResponse = new AlarmCountResponse(alarmCount, SUCCESS);
        } catch (final AttributeConstraintViolationException | ModelConstraintViolationException exception) {
            LOGGER.error("Error while retrieving alarms count from DB {} with given compositeEventTimeCriteria {}", exception,
                    compositeEventTimeCriteria);
            alarmCountResponse = new AlarmCountResponse(-1L, exception.getMessage());
        } catch (final Exception exception) {
            LOGGER.error("Error while retrieving alarms from DB {} with given compositeEventTimeCriteria {} ", exception, compositeEventTimeCriteria);
            final StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append("Failed to read alarms count from DB. Exception details are: ").append(exception.getMessage());
            return new AlarmCountResponse(-1L, errorMessageBuilder.toString());
        }
        return alarmCountResponse;
    }

    /**
     * Retrieves the number of alarm which are resulted by {@link Restriction}s.
     *
     * @param query
     *            -- { @link Query}
     * @param compositeRestriction
     *            -- composite {@link Restriction} of alarm attributes and event times.
     * @param nodeRestrictions
     *            -- {@code List< link@ {@link Restriction}> builded on nodes
     * @return <code><b>true</b></code> if the limit exceeds <code><b>false</b></code> otherwise
     */
    private Long getAlarmCount(final Query<TypeRestrictionBuilder> typeQuery, final Restriction compositeRestriction,
                               final List<Restriction> nodeRestrictions) {
        Long numberOfAlarms = 0L;
        dpsProxy.getService().setWriteAccess(false);
        final QueryExecutor queryExecutor = dpsProxy.getLiveBucket().getQueryExecutor();
        if (compositeRestriction != null) {
            if (nodeRestrictions != null && !nodeRestrictions.isEmpty()) {
                final TypeRestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
                for (final Restriction nodesRestriction : nodeRestrictions) {
                    final Restriction finalRestriction = restrictionBuilder.allOf(compositeRestriction, nodesRestriction);
                    typeQuery.setRestriction(finalRestriction);
                    numberOfAlarms = numberOfAlarms + queryExecutor.executeCount(typeQuery);
                }
            } else {
                typeQuery.setRestriction(compositeRestriction);
                numberOfAlarms = numberOfAlarms + queryExecutor.executeCount(typeQuery);
            }
        } else if (!nodeRestrictions.isEmpty()) {
            final TypeRestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
            Restriction finalRestriction = null;
            for (final Restriction nodesRestriction : nodeRestrictions) {
                if (compositeRestriction != null) {
                    finalRestriction = restrictionBuilder.allOf(compositeRestriction, nodesRestriction);
                } else {
                    finalRestriction = nodesRestriction;
                }
                typeQuery.setRestriction(finalRestriction);
                numberOfAlarms = numberOfAlarms + queryExecutor.executeCount(typeQuery);
            }
        } else {
            numberOfAlarms = queryExecutor.executeCount(typeQuery);
        }
        return numberOfAlarms;
    }
}
