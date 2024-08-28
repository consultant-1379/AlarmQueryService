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
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmCountResponse
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria
import com.ericsson.oss.services.fm.alarmqueryservice.impl.AbstractBaseSpec
import com.ericsson.oss.services.fm.common.tbac.FMTBACAccessControl

class FMTBACCompositeEventTimeCriteriaHandlerTest extends AbstractBaseSpec {

    @ObjectUnderTest
    FMTBACCompositeEventTimeCriteriaHandler fmTbacAlarmHandler = new FMTBACCompositeEventTimeCriteriaHandler();

    @ImplementationInstance
    FMTBACAccessControl accessControl = Mock(FMTBACAccessControl)


    @Unroll("preProcess method with nodes= #nodes ,attributeName=#attributeName ,attributeValue=#attributeValue,operator=# ")
    def "Checking return value of preProcess method when alarmAttributeCriteria is not null "() {
        given: "CompositeEventTimeCriteria"
        def alarmAttributeCriteria=new AlarmAttributeCriteria(attributeName:attributeName,attributeValue:attributeValue,operator:operator);
        def compositeEventTimeCriteria=new CompositeEventTimeCriteria(nodes:nodes,alarmAttributeCriteria:[alarmAttributeCriteria]);
        accessControl.isAuthorizedFromFdn(*_) >> authorized
        when: "invoked TBAC preProcess method"
        def output = fmTbacAlarmHandler.preProcess(accessControl, compositeEventTimeCriteria)
        then: "return response"
        output == response
        where:"multiple inputs passed for CompositeEventTimeCriteria "
        nodes                                                                    | attributeName|   attributeValue                 |operator            |authorized                         |response
        ["NetworkElement=AQS_GROOVY002"]                                         |"fdn"         |"NetworkElement=AQS_GROOVY003"    |Operator.EQ         |   true                            |    true
        []                                                                       |"fdn"         |    ""                            |Operator.STARTS_WITH|   true                            |   true
        ["NetworkElement=AQS_GROOVY001", "NetworkElement=AQS_GROOVY002"]         |"fdn"         |"NetworkElement=AQS_GROOVY002"    |Operator.EQ         |   true                            |    true
        []                                                                       |"fdn"         |"NetworkElement=AQS_GROOVY003"    |Operator.EQ         |   true                            |    true
        ["NetworkElement=AQS_GROOVY003", "NetworkElement=AQS_GROOVY002"]         |"fdn"         |"AQS"                             |Operator.CONTAINS   |   true                            |    true
        []                                                                       |"test"        |"testing"                         |Operator.EQ         |   true                            |    true
        []                                                                       |"fdn"         |"NetworkElement=AQS"              |Operator.STARTS_WITH|   true                            |    true
        []                                                                       |"fdn"         |"02"                              |Operator.ENDS_WITH  |   true                            |    true
        []                                                                       |"fdn"         |"ABC"                             |Operator.STARTS_WITH|    true                           |    false
        []                                                                       |"fdn"         |    ""                            |Operator.STARTS_WITH|  new SecurityViolationException() |   true
        null                                                                     |"fdn"         |"NetworkElement=AQS_GROOVY003"    |Operator.EQ         |   false                           | true
    }

    @Unroll("preProcess method with nodes= #nodes ,alarmAttributeCriteria=#alarmAttributeCriteria ")
    def "Checking return value of preProcess method when alarmAttributeCriteria is null or empty"() {
        given: "CompositeEventTimeCriteria"
        def compositeEventTimeCriteria=new CompositeEventTimeCriteria(nodes:nodes,alarmAttributeCriteria:alarmAttributeCriteria);
        accessControl.isAuthorizedFromFdn(*_) >> authorized
        when: "invoked TBAC preProcess method"
        def output = fmTbacAlarmHandler.preProcess(accessControl, compositeEventTimeCriteria)
        then: "return response"
        output == response
        where:"multiple inputs passed for CompositeEventTimeCriteria "
        nodes                               |authorized |   alarmAttributeCriteria |response
        []                                  |   true    |              []          |true
        null                                |   true    |             null         |true
        null                                |   false   |             null         |false
    }

