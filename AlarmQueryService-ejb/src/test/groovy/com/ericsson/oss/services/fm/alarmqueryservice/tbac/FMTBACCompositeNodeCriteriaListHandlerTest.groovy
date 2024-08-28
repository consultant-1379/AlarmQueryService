/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
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
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmPoIdResponse
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.LogicalCondition
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria
import com.ericsson.oss.services.fm.alarmqueryservice.impl.AbstractBaseSpec
import com.ericsson.oss.services.fm.common.tbac.FMTBACAccessControl


class FMTBACCompositeNodeCriteriaListHandlerTest extends  AbstractBaseSpec{

    @ObjectUnderTest
    FMTBACCompositeNodeCriteriaListHandler fmTbacAlarmActionHandler = new FMTBACCompositeNodeCriteriaListHandler();

    @ImplementationInstance
    FMTBACAccessControl accessControl = Mock(FMTBACAccessControl)

    static final String FDN0 = "NetworkElement=AQS_GROOVY000"
    static final String FDN1 = "NetworkElement=AQS_GROOVY001"
    static final String FDN2 = "NetworkElement=AQS_GROOVY002"

    @Unroll("preProcess method with a list of CompositeNodeCriteria for a particular node")
    def "Checking return value of preProcess method when user search for a particular node which is not authorised and alarmAttributeCriteria is not null"() {
        given: "CompositeNodeCriteria"
        def alarmAttributeCriteriaFirstAttributeFirstValue = new AlarmAttributeCriteria(attributeName : attributeName1,attributeValue : value1, operator : operator, logicalCondition : logicalCondition)
        def alarmAttributeCriteriaFirstAttributeSecondValue = new AlarmAttributeCriteria(attributeName : attributeName1,attributeValue : value2, operator : operator )
        def alarmAttributeCriteriaSecondAttributeFirstValue = new AlarmAttributeCriteria(attributeName : attributeName2,attributeValue : value1, operator : operator, logicalCondition : logicalCondition)
        def alarmAttributeCriteriaSecondAttributeSecondValue = new AlarmAttributeCriteria(attributeName : attributeName2,attributeValue : value2, operator : operator )

        def compositeNodeCriteria1 = new CompositeNodeCriteria(nodes:node1, alarmAttributeCriteria: [alarmAttributeCriteriaFirstAttributeFirstValue, alarmAttributeCriteriaFirstAttributeSecondValue])
        def compositeNodeCriteria2 = new CompositeNodeCriteria(nodes:node1, alarmAttributeCriteria: [alarmAttributeCriteriaSecondAttributeFirstValue, alarmAttributeCriteriaSecondAttributeSecondValue])
        def compositeNodeCriteria3 = new CompositeNodeCriteria(nodes:node2, alarmAttributeCriteria: [alarmAttributeCriteriaFirstAttributeFirstValue, alarmAttributeCriteriaFirstAttributeSecondValue])
        def compositeNodeCriteria4 = new CompositeNodeCriteria(nodes:node2, alarmAttributeCriteria: [alarmAttributeCriteriaSecondAttributeFirstValue, alarmAttributeCriteriaSecondAttributeSecondValue])
        def compositeNodeCriterias = [compositeNodeCriteria1, compositeNodeCriteria2, compositeNodeCriteria3, compositeNodeCriteria4]

        accessControl.isAuthorizedFromFdn(FDN1) >> authorized[0]
        accessControl.isAuthorizedFromFdn(FDN2) >> authorized[1]

        when: "invoked TBAC preProcess method"
        def output = fmTbacAlarmActionHandler.preProcess(accessControl, compositeNodeCriterias)

        then: "return response"
        RuntimeException runtimeException = thrown()
        runtimeException.message == response

        where : "Multiple inputs supplied to CompositeNodeCriteria"
        node1 |node2 |authorized   | attributeName1   | attributeName2 |     value1                            | value2                             | operator    |  logicalCondition    | response
        [FDN1]|[FDN2]|[true, false]|'ciFirstGroup'    |'ciSecondGroup' |  '81d4fae-7dec-11d0-a765-00a0c91e6bf6'|'f91a6e32-e523-b217-7C3912ad3012'   | Operator.EQ |  LogicalCondition.OR | "Insufficient access rights for the node(s):[NetworkElement=AQS_GROOVY002]"
        }

        @Unroll("preProcess method with a list of CompositeNodeCriteria without nodes")
        def "Checking return value of preProcess method when user search for all nodes but for a particular node is not authorized and alarmAttributeCriteria is not null"() {
            given: "CompositeNodeCriteria"
            def alarmAttributeCriteriaFirstAttributeFirstValue = new AlarmAttributeCriteria(attributeName : attributeName1,attributeValue : value1, operator : operator, logicalCondition : logicalCondition)
            def alarmAttributeCriteriaFirstAttributeSecondValue = new AlarmAttributeCriteria(attributeName : attributeName1,attributeValue : value2, operator : operator )
            def alarmAttributeCriteriaSecondAttributeFirstValue = new AlarmAttributeCriteria(attributeName : attributeName2,attributeValue : value1, operator : operator, logicalCondition : logicalCondition)
            def alarmAttributeCriteriaSecondAttributeSecondValue = new AlarmAttributeCriteria(attributeName : attributeName2,attributeValue : value2, operator : operator )

            def compositeNodeCriteria1 = new CompositeNodeCriteria(alarmAttributeCriteria: [alarmAttributeCriteriaFirstAttributeFirstValue, alarmAttributeCriteriaFirstAttributeSecondValue])
            def compositeNodeCriteria2 = new CompositeNodeCriteria(alarmAttributeCriteria: [alarmAttributeCriteriaSecondAttributeFirstValue, alarmAttributeCriteriaSecondAttributeSecondValue])
            def compositeNodeCriterias = [compositeNodeCriteria1, compositeNodeCriteria2]

            accessControl.isAuthorizedFromFdn(FDN0) >> authorized[0]
            accessControl.isAuthorizedFromFdn(FDN1) >> authorized[1]
            accessControl.isAuthorizedFromFdn(FDN2) >> authorized[2]

            when: "invoked TBAC preProcess method"
            def output = fmTbacAlarmActionHandler.preProcess(accessControl, compositeNodeCriterias)

            then: "the node not authorized is skipped"
            output == response
            compositeNodeCriterias[0].getNodes() == [FDN1, FDN0]
            compositeNodeCriterias[1].getNodes() == [FDN1, FDN0]

            where : "Multiple inputs supplied to CompositeNodeCriteria"
            authorized          | attributeName1   | attributeName2 |     value1                            | value2                             | operator    |  logicalCondition    | response
            [true, true, false] | 'ciFirstGroup'   |'ciSecondGroup' |  '81d4fae-7dec-11d0-a765-00a0c91e6bf6'|'f91a6e32-e523-b217-7C3912ad3012'   | Operator.EQ |  LogicalCondition.OR | true
            }
}
