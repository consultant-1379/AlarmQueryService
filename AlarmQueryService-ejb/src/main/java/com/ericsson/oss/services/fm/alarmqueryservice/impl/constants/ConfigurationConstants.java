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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.constants;

/**
 * Class holds the Configuration constants which are used in query.
 *
 */
public final class ConfigurationConstants {

    public static final String MAX_NES_ALLOWED_PER_OPENALARM_QUERY = "maxNEsAllowedPerOpenAlarmQuery";
    public static final String MAX_NES_ALLOWED_PER_HISTORICALALARM_QUERY = "maxNEsAllowedPerHistoricalAlarmQuery";
    public static final String MAX_NES_ALLOWED_PER_INRESTRICTION = "maxNEsAllowedPerInRestriction";
    public static final String MAX_HISTORY_ALARMS_SHOWN = "maxNumberOfHistoryAlarmsShown";
    public static final String SOLR_CONNECTION_TIMEOUT = "solrConnectionTimeout";
    public static final String SOLR_QUERY_TIMEOUT = "solrQueryTimeout";

    private ConfigurationConstants() {
    }
}
