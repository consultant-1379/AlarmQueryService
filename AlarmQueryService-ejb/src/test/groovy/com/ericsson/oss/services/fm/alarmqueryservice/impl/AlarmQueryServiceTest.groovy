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

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmCountResponse
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmPoIdResponse
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.LogicalCondition
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmPoIdCriteria
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion.SortSequence
import com.ericsson.oss.services.fm.common.addinfo.CorrelationType

import spock.lang.Unroll

public class AlarmQueryServiceTest extends AbstractBaseSpec{

    @ObjectUnderTest
    AlarmQueryServiceBean alarmQueryServiceBean

    @Unroll("Retrieving Poids based on CompositeNodeCriteria having #attributeName set for current iteration ")
    def "Getting PoIds for CompositeNodeCriteria "() {

        given :"any CompositeNodeCriteria"
        def alarmAttributeCriteria = new AlarmAttributeCriteria(attributeName : attributeName,attributeValue : attributeValue, operator: operator, logicalCondition : LogicalCondition.AND )
        def compositeNodeCriteria = new CompositeNodeCriteria(nodes:nodes, alarmAttributeCriteria: [alarmAttributeCriteria], sortAttribute:sortAttribute)

        when: " to retrieve po ids from DB using the Alarm Query Service Bean"
        AlarmPoIdResponse alarmPoIdResponse = alarmQueryServiceBean.getAlarmPoIds(compositeNodeCriteria, false)

        then: "assert the alarmPoIdResponse returned from DB"
        alarmPoIdResponse.response == response

        where : "Multiple inputs supplied to CompositeNodeCriteria "
        attributeName      |       attributeValue       | operator       |          nodes                       |   sortAttribute  |  response   | numberOfPoIdsFound
        'eventTime'        |     new Date(1532614958671)|  Operator.NE   |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'insertTime'   | 'Success'   |  1
        'specificProblem'  |     'specificProblem'      |  Operator.EQ   |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'insertTime'   | 'Success'   |  3
        null               |     'specificProblem'      |  Operator.EQ   |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'insertTime'   | 'Failed to read poIds from DB. Exception details are : Attribute Name / Attribute Value given is invalid' |  0
    }

    @Unroll("Retrieving Poids based on CompositeNodeCriteria having #attributeName set for current iteration ")
    def "Getting PoIds for CompositeNodeCriteria and count "() {

        given :"any CompositeNodeCriteria"
        def alarmAttributeCriteria = new AlarmAttributeCriteria(attributeName : attributeName,attributeValue : attributeValue, operator: operator, logicalCondition : LogicalCondition.AND )
        def alarmAttributeCriteria1 = new AlarmAttributeCriteria(attributeName : attributeName1,attributeValue : attributeValue1, operator: operator1, logicalCondition : LogicalCondition.AND )
        def compositeNodeCriteria = new CompositeNodeCriteria(nodes:nodes, alarmAttributeCriteria: [alarmAttributeCriteria, alarmAttributeCriteria1], sortAttribute:sortAttribute)

        when: " to retrieve po ids from DB using the Alarm Query Service Bean"
        AlarmPoIdResponse alarmPoIdResponse = alarmQueryServiceBean.getAlarmPoIds(compositeNodeCriteria, false)
        AlarmCountResponse getAlarmCount = alarmQueryServiceBean.getAlarmCount(compositeNodeCriteria, false)

        then: "assert the alarmPoIdResponse returned from DB"
        alarmPoIdResponse.response == response
        alarmPoIdResponse.poIds.size() == numberOfPoIdsFound
        getAlarmCount.getAlarmCount() == numberOfPoIdsFound

        where : "Multiple inputs supplied to CompositeNodeCriteria "
        attributeName  |    attributeValue                                             | operator                | attributeName1   |       attributeValue1                                           | operator1            |         nodes                       |   sortAttribute  |  response   | numberOfPoIdsFound
        'eventTime'    | [new Date(1532614958671-2000), new Date(1532614958671+2000)]  | Operator.NOT_BETWEEN    | 'eventTime'      |  [new Date(1532614958671-62000), new Date(1532614958671+58000)] | Operator.NOT_BETWEEN | ["NetworkElement=AQS_ARQUILLIAN001"]|   'insertTime'   | 'Success'   |  5
    }