    @Unroll("preProcess method with nodes= #nodes ,attributeName=#attributeName ,attributeValue=#attributeValue,operator=# ")
    def "when user search for a particular node which is not authorised when criteria is not null"() {
        given: ""
        def alarmAttributeCriteria=new AlarmAttributeCriteria(attributeName:attributeName,attributeValue:attributeValue,operator:operator);
        def compositeEventTimeCriteria=new CompositeEventTimeCriteria(nodes:nodes,alarmAttributeCriteria:[alarmAttributeCriteria]);
        accessControl.isAuthorizedFromFdn(*_) >> authorized
        when: "invoked TBAC preProcess method"
        def output = fmTbacAlarmHandler.preProcess(accessControl, compositeEventTimeCriteria)
        then: "return response"
        RuntimeException runtimeException =thrown()
        runtimeException.message ==response
        where:"multiple inputs passed for CompositeEventTimeCriteria "
        nodes                                                            | attributeName|   attributeValue                 |operator            |authorized      |response
        null                                                             |"fdn"         |"NetworkElement=AQS_GROOVY001"    |Operator.EQ         |   false        | "Insufficient access rights for the node(s):[NetworkElement=AQS_GROOVY001]"
        ["NetworkElement=AQS_GROOVY001"]                                 |"fdn"         |"NetworkElement=AQS_GROOVY001"    |Operator.EQ         |   false        | "Insufficient access rights for the node(s):[NetworkElement=AQS_GROOVY001]"
    }

    @Unroll("preProcess method with nodes= #nodes ,alarmAttributeCriteria=#alarmAttributeCriteria ")
    def "when user search for a particular node which is not authorised when criteria is empty"() {
        given: "CompositeEventTimeCriteria"
        def compositeEventTimeCriteria=new CompositeEventTimeCriteria(nodes:nodes,alarmAttributeCriteria:alarmAttributeCriteria);
        accessControl.isAuthorizedFromFdn(*_) >> authorized
        when: "invoked TBAC preProcess method"
        def output = fmTbacAlarmHandler.preProcess(accessControl, compositeEventTimeCriteria)
        then: "return response"
        RuntimeException runtimeException =thrown()
        runtimeException.message ==response
        where:"multiple inputs passed for CompositeEventTimeCriteria "
        nodes                               |authorized |   alarmAttributeCriteria |response
        ["NetworkElement=AQS_GROOVY002"]    |   false   |            []            | "Insufficient access rights for the node(s):[NetworkElement=AQS_GROOVY002]"
    }



    @Unroll("postProcess method ")
    def "Checking return value of postProcess method if instance is AlarmAttributeResponse"(){
        given:"AlarmAttributeResponse"
        def alarmAttributeResponse=new AlarmAttributeResponse(alarmrecords,response);
        when:"invoked TBAC postProcess method"
        def output=fmTbacAlarmHandler.postProcess(accessControl,alarmAttributeResponse)
        then :"return response"
        where:"response is"
        alarmrecords     |response   | result
        new ArrayList()  |"No Alarms"|"AlarmQueryResponse [response=No Alarms, alarmRecords=[]]"
    }

    @Unroll("postProcess method ")
    def "Checking return value of postProcess method if instance is AlarmCountResponse"(){
        given:"AlarmCountResponse"
        def alarmCountResponse=new AlarmCountResponse(count,response);
        when:"invoked TBAC postProcess method"
        def output=fmTbacAlarmHandler.postProcess(accessControl,alarmCountResponse)
        then :"return response"
        output.toString()==result
        where:"response is"
        count | response   |result
        10L   | "Success"  |"AlarmCountResponse [response=Success, alarmCount=10]"
    }
}