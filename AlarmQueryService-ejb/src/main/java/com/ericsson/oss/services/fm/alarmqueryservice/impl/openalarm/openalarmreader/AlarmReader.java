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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ADDITIONAL_INFORMATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_PO_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.NON_SYNCHABLE_RECORD_TYPE_ADDITIONAL_ATTRIBUTE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.VISIBILITY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.ObjectField;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.ProjectionBuilder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

/**
 * Responsible retrieval of alarms based on the input set. <br>
 * Alarms can be retrieved using PoIds or Restrictions.<br>
 * Also Complete / specific attributes of alarm are retrieved based on the attributes given.
 */
public class AlarmReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmReader.class);

    @Inject
    private DPSProxy dpsProxy;

    @Inject
    private AlarmRecordConverter alarmRecordConverter;

    /**
     * Returns complete Alarms / specific attributes of alarm having PoIds, also retrieves NodeId and comment history based on its value.
     * @param poIds
     *            -- A unique number associated to each Alarm
     * @param commentsHistoryRequired
     *            <br>
     *            <code>true</code> -- retrieves history of comments <br>
     *            <code>false</code> -- otherwise
     * @param nodeIdRequired
     *            <br>
     *            <code>true</code> -- retrieves poId of Node <br>
     *            <code>false</code> -- otherwise
     * @param outputAttributes
     *            -- required output attributes of an alarm
     * @return -- {@code List<{@link AlarmRecord}>}
     */
    public List<AlarmRecord> getAlarmRecordsWithPoIds(final List<Long> poIds, final boolean commentsHistoryRequired, final boolean nodeIdRequired,
            final List<String> outputAttributes) {
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final List<PersistenceObject> openAlarms = liveBucket.findPosByIds(poIds);

        LOGGER.debug("Total Number of alarms found with given PoIds without considering the visibility of the alarm {} ", openAlarms.size());

        AlarmRecord alarmRecord = null;

        final boolean isOutputAttributesPresent = outputAttributes != null && !outputAttributes.isEmpty();

        for (final PersistenceObject openAlarm : openAlarms) {
            if (openAlarm.getAttribute(VISIBILITY)) {
                if (isOutputAttributesPresent) {
                    alarmRecord = alarmRecordConverter.prepareAlarmRecordForSeletedAttributes(openAlarm, outputAttributes);
                } else {
                    alarmRecord = alarmRecordConverter.prepareAlarmRecordForUserInterfaces(openAlarm, commentsHistoryRequired, nodeIdRequired);
                }
                removeUnusedAdditionalAttribute(alarmRecord);
                alarmRecords.add(alarmRecord);
            } else {
                LOGGER.trace("Ignored the alarm with poId : {}, since alarm is in hidden state", openAlarm.getPoId());
            }
        }
        return alarmRecords;
    }

    public List<AlarmRecord> getAlarmRecordsForAdditionalAttributeSearchSortWithPoIds(final List<Long> poIds, final boolean commentsHistoryRequired,
            final boolean nodeIdRequired,
            final List<String> outputAttributes) {
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final List<PersistenceObject> openAlarms = liveBucket.findPosByIds(poIds);

        LOGGER.debug("Number of alarms found with given PoIds {} ", openAlarms.size());

        AlarmRecord alarmRecord = null;

        final boolean isOutputAttributesPresent = outputAttributes != null && !outputAttributes.isEmpty();

        for (final PersistenceObject openAlarm : openAlarms) {
            if (openAlarm.getAttribute(VISIBILITY)) {
                if (isOutputAttributesPresent) {
                    alarmRecord = alarmRecordConverter.prepareAlarmRecordForAdditionalAttributes(openAlarm, outputAttributes);
                } else {
                    alarmRecord = alarmRecordConverter.prepareAlarmRecordForUserInterfaces(openAlarm, commentsHistoryRequired, nodeIdRequired);
                }
                if (alarmRecord != null) {
                    removeUnusedAdditionalAttribute(alarmRecord);
                    alarmRecords.add(alarmRecord);
                }
            }
        }
        return alarmRecords;
    }

    /**
     * Returns complete alarms based on the query, also retrieves comment history based on its value.
     * @param typeQuery
     *            -- {@link Query}
     * @param commentsHistoryRequired
     *            <br>
     *            <code>true</code> -- retrieves history of comments. <br>
     *            <code>false</code> -- otherwise
     * @return -- {@code List<{@link AlarmRecord}>}
     */
    public List<AlarmRecord> getAlarmRecords(final Query<TypeRestrictionBuilder> typeQuery, final boolean commentsHistoryRequired) {
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        final Iterator<PersistenceObject> poListIterator = queryExecutor.execute(typeQuery);

        while (poListIterator.hasNext()) {
            final PersistenceObject openAlarm = poListIterator.next();
            final AlarmRecord alarmRecord = alarmRecordConverter.prepareAlarmRecordForUserInterfaces(openAlarm, commentsHistoryRequired, false);
            removeUnusedAdditionalAttribute(alarmRecord);
            alarmRecords.add(alarmRecord);
        }

        return alarmRecords;
    }

    /**
     * Returns specific attributes of alarm having based on the query, specific attributes mentioned in outputAttributes.
     * @param typeQuery
     *            -- {@link Query}
     * @param outputAttributes
     *            -- specific attributes of alarm
     * @return -- {@code List<{@link AlarmRecord}>}
     */
    public List<AlarmRecord> getAlarmRecordsForSelectedAttributes(final Query<TypeRestrictionBuilder> typeQuery,
            final List<String> outputAttributes) {
        dpsProxy.getService().setWriteAccess(false);
        final DataBucket liveBucket = dpsProxy.getLiveBucket();
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>();
        final OpenAlarmProjectionBuilder projectionBuilder = new OpenAlarmProjectionBuilder(outputAttributes);
        final Projections projections = projectionBuilder.build();

        final int numberOfAttributes = outputAttributes.size();
        if (numberOfAttributes == 1) {
            final List<?> openAlarms = queryExecutor.executeProjection(typeQuery, projections.getIntialProjection());
            LOGGER.debug("Number of alarms found with given PoIds {} ", openAlarms.size());

            for (final Object openAlarm : openAlarms) {
                final Map<String, Object> openAlarmMap = new HashMap<String, Object>(1);
                openAlarmMap.put(outputAttributes.get(0), openAlarm);
                final AlarmRecord alarmRecord = new AlarmRecord(openAlarmMap, null, null);
                removeUnusedAdditionalAttribute(alarmRecord);
                alarmRecords.add(alarmRecord);
            }
        } else {
            final List<Object[]> openAlarmAttributes =
                    queryExecutor.executeProjection(typeQuery, projections.getIntialProjection(), projections.getOtherProjection());

            if (openAlarmAttributes != null && !openAlarmAttributes.isEmpty()) {
                LOGGER.debug("Number of alarms found with given PoIds {} ", openAlarmAttributes.size());
                for (final Object[] openAlarm : openAlarmAttributes) {
                    final AlarmRecord alarmRecord = alarmRecordConverter.prepareAlarmRecord(outputAttributes, numberOfAttributes, openAlarm);
                    removeUnusedAdditionalAttribute(alarmRecord);
                    alarmRecords.add(alarmRecord);
                }
            }
        }
        return alarmRecords;
    }

    /**
     * A inner class responsible for creating DPS projections. <br>
     * Projections are used to retrieve specific attributes of alarm from DPS.
     */
    private class OpenAlarmProjectionBuilder {
        List<String> attributes;
        int numberOfAttributes;

        OpenAlarmProjectionBuilder(final List<String> attributes) {
            this.attributes = attributes;
            numberOfAttributes = attributes.size();
        }

        /**
         * Projections will be created for all the expected attributes. <br>
         * First attribute will be captured in intialProjection, rest in otherProjection.
         * @return -- {@link Projections}
         */
        private Projections build() {
            final Projection[] otherProjections = new Projection[numberOfAttributes - 1];

            for (int attributePosition = 1; attributePosition < numberOfAttributes; attributePosition++) {
                otherProjections[attributePosition - 1] = buildProjection(attributes.get(attributePosition));
            }
            final Projections projections = new Projections(buildProjection(attributes.get(0)), otherProjections);
            return projections;
        }

        private Projection buildProjection(final String attribute) {
            Projection projection = null;

            if (EVENT_PO_ID.equalsIgnoreCase(attribute)) {
                projection = ProjectionBuilder.field(ObjectField.PO_ID);
            } else {
                projection = ProjectionBuilder.attribute(attribute);
            }
            return projection;
        }
    }

    /**
     * A POJO class holding intialProjection and otherProjections.
     * <p>
     * intialProjection : will be created for first attribute expected(can never be null) <br>
     * otherProjection : is array of projections, for other than first attribute projections will be created (may be null)
     */
    private final class Projections {
        private final Projection intialProjection;
        private final Projection[] otherProjection;

        private Projections(final Projection intialProjection, final Projection[] otherProjection) {
            this.intialProjection = intialProjection;
            this.otherProjection = otherProjection;
        }

        public Projection getIntialProjection() {
            return intialProjection;
        }

        public Projection[] getOtherProjection() {
            return otherProjection;
        }
    }

    private void removeUnusedAdditionalAttribute(final AlarmRecord alarmRecord) {
        if (alarmRecord.getAdditionalInformationWithSpecialCharacters(true) != null) {
            final String additionalInfoWithoutOriginalRecordType =
                    alarmRecord.getAdditionalInformationWithSpecialCharacters(true).replace(NON_SYNCHABLE_RECORD_TYPE_ADDITIONAL_ATTRIBUTE, "");
            alarmRecord.setAttribute(ADDITIONAL_INFORMATION, additionalInfoWithoutOriginalRecordType);
        }
    }
}
