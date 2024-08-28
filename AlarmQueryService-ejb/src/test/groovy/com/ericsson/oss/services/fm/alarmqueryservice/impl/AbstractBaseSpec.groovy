/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.fm.alarmqueryservice.impl

import com.ericsson.oss.services.fm.common.cache.FMXAdditionalAttributesCacheManager

import javax.cache.Cache
import javax.inject.Inject

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.providers.custom.model.ModelPattern
import com.ericsson.cds.cdi.support.providers.custom.model.RealModelServiceProvider
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.itpf.sdk.cache.annotation.NamedCache
import com.ericsson.oss.services.fm.models.FmxAddtionalAttributesRecord

import javax.persistence.criteria.CriteriaBuilder

/**
 * Class is responsible initializing DPS, Model service and cache etc.
 *
 */
public class AbstractBaseSpec extends CdiSpecification {

    /**
     * Real Model Provider allows the test to use the real model service using the JAr dependencies
     * (just declare the models you need as a test scope dependency)
     */
    private static RealModelServiceProvider realModelServiceProvider = new RealModelServiceProvider([
            new ModelPattern('dps_primarytype', 'FM', 'DynamicAlarmAttributeInformation', '.*'),
            new ModelPattern('dps_primarytype', 'FM', 'OpenAlarm', '.*'),
            new ModelPattern('dps_primarytype', 'FM', 'CommentOperation', '.*')
    ])

    /**
     * Customize the injection provider
     * */
    @Override
    public Object addAdditionalInjectionProperties(InjectionProperties injectionProperties) {
        injectionProperties.autoLocateFrom('com.ericsson.oss.services.fm.alarmqueryservice.impl')
        injectionProperties.addInjectionProvider(realModelServiceProvider)
    }

    RuntimeConfigurableDps runtimeDps

    TestSetupInitializer testSetupInitializer

    @Inject
    FMXAdditionalAttributesCacheManager fMXAdditionalAttributesCacheManager

    @Inject
    @NamedCache("FmxAdditionalAttributeCache")
    private Cache<Boolean, FmxAddtionalAttributesRecord> fmxAdditionalAttributeCache

    // TODO: This method is executed for every test case discussing with coaches to run only once for class.
    def setup() {
        runtimeDps = cdiInjectorRule.getService(RuntimeConfigurableDps)
        testSetupInitializer = new TestSetupInitializer()
        testSetupInitializer.addDataToCache(fmxAdditionalAttributeCache)
        testSetupInitializer.persistsHiddenAlarms(runtimeDps)
        testSetupInitializer.persistsVisibleAlarms(runtimeDps)
        testSetupInitializer.persistsNodes(runtimeDps)
        runtimeDps.resetTransactionalState()
        fMXAdditionalAttributesCacheManager.fmxAdditionalAttributeCache = fmxAdditionalAttributeCache
    }
}
