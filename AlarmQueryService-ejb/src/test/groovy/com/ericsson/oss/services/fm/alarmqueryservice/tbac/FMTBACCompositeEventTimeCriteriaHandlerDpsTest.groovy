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
package com.ericsson.oss.services.fm.alarmqueryservice.tbac

import spock.lang.Unroll
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy
import com.ericsson.oss.services.fm.common.tbac.FMTBACAccessControl

class FMTBACCompositeEventTimeCriteriaHandlerDpsTest extends CdiSpecification {

    @ObjectUnderTest
    FMTBACCompositeEventTimeCriteriaHandler fmTbacAlarmHandler = new FMTBACCompositeEventTimeCriteriaHandler();

    @ImplementationInstance
    FMTBACAccessControl accessControl = Mock(FMTBACAccessControl)

    @Unroll("preProcess method with nodes= #nodes ,attributeName=#attributeName ,attributeValue=#attributeValue,operator=# ")
    def "Checking return value of preProcess method when a database exception occurs "() {
        given: "CompositeEventTimeCriteria"
        def alarmAttributeCriteria=new AlarmAttributeCriteria(attributeName:attributeName,attributeValue:attributeValue,operator:operator);
        def compositeEventTimeCriteria=new CompositeEventTimeCriteria(nodes:nodes,alarmAttributeCriteria:[alarmAttributeCriteria]);
        accessControl.isAuthorizedFromFdn(*_) >> authorized
        when: "invoked TBAC preProcess method"
        def output = fmTbacAlarmHandler.preProcess(accessControl, compositeEventTimeCriteria)
        then: "return response"
        output == response
        where:"inputs passed for CompositeEventTimeCriteria "
        nodes       | attributeName|   attributeValue                 |operator            |authorized |response
        []          |"fdn"         |    ""                            |Operator.STARTS_WITH|   true    |   false
    }
}
