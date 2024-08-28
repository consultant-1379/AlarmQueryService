/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.impl.instrumentation;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.instrument.annotation.InstrumentedBean;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute.CollectionType;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute.Visibility;
import com.ericsson.oss.itpf.sdk.instrument.annotation.Profiled;

/**
 * Instrumentation bean for Alarm Query Service.
 */
@InstrumentedBean(displayName = "AlarmQueryService Matrix", description = "Instrumentation matrix for Solr loader")
@ApplicationScoped
@Profiled
public class AqsInstrumentationBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AqsInstrumentationBean.class);

    private int numberOfFmSolrReadFailure;

    @MonitoredAttribute(displayName = "Number of documents failed to write to Solr",
                        visibility = Visibility.ALL,
                        collectionType = CollectionType.TRENDSUP)
    public int getNumberOfFmSolrReadFailure() {
        return numberOfFmSolrReadFailure;
    }

    public void incrementNumberOfFmSolrReadFailure() {
        LOGGER.debug("Incrementing the number of failed history search.");
        numberOfFmSolrReadFailure++;
    }
}
