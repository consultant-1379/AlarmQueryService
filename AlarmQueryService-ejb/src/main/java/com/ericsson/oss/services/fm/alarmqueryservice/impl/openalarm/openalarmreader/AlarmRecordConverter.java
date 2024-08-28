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
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.OBJECT_OF_REFERENCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.RECORD_TYPE;
import static com.ericsson.oss.services.fm.common.constants.AddInfoConstants.CI_GROUP_1;
import static com.ericsson.oss.services.fm.common.constants.AddInfoConstants.CI_GROUP_2;
import static com.ericsson.oss.services.fm.common.constants.AddInfoConstants.ROOT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.common.addinfo.CorrelationType;
import com.ericsson.oss.services.fm.common.addinfo.TargetAdditionalInformationHandler;
import com.ericsson.oss.services.fm.models.processedevent.FMProcessedEventType;

/**
 * Responsible for preparing alarm records based on provided inputs.<br>
 * The inputs can either be complete alarm (in the form of a {@link PersistenceObject} or specific attributes of alarm<br>
 *
 *
 */
public class AlarmRecordConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRecordConverter.class);

    @Inject
    TargetAdditionalInformationHandler targetAdditionalInformationHandler;

    @Inject
    private CommentHistoryReader historyCommentsReader;

    @Inject
    private AncestorMOFinder ancestorMOFinder;

    /**
     * Returns alarm record which is prepared from {@link PersistenceObject} based on the outputAttributes projection.
     *
     * @param persistenceObject
     *            -- {@link PersistenceObject}
     * @param outputAttributes
     *            -- expected attributes
     * @return --{@link AlarmRecord}
     */
    AlarmRecord prepareAlarmRecordForSeletedAttributes(final PersistenceObject persistenceObject, final List<String> outputAttributes) {
        final Map<String, Object> alarmAttributeMap = persistenceObject.getAllAttributes();
        rebuildCorrelationInformation(alarmAttributeMap);

        if (outputAttributes.contains(EVENT_PO_ID)) {
            alarmAttributeMap.put(EVENT_PO_ID, persistenceObject.getPoId());
        }

        final Map<String, Object> alarmAttributes = new HashMap<String, Object>();
        for (final String attribute : outputAttributes) {
            alarmAttributes.put(attribute, alarmAttributeMap.get(attribute));
        }

        final AlarmRecord alarmRecord = new AlarmRecord(alarmAttributes, null, null);
        return alarmRecord;
    }

    /**
     * Returns alarm record which is prepared from {@link PersistenceObject} based on the outputAttributes projection.
     * <p>
     * Record type check is required here ,because
     *
     * <p>
     * If a repeated alarm has been received on top of update alarm then additional attribute information will be overwritten. So sending the alarm
     * records only with record type update.
     *
     * @param persistenceObject
     *            -- {@link PersistenceObject}
     * @param outputAttributes
     *            -- expected attributes
     * @return --{@link AlarmRecord}
     */
    AlarmRecord prepareAlarmRecordForAdditionalAttributes(final PersistenceObject persistenceObject, final List<String> outputAttributes) {
        final Map<String, Object> alarmAttributeMap = persistenceObject.getAllAttributes();

        rebuildCorrelationInformation(alarmAttributeMap);

        if (alarmAttributeMap.get(RECORD_TYPE).toString().equals(FMProcessedEventType.UPDATE.name())) {
            if (outputAttributes.contains(EVENT_PO_ID)) {
                alarmAttributeMap.put(EVENT_PO_ID, persistenceObject.getPoId());
            }

            final Map<String, Object> alarmAttributes = new HashMap<String, Object>();
            for (final String attribute : outputAttributes) {
                alarmAttributes.put(attribute, alarmAttributeMap.get(attribute));
            }

            final AlarmRecord alarmRecord = new AlarmRecord(alarmAttributes, null, null);
            return alarmRecord;
        }
        return null;

    }

    /**
     * Returns alarm record with complete open alarm attributes, also set comment history and nodeId based on Input.
     *
     * @param persistenceObject
     *            -- {@link PersistenceObject}
     * @param commentHistoryRequired
     *            -- <code> true</code> retrieves comment history <code> false</code>otherwise
     * @param nodeIdRequired
     *            -- <code> true</code> retrieves node poId <code> false</code> otherwise
     * @return -- {@link AlarmRecord}
     */

    AlarmRecord prepareAlarmRecordForUserInterfaces(final PersistenceObject persistenceObject, final boolean commentHistoryRequired,
                                                    final boolean nodeIdRequired) {
        final Map<String, Object> alarmAttributeMap = persistenceObject.getAllAttributes();

        rebuildCorrelationInformation(alarmAttributeMap);

        final Long eventPoId = persistenceObject.getPoId();
        alarmAttributeMap.put(EVENT_PO_ID, eventPoId);

        List<Map<String, Object>> comments = null;
        if (commentHistoryRequired) {
            comments = historyCommentsReader.getAllComments(eventPoId);
        }

        String nodeId = null;

        if (nodeIdRequired) {
            final Object objectOfReference = alarmAttributeMap.get(OBJECT_OF_REFERENCE);
            if (objectOfReference != null) {
                nodeId = ancestorMOFinder.find(objectOfReference.toString());
            }
        }
        return new AlarmRecord(alarmAttributeMap, nodeId, comments);
    }

    /**
     * Returns alarm record which is prepared from projected attributes retrieved from DPS.
     *
     * @param outputAttributes
     *            -- specific attributes of an alarm
     * @param attributeCount
     *            -- count of outputAttributes
     * @param openAlarm
     *            -- Projected attributes retrieved from DPS
     * @return -- {@link AlarmRecord}
     */
    AlarmRecord prepareAlarmRecord(final List<String> outputAttributes, final int attributeCount, final Object[] openAlarm) {
        final Map<String, Object> openAlarmMap = new HashMap<String, Object>(attributeCount);
        for (int attributeCounter = 0; attributeCounter <= (attributeCount - 1); attributeCounter++) {
            openAlarmMap.put(outputAttributes.get(attributeCounter), openAlarm[attributeCounter]);
        }
        rebuildCorrelationInformation(openAlarmMap);
        return new AlarmRecord(openAlarmMap, null, null);
    }

    /**
     * Enrich Correlation Information in targetAdditionalInfo attribute inside additionalInformation attribute.
     *
     * @param alarmAttributeMap
     *            -- all attributes of an alarm
     *
     */
    private void rebuildCorrelationInformation(final Map<String, Object> alarmAttributeMap) {
        if (alarmAttributeMap.get(ROOT) != null && !alarmAttributeMap.get(ROOT).toString().isEmpty()) {
            try {
                final String enrichedAdditionalInfo = targetAdditionalInformationHandler.enrichAdditionalInfoCorrelationInformation(
                        (alarmAttributeMap.get(ADDITIONAL_INFORMATION) != null) ? alarmAttributeMap.get(ADDITIONAL_INFORMATION).toString() : null,
                        (alarmAttributeMap.get(CI_GROUP_1) != null) ? alarmAttributeMap.get(CI_GROUP_1).toString() : null,
                        (alarmAttributeMap.get(CI_GROUP_2) != null) ? alarmAttributeMap.get(CI_GROUP_2).toString() : null,
                        CorrelationType.valueOf(alarmAttributeMap.get(ROOT).toString()));
                LOGGER.debug("additional information attribute AFTER enrichment= {}", enrichedAdditionalInfo);
                if (enrichedAdditionalInfo != null) {
                    alarmAttributeMap.put(ADDITIONAL_INFORMATION,
                            (enrichedAdditionalInfo != null) ? enrichedAdditionalInfo : alarmAttributeMap.get(ADDITIONAL_INFORMATION));
                }
            } catch (final IllegalArgumentException illegalArgumentException) {
                LOGGER.error("Invalid value:\"{}\" for CorrelationType {}", alarmAttributeMap.get(ROOT).toString(), illegalArgumentException);
            }
        }
    }
}
