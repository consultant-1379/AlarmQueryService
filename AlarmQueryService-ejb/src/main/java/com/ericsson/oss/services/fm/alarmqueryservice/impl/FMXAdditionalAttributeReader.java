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

package com.ericsson.oss.services.fm.alarmqueryservice.impl;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARM_ADDITIONAL_INFORMATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.ALARM_ADDITIONAL_INFORMATION_ATTRIBUTE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.old.util.QueryConstants.FM;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.ProjectionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

/**
 * A class responsible for reading the additional attribute information from DPS.
 */
public class FMXAdditionalAttributeReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(FMXAdditionalAttributeReader.class);

    @Inject
    private DPSProxy dpsProxy;

    /**
     * Retrieves the additional attributes.
     *
     * @return {@link List<String>} - Returns the list of additional attributes.
     */
    public List<String> getAlarmAdditionalAttributes() {
        try {
            final QueryBuilder queryBuilder = dpsProxy.getService().getQueryBuilder();
            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(FM, ALARM_ADDITIONAL_INFORMATION);
            final Projection projection = ProjectionBuilder.attribute(ALARM_ADDITIONAL_INFORMATION_ATTRIBUTE);
            dpsProxy.getService().setWriteAccess(false);
            final List<String> resultList = dpsProxy.getService().getLiveBucket().getQueryExecutor().executeProjection(typeQuery, projection);
            LOGGER.debug("{} Additional Attributes found in the DB.", resultList.size());
            return new ArrayList<String>(new HashSet<String>(resultList));
        } catch (final Exception exception) {
            LOGGER.error("Exception Occured while retrieving the Additional Attributes from DB {}", exception);
            final StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append("Failed to read Additional Attributes from DB. Exception details are ").append(exception.getMessage());
            throw new RuntimeException(errorMessageBuilder.toString());
        }
    }
}
