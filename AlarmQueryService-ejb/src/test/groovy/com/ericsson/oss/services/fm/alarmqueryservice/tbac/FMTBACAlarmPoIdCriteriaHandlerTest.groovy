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

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ADDITIONAL_INFORMATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.NETWORK_ELEMENT_DELIMITER;
import static com.ericsson.oss.services.fm.common.tbac.FMTBACConstants.FMTBAC_ERROR;
import static com.ericsson.oss.services.fm.common.tbac.FMTBACConstants.INSUFFICIENT_ACCESS_RIGHTS_ERROR_MSG;

import spock.lang.Unroll

import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord
import com.ericsson.oss.services.fm.common.tbac.FMTBACAccessControl

class FMTBACAlarmPoIdCriteriaHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    FMTBACAlarmPoIdCriteriaHandler fmTBACAlarmPoIdCriteriaHandler = new FMTBACAlarmPoIdCriteriaHandler()

    @ImplementationInstance
    FMTBACAccessControl accessControl = Mock(FMTBACAccessControl)
    
    static final String FDN1 = NETWORK_ELEMENT_DELIMITER + "NE1"
    static final String FDN2 = NETWORK_ELEMENT_DELIMITER + "NE2"
    static final String FDN_ERROR = NETWORK_ELEMENT_DELIMITER + FMTBAC_ERROR
    static final String ADD_INFO = "tbacOk"
    static final String ADD_INFO_ERROR = FMTBAC_ERROR + ":" + INSUFFICIENT_ACCESS_RIGHTS_ERROR_MSG
    final List<AlarmRecord> alarmRecords = new ArrayList<AlarmRecord>()
    
    def setup() {
        final Map<String, Object> attributeMap1 = new HashMap<String, Object>()
        attributeMap1.put(FDN, FDN1);
        attributeMap1.put(ADDITIONAL_INFORMATION, ADD_INFO);
        AlarmRecord alarmRecord1 = new AlarmRecord(attributeMap1, "NE1", null)
        alarmRecords.add(alarmRecord1)
        
        final Map<String, Object> attributeMap2 = new HashMap<String, Object>()
        attributeMap2.put(FDN, FDN2);
        attributeMap2.put(ADDITIONAL_INFORMATION, ADD_INFO);
        AlarmRecord alarmRecord2 = new AlarmRecord(attributeMap2, "NE2", null)
        alarmRecords.add(alarmRecord2)
        
        final Map<String, Object> attributeMap3 = new HashMap<String, Object>()
        attributeMap3.put(FDN, null);
        attributeMap3.put(ADDITIONAL_INFORMATION, ADD_INFO);
        AlarmRecord alarmRecord3 = new AlarmRecord(attributeMap3, "NE3", null)
        alarmRecords.add(alarmRecord3)
    }

    @Unroll("preProcess method")
    def "Checking return value of preProcess method"() {
        given: "input parameters"
        def input = new Object()
        when: ""
        def output = fmTBACAlarmPoIdCriteriaHandler.preProcess(accessControl, input)
        then: ""
        output == true
    }
  
    @Unroll("postProcess method")
    def "Checking return value of postProcess method"() {
        given: "input parameters"
        AlarmAttributeResponse alarmAttributeResponse = new AlarmAttributeResponse(alarmRecords, "")
        accessControl.isAuthorizedFromFdn(FDN1) >> authorized1
        accessControl.isAuthorizedFromFdn(FDN2) >> authorized2
        when: ""
        fmTBACAlarmPoIdCriteriaHandler.preProcess(accessControl, null)
        AlarmAttributeResponse output = fmTBACAlarmPoIdCriteriaHandler.postProcess(accessControl, alarmAttributeResponse)
        then: ""
        output.getAlarmRecords().size == numOfAlarmRecords
        final String fdns = ""
        final String additionals = ""
        for (final AlarmRecord record : output.getAlarmRecords()) {
            fdns += record.getFdn()
            additionals += record.getAdditionalInformation()
        }
        fdns == expextedFdns
        additionals == expectedAdditionals
        where: ""
        authorized1 | authorized2 || numOfAlarmRecords | expextedFdns               | expectedAdditionals
        false       | false       || 3                 | "null"+FDN_ERROR+FDN_ERROR | ADD_INFO+ADD_INFO_ERROR+ADD_INFO_ERROR
        true        | true        || 3                 | FDN1+FDN2+"null"           | ADD_INFO+ADD_INFO+ADD_INFO
        false       | false       || 3                 | "null"+FDN_ERROR+FDN_ERROR | ADD_INFO+ADD_INFO_ERROR+ADD_INFO_ERROR
        true        | true        || 3                 | FDN1+FDN2+"null"           | ADD_INFO+ADD_INFO+ADD_INFO
        false       | false       || 3                 | "null"+FDN_ERROR+FDN_ERROR | ADD_INFO+ADD_INFO_ERROR+ADD_INFO_ERROR
        false       | true        || 3                 | FDN2+"null"+FDN_ERROR      | ADD_INFO+ADD_INFO+ADD_INFO_ERROR
        true        | false       || 3                 | FDN1+"null"+FDN_ERROR      | ADD_INFO+ADD_INFO+ADD_INFO_ERROR
        true        | true        || 3                 | FDN1+FDN2+"null"           | ADD_INFO+ADD_INFO+ADD_INFO
    }
}

