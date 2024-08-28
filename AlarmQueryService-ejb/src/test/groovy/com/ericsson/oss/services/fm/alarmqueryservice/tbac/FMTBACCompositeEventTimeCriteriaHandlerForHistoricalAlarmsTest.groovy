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


import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.NETWORK_ELEMENT_DELIMITER

import spock.lang.Unroll

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria
import com.ericsson.oss.services.fm.alarmqueryservice.impl.AbstractBaseSpec
import com.ericsson.oss.services.fm.common.tbac.FMTBACAccessControl

class FMTBACCompositeEventTimeCriteriaHandlerForHistoricalAlarmsTest extends AbstractBaseSpec{
    @ObjectUnderTest
    FMTBACCompositeEventTimeCriteriaHandlerForHistoricalAlarms fmTbacHistoryAlarmHandler = new FMTBACCompositeEventTimeCriteriaHandlerForHistoricalAlarms();

    @ImplementationInstance
    FMTBACAccessControl accessControl = Mock(FMTBACAccessControl)

    static final String FDN1 = NETWORK_ELEMENT_DELIMITER + "AQS_GROOVY001"
    static final String FDN2 = NETWORK_ELEMENT_DELIMITER + "AQS_GROOVY002"
    static final String FDN3 = NETWORK_ELEMENT_DELIMITER + "AQS_GROOVY003"
    final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>()

    def setup() {
        final Map<String, Object> attributeMap1 = new HashMap<String, Object>()
        attributeMap1.put(FDN, FDN1);
        AlarmRecord alarmRecord1 = new AlarmRecord(attributeMap1, "NE1", null)
        alarmRecords.add(alarmRecord1)

        final Map<String, Object> attributeMap2 = new HashMap<String, Object>()
        attributeMap2.put(FDN, FDN2);
        AlarmRecord alarmRecord2 = new AlarmRecord(attributeMap2, "NE2", null)
        alarmRecords.add(alarmRecord2)

        final Map<String, Object> attributeMap3 = new HashMap<String, Object>()
        attributeMap3.put(FDN, FDN3);
        AlarmRecord alarmRecord3 = new AlarmRecord(attributeMap3, "NE3", null)
        alarmRecords.add(alarmRecord3)
    }

    @Unroll("preProcess method with nodes= #nodes ,attributeName=#attributeName ,attributeValue=#attributeValue,operator=# ")
    def "Checking return value of preProcess method when alarmAttributeCriteria is not null "() {
        given: "CompositeEventTimeCriteria"
        def alarmAttributeCriteria=new AlarmAttributeCriteria(attributeName:attributeName,attributeValue:attributeValue,operator:operator);
        def compositeEventTimeCriteria=new CompositeEventTimeCriteria(nodes:nodes,alarmAttributeCriteria:[alarmAttributeCriteria]);
        accessControl.isAuthorizedFromFdn(*_) >> authorized
        when: "invoked TBAC preProcess method"
        def output = fmTbacHistoryAlarmHandler.preProcess(accessControl, compositeEventTimeCriteria)
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
        []                                                                       |"fdn"         |"ABC"                             |Operator.STARTS_WITH|    true                           |    true
        []                                                                       |"fdn"         |    ""                            |Operator.STARTS_WITH|  new SecurityViolationException() |   true
        null                                                                     |"fdn"         |"NetworkElement=AQS_GROOVY003"    |Operator.EQ         |   false                           |true
        ["NetworkElement=AQS_GROOVY003", "NetworkElement=AQS_GROOVY002"]         |"fdn"         |"TES"                             |Operator.CONTAINS   |   true                            |  true
    }

