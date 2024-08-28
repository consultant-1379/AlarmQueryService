/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.OBJECT_OF_REFERENCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.OPEN_ALARM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.SUCCESS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.exception.model.ModelConstraintViolationException;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmPoIdResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.OORCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.OORExpression;
import com.ericsson.oss.services.fm.alarmqueryservice.api.exception.AttributeConstraintViolationException;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.builder.LogicalOperatorRestrictionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

/**
 * Responsible for retrieving the alarms based on the conditions set in {@link OORCriteria}.
 *
 **/

public class OORCriteriaHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OORCriteriaHandler.class);

    @Inject
    private DPSProxy dpsProxy;

    @Inject
    private PoIdReader poIdReader;

    @Inject
    private LogicalOperatorRestrictionBuilder logicalOperatorRestrictionBuilder;

    /**
     * Returns the {@link AlarmPoIdResponse} for the conditions set in {@link OORCriteria} <br>
     * {@link AlarmPoIdResponse} contains the retrieved poIds for {@link OORCriteria}.<br>
     *
     * @param oorCriteria
     *            -- {@link OORCriteria}
     * @return -- {@link AlarmPoIdResponse}
     */
    public AlarmPoIdResponse getAlarmPoIds(final OORCriteria oorCriteria) {
        AlarmPoIdResponse alarmPoIdResponse = null;
        try {
            dpsProxy.getService().setWriteAccess(false);
            final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(QueryConstants.FM, OPEN_ALARM);
            final List<Long> poIds = getPoIds(typeQuery, oorCriteria);
            LOGGER.debug("Number of PoIds  found for OORCriteria :: {} is :: {}", oorCriteria, poIds.size());
            alarmPoIdResponse = new AlarmPoIdResponse(poIds, SUCCESS);
        } catch (final AttributeConstraintViolationException | ModelConstraintViolationException exception) {
            LOGGER.error("Error while retrieving alarms from DB {} with given oorCriteria {} ", exception, oorCriteria);
            alarmPoIdResponse = new AlarmPoIdResponse(Collections.<Long> emptyList(), exception.getMessage());
        } catch (final Exception exception) {
            LOGGER.error("Error while retrieving alarms from DB {} with given oorCriteria {} ", exception, oorCriteria);
            final StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append("Failed to read poIds from DB. Exception details are : ").append(exception.getMessage());
            alarmPoIdResponse = new AlarmPoIdResponse(Collections.<Long> emptyList(), errorMessageBuilder.toString());
        }
        return alarmPoIdResponse;
    }

    /**
     * Returns list of poIds for the conditions set in {@link OORCriteria}<br>
     * If OORCriteria is not set, method returns all the poIds.
     *
     * @param typeQuery
     *            -- {@link Query}
     * @param oorCriteria
     *            -- {@link OORCriteria}
     * @return -- list of PoIds
     */
    private List<Long> getPoIds(final Query<TypeRestrictionBuilder> typeQuery, final OORCriteria oorCriteria) {
        final List<Long> poIds = new ArrayList<Long>(0);
        final TypeRestrictionBuilder restrictionBuilder = typeQuery.getRestrictionBuilder();
        // Only alarms which are having VISIBILITY value true, will be retrieved from DPS.
        final Restriction visibilityRestriction = restrictionBuilder.equalTo(VISIBILITY, true);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        if (oorCriteria != null) {
            for (final OORExpression oorCondition : oorCriteria.getOorExpressions()) {
                final Restriction oorRestriction = logicalOperatorRestrictionBuilder.build(restrictionBuilder, OBJECT_OF_REFERENCE,
                        oorCondition.getObjectOfReference(), oorCondition.getOperator());
                Restriction finalRestriction = oorRestriction;
                finalRestriction = restrictionBuilder.allOf(finalRestriction, visibilityRestriction);
                typeQuery.setRestriction(finalRestriction);
                poIds.addAll(poIdReader.getPoIds(liveBucket, typeQuery));
            }
        } else {
            typeQuery.setRestriction(visibilityRestriction);
            poIds.addAll(poIdReader.getPoIds(liveBucket, typeQuery));
        }
        return poIds;
    }
}
