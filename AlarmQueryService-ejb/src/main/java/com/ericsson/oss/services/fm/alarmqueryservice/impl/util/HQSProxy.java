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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.util;

import javax.enterprise.context.ApplicationScoped;

import com.ericsson.oss.itpf.datalayer.historicalqueryservice.HistoricalQueryService;
import com.ericsson.oss.itpf.datalayer.historicalqueryservice.query.Query;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;

/**
 *
 * An ApplicationScoped bean, for providing a HQS instance.
 *
 */
@ApplicationScoped
public class HQSProxy {

    @EServiceRef
    private HistoricalQueryService historicalQueryService;

    public HistoricalQueryService getHistoricalQueryService() {
        return historicalQueryService;
    }

    public Query getQuery() {
        return historicalQueryService.createQuery();
    }

}