    @Unroll("preProcess method with nodes= #nodes ,alarmAttributeCriteria=#alarmAttributeCriteria ")
    def "Checking return value of preProcess method when alarmAttributeCriteria is null or empty"() {
        given: "CompositeEventTimeCriteria"
        def compositeEventTimeCriteria=new CompositeEventTimeCriteria(nodes:nodes,alarmAttributeCriteria:alarmAttributeCriteria);
        accessControl.isAuthorizedFromFdn(*_) >> authorized
        when: "invoked TBAC preProcess method"
        def output = fmTbacHistoryAlarmHandler.preProcess(accessControl, compositeEventTimeCriteria)
        then: "return response"
        output == response
        where:"multiple inputs passed for CompositeEventTimeCriteria "
        nodes                               |authorized |   alarmAttributeCriteria |response
        []                                  |   true    |              []          |true
        null                                |   true    |             null         |true
        null                                |   false   |             null         |true
    }

    @Unroll("preProcess method with nodes= #nodes ,attributeName=#attributeName ,attributeValue=#attributeValue,operator=# ")
    def "when user search for a particular node which is not authorised when criteria is not null"() {
        given: ""
        def alarmAttributeCriteria=new AlarmAttributeCriteria(attributeName:attributeName,attributeValue:attributeValue,operator:operator);
        def compositeEventTimeCriteria=new CompositeEventTimeCriteria(nodes:nodes,alarmAttributeCriteria:[alarmAttributeCriteria]);
        accessControl.isAuthorizedFromFdn(*_) >> authorized
        when: "invoked TBAC preProcess method"
        def output = fmTbacHistoryAlarmHandler.preProcess(accessControl, compositeEventTimeCriteria)
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
        def output = fmTbacHistoryAlarmHandler.preProcess(accessControl, compositeEventTimeCriteria)
        then: "return response"
        RuntimeException runtimeException =thrown()
        runtimeException.message ==response
        where:"multiple inputs passed for CompositeEventTimeCriteria "
        nodes                               |authorized |   alarmAttributeCriteria |response
        ["NetworkElement=AQS_GROOVY002"]    |   false   |            []                | "Insufficient access rights for the node(s):[NetworkElement=AQS_GROOVY002]"
    }



    @Unroll("postProcess method ")
    def "Checking return value of postProcess method if instance is AlarmAttributeResponse"(){
        given:"AlarmAttributeResponse"
        def alarmAttributeCriteria=new AlarmAttributeCriteria(attributeName:attributeName,attributeValue:attributeValue,operator:operator);
        def compositeEventTimeCriteria=new CompositeEventTimeCriteria(nodes:nodes,alarmAttributeCriteria:[alarmAttributeCriteria]);
        def alarmAttributeResponse = new AlarmAttributeResponse(alarmRecords, "")
        accessControl.isAuthorizedFromFdn(*_) >> authorized
        when:"invoked TBAC postProcess method"
        fmTbacHistoryAlarmHandler.preProcess(accessControl,compositeEventTimeCriteria)
        def output=fmTbacHistoryAlarmHandler.postProcess(accessControl,alarmAttributeResponse)
        then :"return response"
        output.toString()==response
        where:"response is"
        nodes                                     | attributeName|   attributeValue                 |operator            |authorized|response
        ["NetworkElement=AQS_GROOVY002"]          |"fdn"         |"NetworkElement=AQS_GROOVY003"    |Operator.EQ         |   true   |"AlarmQueryResponse [response=, alarmRecords=[AlarmRecord [attributeMap={fdn=NetworkElement=AQS_GROOVY002}, additionalAttributes=AdditionalAttributes [nodeId=NE2, comments=null]], AlarmRecord [attributeMap={fdn=NetworkElement=AQS_GROOVY003}, additionalAttributes=AdditionalAttributes [nodeId=NE3, comments=null]]]]"
        null                                      |"fdn"         |"AQS"                             |Operator.CONTAINS   | true     |"AlarmQueryResponse [response=, alarmRecords=[AlarmRecord [attributeMap={fdn=NetworkElement=AQS_GROOVY001}, additionalAttributes=AdditionalAttributes [nodeId=NE1, comments=null]], AlarmRecord [attributeMap={fdn=NetworkElement=AQS_GROOVY002}, additionalAttributes=AdditionalAttributes [nodeId=NE2, comments=null]], AlarmRecord [attributeMap={fdn=NetworkElement=AQS_GROOVY003}, additionalAttributes=AdditionalAttributes [nodeId=NE3, comments=null]]]]"
    }
}