   @Unroll("Retrieving alarms from db based on different values of sort attribute set in CompositeNodeCriteria and sort attribute #sortAttribute set for current iteration")
     def "Getting alarm for CompositeNodeCriteria "() {

        given :"any CompositeNodeCriteria"
        def alarmSortCriterion1 = new AlarmSortCriterion(sortAttribute:sortAttribute, sortOrder:sortOrder1, sortSequence:sortSequence1)
        def alarmAttributeCriteria = new AlarmAttributeCriteria(attributeName : attributeName,attributeValue : attributeValue, operator: operator )
        def compositeNodeCriteria = new CompositeNodeCriteria(nodes:nodes, alarmAttributeCriteria: [alarmAttributeCriteria], alarmSortCriteria:[alarmSortCriterion1])
        def expectedOutputAttributes = new ExpectedOutputAttributes(outputAttributes: outputAttributes, nodeIdRequired:false, commentHistoryRequired:false)

        when : "to retrieve alarms from DB using the Alarm Query Service Bean"
        AlarmAttributeResponse alarmAttributeResponse = alarmQueryServiceBean.getAlarms(compositeNodeCriteria, expectedOutputAttributes, false)

        then : "assert the alarmPoIdResponse returned from DB"
        alarmAttributeResponse.response == response
        alarmAttributeResponse.getAlarmRecords().size() == numberOfAlarmFound
        if (numberOfAlarmFound != 0) {
            assert alarmAttributeResponse.getAlarmRecords().get(0).getAttribute(attributeValue) == attributeValue
        }

        where : "Multiple inputs supplied to CompositeNodeCriteria"
        attributeName     |     attributeValue   | operator    |           nodes                      |   sortAttribute          |      sortOrder1       |        sortSequence1           |         outputAttributes              ||                 response                                                | numberOfAlarmFound
        'probableCause'   |   'probableCause'    | Operator.EQ |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxTest'              | SortingOrder.ASCENDING| SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'specificProblem']||  "Error while retrieving alarms from DB {}null"                          |       0
        'specificProblem' |   'specificProblem'  | Operator.EQ |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxdisbledattribute4' | SortingOrder.ASCENDING| SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'specificProblem']||  "Error while retrieving alarms from DB {}Invalid sort/search attribute" |       0
        'probableCause'   |   'probableCause'    | Operator.EQ |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | SortingOrder.ASCENDING| SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'specificProblem']||                 "Success"                                                |       6
    }

    @Unroll("Retrieving alarms based on same attribute #attributeValue with different operator #logicalCondition,#logicalCondition1,#logicalCondition2.")
    def "Getting alarms for CompositeNodeCriteria for same attribute alarmAttributeCriteria"() {

        given :"same attribute alarmAttributeCriteria"
        def alarmAttributeCriteria = new AlarmAttributeCriteria(attributeName : attributeName,attributeValue : attributeValue, operator: operator,logicalCondition:logicalCondition)
        def alarmAttributeCriteria1 = new AlarmAttributeCriteria(attributeName : attributeName,attributeValue : attributeValue1, operator: operator1,logicalCondition:logicalCondition1)
        def alarmAttributeCriteria2 = new AlarmAttributeCriteria(attributeName : attributeName,attributeValue : attributeValue2, operator: operator2,logicalCondition:logicalCondition2)
        def compositeNodeCriteria = new CompositeNodeCriteria(nodes:nodes, alarmAttributeCriteria: [alarmAttributeCriteria, alarmAttributeCriteria1, alarmAttributeCriteria2])
        def expectedOutputAttributes = new ExpectedOutputAttributes( nodeIdRequired:false, commentHistoryRequired:false)

        when : "to retrieve alarms from DB using the Alarm Query Service Bean"
        AlarmAttributeResponse alarmAttributeResponse = alarmQueryServiceBean.getAlarms(compositeNodeCriteria, expectedOutputAttributes, false)

        then : "assert the alarmPoIdResponse returned from DB"
        alarmAttributeResponse.response == response
        alarmAttributeResponse.getAlarmRecords().size() == numberOfAlarmFound
        if(numberOfAlarmFound != 0){
            assert alarmAttributeResponse.getAlarmRecords().get(0).getAttribute(attributeName) == expectedAttributeValue
        }

        where : "Multiple inputs supplied to CompositeNodeCriteria of same attribute alarmAttributeCriteria"
        attributeName     |     attributeValue   | operator    |  logicalCondition     |           nodes                      |   attributeValue1   |      attributeValue2 | operator1     |  logicalCondition1   | logicalCondition2   | operator2   |  response      | numberOfAlarmFound | expectedAttributeValue
        'probableCause'   |   'probableCause'    | Operator.EQ |LogicalCondition.OR    |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'probableCause1'  | 'probableCause2'     |  Operator.EQ  | LogicalCondition.OR  | LogicalCondition.OR | Operator.EQ |  "Success"     |       7            | 'probableCause'
        'probableCause'   |   'probableCause'    | Operator.NE |LogicalCondition.AND   |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'probableCause1'  | 'probableCause2'     |  Operator.NE  | LogicalCondition.AND | LogicalCondition.AND| Operator.NE |  "No Alarms"   |       0            | 'probableCause'
        'probableCause'   |   'probableCause'    | Operator.EQ |LogicalCondition.OR    |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'probableCause1'  | 'probableCause2'     |  Operator.NE  | LogicalCondition.AND | LogicalCondition.AND| Operator.NE |  "Success"     |       6            | 'probableCause'
        'probableCause'   |   'probableCause'    | Operator.NE |LogicalCondition.AND   |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'probableCause1'  | 'probableCause2'     |  Operator.EQ  | LogicalCondition.OR  | LogicalCondition.AND| Operator.NE |  "Success"     |       1            | 'probableCause1'
        'probableCause'   |   'probableCause'    | Operator.NE |LogicalCondition.OR    |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'probableCause1'  | 'probableCause2'     |  Operator.NE  | LogicalCondition.OR  | LogicalCondition.OR | Operator.NE |  "Success"     |       7            | 'probableCause'
    }

    @Unroll("Retrieving Poids of alarms with enrichedCI from db based on the different values set for attribute in CompositeNodeCriteria and attribute #attributeName set for current iteration ")
    def "Getting PoIds for CompositeNodeCriteria for alarm with enrichedCI"() {

        given :"any CompositeNodeCriteria"
        def alarmAttributeCriteria = new AlarmAttributeCriteria(attributeName : attributeName,attributeValue : attributeValue, operator: operator )
        def compositeNodeCriteria = new CompositeNodeCriteria(nodes:nodes, alarmAttributeCriteria: [alarmAttributeCriteria], sortAttribute:sortAttribute)
        def expectedOutputAttributes = new ExpectedOutputAttributes( nodeIdRequired:false, commentHistoryRequired:false)

        when: " to retrieve po ids of alarms with enrichedCI from DB using the Alarm Query Service Bean"
        AlarmPoIdResponse alarmPoIdResponse = alarmQueryServiceBean.getAlarmPoIds(compositeNodeCriteria, false)

        then: "assert the alarmPoIdResponse returned from DB"
        alarmPoIdResponse.response == response
        alarmPoIdResponse.poIds.size() == numberOfPoIdsFound

        where : "Multiple inputs supplied to CompositeNodeCriteria "
        attributeName           | attributeValue                          | operator       |           nodes                       |   sortAttribute  |  response          | numberOfPoIdsFound
        'ciFirstGroup'          | "81d4fae-7dec-11d0-a765-00a0c91e6bf6"   |  Operator.EQ   |  ["NetworkElement=AQS_ARQUILLIAN001"] |   'insertTime'   | 'Success'          |  2
        'ciSecondGroup'         | "f91a6e32-e523-b217-7C3912ad3012"       |  Operator.EQ   |  ["NetworkElement=AQS_ARQUILLIAN001"] |   'insertTime'   | 'Success'          |  1
        'root'                  | CorrelationType.PRIMARY                 |  Operator.EQ   |  ["NetworkElement=AQS_ARQUILLIAN001"] |   'insertTime'   | 'Success'          |  2
        'root'                  | CorrelationType.SECONDARY               |  Operator.EQ   |  ["NetworkElement=AQS_ARQUILLIAN001"] |   'insertTime'   | 'Success'          |  1
        'root'                  | CorrelationType.NOT_APPLICABLE          |  Operator.EQ   |  ["NetworkElement=AQS_ARQUILLIAN001"] |   'insertTime'   | 'Success'          |  1
        'ciSecondGroup'         | ""                                      |  Operator.EQ   |  ["NetworkElement=AQS_ARQUILLIAN001"] |   'insertTime'   | 'Success'          |  3
        }

     @Unroll("Retrieving alarms enriched with correlation information based on #attributeName=#attributeValue, #attributeName1=#attributeValue1 with different operator #logicalCondition.")
     def "Getting alarms enriched with correlation information for CompositeNodeCriteria for same attribute alarmAttributeCriteria"() {

         given :"same attribute alarmAttributeCriteria"
         def alarmAttributeCriteria = new AlarmAttributeCriteria(attributeName : attributeName,attributeValue : attributeValue, operator: operator,logicalCondition:logicalCondition)
         def alarmAttributeCriteria1 = new AlarmAttributeCriteria(attributeName : attributeName1,attributeValue : attributeValue1, operator: operator1)
         def compositeNodeCriteria = new CompositeNodeCriteria(nodes:nodes, alarmAttributeCriteria: [alarmAttributeCriteria, alarmAttributeCriteria1, alarmAttributeCriteria1])
         def compositeNodeCriterias = [compositeNodeCriteria]
         def expectedOutputAttributes = new ExpectedOutputAttributes( nodeIdRequired:false, commentHistoryRequired:false)
         def List<AlarmSortCriterion> alarmSortCriteria = new ArrayList<>()

         when : "to retrieve alarms from DB using the Alarm Query Service Bean"
         AlarmAttributeResponse alarmAttributeResponse = alarmQueryServiceBean.getAlarms(compositeNodeCriterias, expectedOutputAttributes, alarmSortCriteria, false)

         then:"assert the alarmAttributeResponse returned from DB"
         alarmAttributeResponse.response == response
         alarmAttributeResponse.getAlarmRecords().size() == numberOfAlarmFound
         def alarmRecords = alarmAttributeResponse.getAlarmRecords()
         alarmRecords.each {alarmRecord ->
            assert alarmRecord.getAdditionalAttributeMap() != null
            def map = alarmRecord.getAdditionalAttributeMap()
            if (alarmRecord.getAttribute("root") != null && alarmRecord.getAdditionalAttributeMap().get("targetAdditionalInformation") != null){
                def targetAdditionalInfo = alarmRecord.getAdditionalAttributeMap().get("targetAdditionalInformation")    
                def root = alarmRecord.getAttribute("root")
                def expectedTargetAddInfo = [
                    "SECONDARY": "CI={\"S\":[\"81d4fae-7dec-11d0-a765-00a0c91e6bf6\",\"f91a6e32-e523-b217-7C3912ad3012\"],\"C\":[{\"I\":\"201f0123-88ca-23a2-7451-8B5872ac457b\",\"n\":\"vRC\"}]};",
                    "PRIMARY":"DN2=ManagedElement\\=1,Equipment\\=1;CI={\"P\":\"81d4fae-7dec-11d0-a765-00a0c91e6bf6\",\"C\":[{\"I\":\"201f0123-88ca-23a2-7451-8B5872ac457b\",\"n\":\"vRC\"}]};", 
                    "NOT_APPLICABLE": "additionalInfo"]
                def expectedTargetAddInfoMaxlength = "CI={\"P\":\"13ae0000-0036-11e7-8e92-98c5db77d231\",\"C\":[{\"I\":\"13aec000-d3cf-11e7-8e92-98c5db77d231\",\"n\":\"RadioNode\"}]};PLMN ID-eNB ID 1=26280-210757;PLMN ID-eNB ID 2=26280-211205;PLMN ID-eNB ID 3=26280-210701;PLMN ID-eNB ID 4=26280-210702;PLMN ID-eNB;]]\";"
                assert ((expectedTargetAddInfo.getAt(root.toString()) == targetAdditionalInfo) || (expectedTargetAddInfoMaxlength == targetAdditionalInfo))
                println "FOUND root = $root"
            }
        }

         where : "Multiple inputs supplied to CompositeNodeCriteria of same attribute alarmAttributeCriteria"
         attributeName     |     attributeValue                         | operator    |  logicalCondition     | attributeName1      |   attributeValue1                       |     operator1     | sortCriteria                                  |  nodes                                  |  response      | numberOfAlarmFound 
         'ciSecondGroup'   |   ""                                       | Operator.EQ |LogicalCondition.OR    | "ciSecondGroup"     |   "f91a6e32-e523-b217-7C3912ad3012"     |    Operator.EQ    | []                                            |  ["NetworkElement=AQS_ARQUILLIAN001"]   |  "Success"     |       4
         'ciFirstGroup'    |   "81d4fae-7dec-11d0-a765-00a0c91e6bf6"    | Operator.EQ |LogicalCondition.OR    | "ciFirstGroup"      |   "f91a6e32-e523-b217-7C3912ad3012"     |    Operator.EQ    | []                                            |  ["NetworkElement=AQS_ARQUILLIAN001"]   |  "Success"     |       2  
         'ciFirstGroup'    |   "81d4fae-7dec-11d0-a765-00a0c91e6bf6"    | Operator.EQ |LogicalCondition.AND   | "ciSecondGroup"     |   "f91a6e32-e523-b217-7C3912ad3012"     |    Operator.EQ    | []                                            |  ["NetworkElement=AQS_ARQUILLIAN001"]   |  "Success"     |       1
         'root'            |   CorrelationType.PRIMARY                  | Operator.EQ |LogicalCondition.OR    | "root"              |   CorrelationType.SECONDARY             |    Operator.EQ    | []                                            |  ["NetworkElement=AQS_ARQUILLIAN001"]   |  "Success"     |       3
         'ciSecondGroup'   |   ""                                       | Operator.EQ |LogicalCondition.AND   | "root"              |   CorrelationType.NOT_APPLICABLE        |    Operator.EQ    | []                                            |  ["NetworkElement=AQS_ARQUILLIAN001"]   |  "Success"     |       1
         'ciSecondGroup'   |   ""                                       | Operator.EQ |LogicalCondition.OR    | "ciSecondGroup"     |   "f91a6e32-e523-b217-7C3912ad3012"     |    Operator.EQ    | [[attribute:"presentSeverity", mode: "desc"]] |  ["NetworkElement=AQS_ARQUILLIAN001"]   |  "Success"     |       4
        }

        @Unroll("Retrieving alarms if root is null or empty won't enrich with correlation information")
        def "Getting alarms won't enrich with correlation information if root is invalid"() {

            given :"same attribute alarmAttributeCriteria"
            def alarmAttributeCriteria = new AlarmAttributeCriteria(attributeName : attributeName,attributeValue : attributeValue, operator: operator, logicalCondition : LogicalCondition.OR )
            def compositeNodeCriteria = new CompositeNodeCriteria(nodes:nodes, alarmAttributeCriteria: [alarmAttributeCriteria])  
            def compositeNodeCriterias = [compositeNodeCriteria]
            def expectedOutputAttributes = new ExpectedOutputAttributes(outputAttributes: outputAttributes, nodeIdRequired:false, commentHistoryRequired:false)

            when : "retrieve alarms from DB using the Alarm Query Service Bean"
            AlarmAttributeResponse alarmAttributeResponse = alarmQueryServiceBean.getAlarms(compositeNodeCriterias, expectedOutputAttributes, [], false)

            then : "assert the alarmAttributeResponse returned from DB"
            alarmAttributeResponse.response == response
            def alarmRecords = alarmAttributeResponse.getAlarmRecords()
            def counter =0
            alarmRecords.each {alarmRecord ->
                    assert alarmRecord.getAdditionalAttributeMap() != null
                    assert alarmRecord.getAdditionalAttributeMap().get("targetAdditionalInformation") != null
                    assert alarmAttributeResponse.getAlarmRecords().size() == numberOfAlarmFound
                    assert alarmRecord.getAdditionalAttributeMap().get("targetAdditionalInformation") == expectedTargetAdditionalInfo[counter]
                    counter++
                }

            where : "Multiple inputs supplied to CompositeNodeCriteria of same attribute alarmAttributeCriteria"
            attributeName  | attributeValue                      | operator    |  nodes                                |  outputAttributes                                                 ||response   | numberOfAlarmFound | expectedTargetAdditionalInfo
            'ciSecondGroup'|'f91a6e32-e523-b217-7C3912ad3013'    | Operator.EQ |  ["NetworkElement=AQS_ARQUILLIAN001"] |  ['root','ciFirstGroup','ciSecondGroup', 'additionalInformation'] ||"Success"  |       1            |["CI={\"C\": [{\"I\": \"201f0123-88ca-23a2-7451-8B5872ac457b\",\"n\": \"vRC\"}]}"]
            'ciFirstGroup' |'81d4fae-7dec-11d0-a765-00a0c91e6bf7'| Operator.EQ |  ["NetworkElement=AQS_ARQUILLIAN001"] |  ['root','ciFirstGroup','ciSecondGroup', 'additionalInformation'] ||"Success"  |       1            |["addInfo"]
            'root'         |CorrelationType.PRIMARY              | Operator.EQ |  ["NetworkElement=AQS_ARQUILLIAN001"] |  ['root','ciFirstGroup','ciSecondGroup', 'additionalInformation'] ||"Success"  |       2            |["DN2=ManagedElement\\=1,Equipment\\=1;CI={\"P\":\"81d4fae-7dec-11d0-a765-00a0c91e6bf6\",\"C\":[{\"I\":\"201f0123-88ca-23a2-7451-8B5872ac457b\",\"n\":\"vRC\"}]};","CI={\"P\":\"13ae0000-0036-11e7-8e92-98c5db77d231\",\"C\":[{\"I\":\"13aec000-d3cf-11e7-8e92-98c5db77d231\",\"n\":\"RadioNode\"}]};PLMN ID-eNB ID 1=26280-210757;PLMN ID-eNB ID 2=26280-211205;PLMN ID-eNB ID 3=26280-210701;PLMN ID-eNB ID 4=26280-210702;PLMN ID-eNB;]]\";"]
          }

        @Unroll("Retrieving alarms from db sorting them by attribute = #sortAttribute using single query and a list of CompositeNodeCriteria")
        def "Getting sorted alarms for List<CompositeNodeCriteria> and single query"() {

           given :"any CompositeNodeCriteria"
           def alarmSortCriterion1 = new AlarmSortCriterion(sortAttribute:sortAttribute, sortOrder:(sortOrder == 'asc')?SortingOrder.ASCENDING:SortingOrder.DESCENDING, sortSequence:sortSequence1)
           def alarmAttributeCriteria = new AlarmAttributeCriteria(attributeName : attributeName,attributeValue : attributeValue, operator: operator )
           def compositeNodeCriteria = new CompositeNodeCriteria(nodes:nodes, alarmAttributeCriteria: [alarmAttributeCriteria])
           def compositeNodeCriterias = [compositeNodeCriteria]
           def expectedOutputAttributes = new ExpectedOutputAttributes(outputAttributes: outputAttributes, nodeIdRequired:false, commentHistoryRequired:false)           

           when : "to retrieve alarms from DB using the Alarm Query Service Bean"
           AlarmAttributeResponse alarmAttributeResponse = alarmQueryServiceBean.getAlarms(compositeNodeCriterias, expectedOutputAttributes, [alarmSortCriterion1], false)

           then : "assert the alarmPoIdResponse returned from DB"
           alarmAttributeResponse.response == response
           alarmAttributeResponse.getAlarmRecords().size() == alarmNum
           if (alarmNum != 0) {
               assert alarmAttributeResponse.getAlarmRecords().get(0).getAttribute(attributeName) == attributeValue
               alarmAttributeResponse.getAlarmRecords().eachWithIndex {
                   alarm, idx -> assert alarm.getEventPoIdAsLong() == orderedPoidList[idx]
               }
           }

           where : "Multiple inputs supplied to CompositeNodeCriteria"
           attributeName     |     attributeValue                        | operator    |           nodes                      |   sortAttribute          |  sortOrder  |        sortSequence1          |         outputAttributes                      ||                 response                                                 | alarmNum | orderedPoidList
           'probableCause'   |   'probableCause'                         | Operator.EQ |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxTest'              | 'asc'       | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'specificProblem']        ||  "Error while retrieving alarms from DB {}null"                          |       0  | []
           'specificProblem' |   'specificProblem'                       | Operator.EQ |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxdisbledattribute4' | 'asc'       | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'specificProblem']        ||  "Error while retrieving alarms from DB {}Invalid sort/search attribute" |       0  | []
           'probableCause'   |   'probableCause'                         | Operator.EQ |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'asc'       | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'specificProblem']        ||                 "Success"                                                |       6  | [2,3,4,5,6,7]
           'ciFirstGroup'    |   '81d4fae-7dec-11d0-a765-00a0c91e6bf6'   | Operator.EQ |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['ciFirstGroup']                            ||                 "Success"                                                |       2  | [2,3]
           'ciFirstGroup'    |   '81d4fae-7dec-11d0-a765-00a0c91e6bf6'   | Operator.EQ |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'presentSeverity'      | 'asc'       | SortSequence.FIRST_LEVEL_SORT |   ['root', 'presentSeverity', 'ciFirstGroup'] ||                 "Success"                                                |       2  | [2,3]
           'ciFirstGroup'    |   '81d4fae-7dec-11d0-a765-00a0c91e6bf6'   | Operator.EQ |  ["NetworkElement=AQS_ARQUILLIAN001"]|   'presentSeverity'      | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['root', 'presentSeverity', 'ciFirstGroup'] ||                 "Success"                                                |       2  | [3,2]
       }

      @Unroll("Retrieving alarms from db sorting them by attribute = #sortAttribute using multiple query and a list of CompositeNodeCriteria")
      def "Getting sorted alarms for List<CompositeNodeCriteria> and multiple queries"() {

         given :"any CompositeNodeCriteria"
         def alarmSortCriterion1 = new AlarmSortCriterion(sortAttribute:sortAttribute, sortOrder:(sortOrder == 'asc')?SortingOrder.ASCENDING:SortingOrder.DESCENDING, sortSequence:sortSequence1)
         def alarmAttributeCriteria1 = new AlarmAttributeCriteria(attributeName : attributeName1,attributeValue : group1, operator : operator , logicalCondition : logicalCondition)
         def alarmAttributeCriteria2 = new AlarmAttributeCriteria(attributeName : attributeName2,attributeValue : group2, operator : operator )
         def compositeNodeCriteria1 = new CompositeNodeCriteria(nodes:nodes, alarmAttributeCriteria: [alarmAttributeCriteria1])
         def compositeNodeCriteria2 = new CompositeNodeCriteria(nodes:nodes, alarmAttributeCriteria: [alarmAttributeCriteria2])
         def compositeNodeCriterias = [compositeNodeCriteria1, compositeNodeCriteria2]
         def expectedOutputAttributes = new ExpectedOutputAttributes(outputAttributes: outputAttributes, nodeIdRequired:false, commentHistoryRequired:false)

         when : "to retrieve alarms from DB using the Alarm Query Service Bean"
         AlarmAttributeResponse alarmAttributeResponse = alarmQueryServiceBean.getAlarms(compositeNodeCriterias, expectedOutputAttributes, [alarmSortCriterion1], false)

         then : "assert the alarmPoIdResponse returned from DB"
         alarmAttributeResponse.response == response
         alarmAttributeResponse.getAlarmRecords().size() == alarmNum
         if (alarmNum != 0) {
             assert alarmAttributeResponse.getAlarmRecords().get(0).getAttribute(attributeName1) == group1
             alarmAttributeResponse.getAlarmRecords().eachWithIndex {
                 alarm, idx -> assert alarm.getEventPoIdAsLong() == orderedPoidList[idx]
             }
         }

         where : "Multiple inputs supplied to CompositeNodeCriteria"
         attributeName1    | attributeName2 |     group1                            | group2                             | operator    |  logicalCondition    |          nodes                      |   sortAttribute          |  sortOrder  |        sortSequence1          |         outputAttributes                              ||      response    | alarmNum | orderedPoidList
         'ciFirstGroup'    |'ciSecondGroup' |  '81d4fae-7dec-11d0-a765-00a0c91e6bf6'|'f91a6e32-e523-b217-7C3912ad3012'   | Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "Success"   |       2  | [2,3]
     }

     @Unroll("Retrieving alarms from db using a list of CompositeNodeCriteria but without nodes selection")
     def "Getting alarms for List<CompositeNodeCriteria> and multiple queries without nodes selection"() {

        given :"any CompositeNodeCriteria"
        def alarmAttributeCriteriaFirstAttributeFirstValue = new AlarmAttributeCriteria(attributeName : attributeName1,attributeValue : value1, operator : operator, logicalCondition : logicalCondition)
        def alarmAttributeCriteriaFirstAttributeSecondValue = new AlarmAttributeCriteria(attributeName : attributeName1,attributeValue : value2, operator : operator )
        def alarmAttributeCriteriaSecondAttributeFirstValue = new AlarmAttributeCriteria(attributeName : attributeName2,attributeValue : value1, operator : operator, logicalCondition : logicalCondition)
        def alarmAttributeCriteriaSecondAttributeSecondValue = new AlarmAttributeCriteria(attributeName : attributeName2,attributeValue : value2, operator : operator )

        def compositeNodeCriteria1 = new CompositeNodeCriteria(alarmAttributeCriteria: [alarmAttributeCriteriaFirstAttributeFirstValue, alarmAttributeCriteriaFirstAttributeSecondValue])
        def compositeNodeCriteria2 = new CompositeNodeCriteria(alarmAttributeCriteria: [alarmAttributeCriteriaSecondAttributeFirstValue, alarmAttributeCriteriaSecondAttributeSecondValue])
        def compositeNodeCriterias = [compositeNodeCriteria1, compositeNodeCriteria2]

        def expectedOutputAttributes = new ExpectedOutputAttributes(outputAttributes: outputAttributes, nodeIdRequired:false, commentHistoryRequired:false)

        when : "to retrieve alarms from DB using the Alarm Query Service Bean"
        AlarmAttributeResponse alarmAttributeResponse = alarmQueryServiceBean.getAlarms(compositeNodeCriterias, expectedOutputAttributes, [], false)

        then : "assert the alarmPoIdResponse returned from DB"
        alarmAttributeResponse.response == response
        alarmAttributeResponse.getAlarmRecords().size() == alarmNum
        if (alarmNum != 0) {
            assert alarmAttributeResponse.getAlarmRecords().get(0).getAttribute(attributeName1) == value1
            alarmAttributeResponse.getAlarmRecords().eachWithIndex {
                alarm, idx -> assert alarm.getEventPoIdAsLong() == expectedPoidList[idx]
            }
        }

        where : "Multiple inputs supplied to CompositeNodeCriteria"
        attributeName1    | attributeName2 |     value1                            | value2                             | operator    |  logicalCondition    |        outputAttributes                            | response    | alarmNum | expectedPoidList
        'ciFirstGroup'    |'ciSecondGroup' |  '81d4fae-7dec-11d0-a765-00a0c91e6bf6'|'f91a6e32-e523-b217-7C3912ad3012'   | Operator.EQ |  LogicalCondition.OR | ['probableCause', 'ciFirstGroup', 'ciSecondGroup'] | "Success"   |       2  | [2,3]
    }

	@Unroll("Retrieving alarms based on poId and verifying Additional Information ")
	def "Getting alarms for PoIds and verifying Additional Information "() {

		given :"any Additional Information"
		def expectedOutputAttributes = new ExpectedOutputAttributes(nodeIdRequired:nodeIdRequired, commentHistoryRequired:commentHistoryRequired)
		def alarmPoIdCriteria = new AlarmPoIdCriteria(poIds : poids)

		when : "to retrieve alarms from DB using the Additional Information"
		AlarmAttributeResponse alarmAttributeResponse = alarmQueryServiceBean.getAlarms(alarmPoIdCriteria, expectedOutputAttributes, authorizationRequired, tbacValidationRequired)

		then : "assert the alarmPoIdResponse returned from DB"
		def alarmRecords=alarmAttributeResponse.getAlarmRecords()
		def additionalInformation
		alarmRecords.each {alarmRecord ->
			additionalInformation=alarmRecord.getAdditionalAttributeMap().toString()
		}
		assert additionalInformation.contains(expectedAdditionalInformation)
		where : "Multiple inputs supplied to alarmPoIdCriteria"
		nodeIdRequired   |commentHistoryRequired  |poids  |authorizationRequired  | tbacValidationRequired | expectedAdditionalInformation
		false            |  false                 |[4L]   |        true           |      true              |  'fmxToken:UPDATE_ALARM##NSX_Shortlived_and_Frequent:Short-lived alarm filtering and Frequent alarm creation:202##6, targetAdditionalInformation:additionalInfo'
		true             |  false                 |[3L]   |        true           |      true              |  '[targetAdditionalInformation:CI={"S":["81d4fae-7dec-11d0-a765-00a0c91e6bf6","f91a6e32-e523-b217-7C3912ad3012"],"C":[{"I":"201f0123-88ca-23a2-7451-8B5872ac457b","n":"vRC"}]};]'
		false            |  true                  |[5L]   |        true           |      false             |  '[targetAdditionalInformation:CI={"C": [{"I": "201f0123-88ca-23a2-7451-8B5872ac457b","n": "vRC"}]}]'
		true             |  false                 |[6L]   |        false          |      true              |  '[targetAdditionalInformation:addInfo]'       
                true             |  false                 |[7L]   |        false          |      true              |  '[targetAdditionalInformation:CI={\"P\":\"13ae0000-0036-11e7-8e92-98c5db77d231\",\"C\":[{\"I\":\"13aec000-d3cf-11e7-8e92-98c5db77d231\",\"n\":\"RadioNode\"}]};PLMN ID-eNB ID 1=26280-210757;PLMN ID-eNB ID 2=26280-211205;PLMN ID-eNB ID 3=26280-210701;PLMN ID-eNB ID 4=26280-210702;PLMN ID-eNB;]]";]'
	}
     @Unroll("Check negative case retrieving alarms from db when correlation groups = [#group1,#group2] using multiple query and a list of CompositeNodeCriteria")
     def "Getting sorted alarms for List<CompositeNodeCriteria> and multiple queries with wrong parameter"() {

        given :"any CompositeNodeCriteria"
        def alarmSortCriterion1 = new AlarmSortCriterion(sortAttribute:sortAttribute, sortOrder:(sortOrder == 'asc')?SortingOrder.ASCENDING:SortingOrder.DESCENDING, sortSequence:sortSequence1)
        def alarmAttributeCriteriaFirstGroupFirstValue = new AlarmAttributeCriteria(attributeName : attributeName1,attributeValue : group1, operator : operator , logicalCondition : logicalCondition)
        def alarmAttributeCriteriaFirstGroupSecondValue = new AlarmAttributeCriteria(attributeName : attributeName1,attributeValue : group2, operator : operator , logicalCondition : logicalCondition)   
        def alarmAttributeCriteriaSecondGroupFirstValue = new AlarmAttributeCriteria(attributeName : attributeName2,attributeValue : group1, operator : operator, logicalCondition : logicalCondition)
        def alarmAttributeCriteriaSecondGroupSecondValue = new AlarmAttributeCriteria(attributeName : attributeName2,attributeValue : group2, operator : operator, logicalCondition : logicalCondition )
        def compositeNodeCriteria1 = new CompositeNodeCriteria(nodes:nodes, alarmAttributeCriteria: [alarmAttributeCriteriaFirstGroupFirstValue, alarmAttributeCriteriaFirstGroupSecondValue])
        def compositeNodeCriteria2 = new CompositeNodeCriteria(nodes:nodes, alarmAttributeCriteria: [alarmAttributeCriteriaSecondGroupFirstValue, alarmAttributeCriteriaSecondGroupSecondValue])
        def compositeNodeCriterias = [compositeNodeCriteria1, compositeNodeCriteria2]
        def expectedOutputAttributes = new ExpectedOutputAttributes(outputAttributes: outputAttributes, nodeIdRequired:false, commentHistoryRequired:false)

        when : "to retrieve alarms from DB using the Alarm Query Service Bean"
        AlarmAttributeResponse alarmAttributeResponse = alarmQueryServiceBean.getAlarms(compositeNodeCriterias, expectedOutputAttributes, [], false)

        then : "assert the alarmPoIdResponse returned from DB"
        alarmAttributeResponse.response == response
        alarmAttributeResponse.getAlarmRecords().size() == alarmNum
          if (alarmNum != 0) {
            alarmAttributeResponse.getAlarmRecords().eachWithIndex {
                alarm, idx ->
                     assert alarm.getEventPoIdAsLong() == orderedPoidList[idx]
                }
        }

        // APIs check attributeName and/or attributeValue null. All others values for attributeName and/or attributeValue are not considered wrong and sent to DPS which returns No alarms
        where : "Multiple inputs supplied to CompositeNodeCriteria"
        attributeName1            | attributeName2        |     group1                            | group2                           | operator    |  logicalCondition    |          nodes                      |   sortAttribute          |  sortOrder  |        sortSequence1          |         outputAttributes                              ||      response                                                                                    | alarmNum | orderedPoidList
        'ciFirstGroup'            |'ciSecondGroup'        |  'G1'                                 | 'G2'                             | Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "No Alarms"                                                                                 |       0  | []
        'ciFirstGroup'            |'ciSecondGroup'        |  'G1'                                 | 'f91a6e32-e523-b217-7C3912ad3012'| Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "Success"                                                                                   |       1  | [3]
        'ciFirstGroup'            |'ciSecondGroup'        |  '81d4fae-7dec-11d0-a765-00a0c91e6bf6'| 'G2'                             | Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "Success"                                                                                   |       2  | [2,3]
        'ciFirstGroup'            |'ciSecondGroup'        |  'G1'                                 | ''                               | Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "Success"                                                                                   |       4  | [2,4,5,7]
        'AttributeNotExistent'    |'AttributeNotExistent' |  '81d4fae-7dec-11d0-a765-00a0c91e6bf6'| 'G2'                             | Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "No Alarms"                                                                                 |       0  | []
        'AttributeNotExistent'    |'ciSecondGroup'        |  '81d4fae-7dec-11d0-a765-00a0c91e6bf6'| 'G2'                             | Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "No Alarms"                                                                                 |       0  | []    
        'AttributeNotExistent'    |'ciSecondGroup'        |  'G1'                                 | 'f91a6e32-e523-b217-7C3912ad3012'| Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "Success"                                                                                   |       1  | [3]
         null                     | null                  |  '81d4fae-7dec-11d0-a765-00a0c91e6bf6'| 'f91a6e32-e523-b217-7C3912ad3012'| Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "Error while retrieving alarms from DB {}Attribute Name / Attribute Value given is invalid" |       0  | []
        'ciFirstGroup'            | null                  |  '81d4fae-7dec-11d0-a765-00a0c91e6bf6'| 'f91a6e32-e523-b217-7C3912ad3012'| Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "Error while retrieving alarms from DB {}Attribute Name / Attribute Value given is invalid" |       0  | []
         null                     |'ciSecondGroup'        |   null                                | 'f91a6e32-e523-b217-7C3912ad3012'| Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "Error while retrieving alarms from DB {}Attribute Name / Attribute Value given is invalid" |       0  | []
        'AttributeNotExistent'    |'ciSecondGroup'        |  'G1'                                 | 'f91a6e32-e523-b217-7C3912ad3012'| Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "Success"                                                                                   |       1  | [3]
        'ciFirstGroup'            |'ciSecondGroup'        |   null                                | ''                               | Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "Error while retrieving alarms from DB {}Attribute Name / Attribute Value given is invalid" |       0  | []
        'AttributeNotExistent'    |'ciSecondGroup'        |   null                                | 'f91a6e32-e523-b217-7C3912ad3012'| Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "Error while retrieving alarms from DB {}Attribute Name / Attribute Value given is invalid" |       0  | [] //[3]
        'AttributeNotExistent'    |'ciSecondGroup'        |   ''                                  | 'f91a6e32-e523-b217-7C3912ad3012'| Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "Success"                                                                                   |       4  | [2,3,4,7]
        'ciFirstGroup'            |'AttributeNotExistent' |  '81d4fae-7dec-11d0-a765-00a0c91e6bf6'| ''                               | Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "Success"                                                                                   |       4  | [2,3,4,5]
        'ciFirstGroup'            |'AttributeNotExistent' |  '81d4fae-7dec-11d0-a765-00a0c91e6bf6'| null                             | Operator.EQ |  LogicalCondition.OR | ["NetworkElement=AQS_ARQUILLIAN001"]|   'fmxenabledattribute1' | 'desc'      | SortSequence.FIRST_LEVEL_SORT |   ['probableCause', 'ciFirstGroup', 'ciSecondGroup']  ||      "Error while retrieving alarms from DB {}Attribute Name / Attribute Value given is invalid" |       0  | []
    }
}