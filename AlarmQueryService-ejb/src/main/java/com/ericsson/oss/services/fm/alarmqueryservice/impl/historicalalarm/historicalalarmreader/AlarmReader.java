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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.historicalalarm.historicalalarmreader;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.NUMBER_OF_ADDITIONAL_RECORDS_TO_AVOID_DUPLICATES;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.HistoricalQueryService;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.Query;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.restriction.Restriction;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.configuration.ConfigurationListener;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.HQSProxy;

/**
 * Delegate class responsible for retrieving the historical alarms. It uses {@link HistoricalQueryService} for the retrieval
 **/
public class AlarmReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmReader.class);

    @Inject
    private HQSProxy hqsProxy;

    @Inject
    private ConfigurationListener configurationListener;

    /**
     * Returns Historical Alarms based on the restriction set on query.
     * @param query
     *            -- {@link Query}
     * @param compositeRestriction
     *            -- {@Link Restriction}
     * @return -- historical Alarms.
     */
    public List<AlarmRecord> getHistoricalAlarms(final Query query, final Restriction compositeRestriction,
            final int configuredMaxNumberOfHistoryAlarmShown) {
        final Set<AlarmRecord> alarmRecords = new LinkedHashSet<>();
        if (compositeRestriction != null) {
            List<Map<String, Object>> historicalAlarmData;
            // Fetching 2K extra records just in case if there are any duplicates present and are filtered out
            query.setPageFilter(0, configuredMaxNumberOfHistoryAlarmShown + NUMBER_OF_ADDITIONAL_RECORDS_TO_AVOID_DUPLICATES);
            query.setRestriction(compositeRestriction);
            historicalAlarmData = hqsProxy.getHistoricalQueryService().execute(query, configurationListener.getSolrConnectionTimeout(),
                    configurationListener.getSolrQueryTimeout());
            LOGGER.debug("Number of entries fetched from the historicalAlarm DB are {}", historicalAlarmData.size());
            for (final Map<String, Object> historicalAlarmMap : historicalAlarmData) {
                final AlarmRecord alarmRecord = new AlarmRecord(historicalAlarmMap, null, null);
                alarmRecords.add(alarmRecord);
            }
            historicalAlarmData.clear();
        }
        LOGGER.debug("Number of AlarmRecords after filtering the duplicates out are {}", alarmRecords.size());

        return extractRequiredNumberOfAlarms(alarmRecords, configuredMaxNumberOfHistoryAlarmShown);
    }

    /**
     * Returns the sublist if the size of AlarmRecords set is more than the configured number of History Alarm to be shown, otherwise returns the
     * whole set converted to list.
     * @param alarmRecords
     *            -- {@link AlarmRecord}
     * @param configuredMaxNumberOfHistoryAlarmShown
     *            -- Configured maximum number of History Alarms to be shown
     */
    private List<AlarmRecord> extractRequiredNumberOfAlarms(final Set<AlarmRecord> alarmRecords,
            final Integer configuredMaxNumberOfHistoryAlarmShown) {
        return (alarmRecords.size() > configuredMaxNumberOfHistoryAlarmShown)
                ? new ArrayList<AlarmRecord>(alarmRecords).subList(0, configuredMaxNumberOfHistoryAlarmShown)
                : new ArrayList<AlarmRecord>(alarmRecords);
    }
}
