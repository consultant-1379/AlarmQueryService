/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.fm.alarmqueryservice.impl

import static com.ericsson.oss.services.fm.common.constants.AddInfoConstants.CI_GROUP_1
import static com.ericsson.oss.services.fm.common.constants.AddInfoConstants.CI_GROUP_2
import static com.ericsson.oss.services.fm.common.constants.AddInfoConstants.ROOT

import com.ericsson.oss.itpf.datalayer.dps.DataBucket
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator.PseudoSeverities
import com.ericsson.oss.services.fm.common.addinfo.CorrelationType
import com.ericsson.oss.services.fm.models.FmxAddtionalAttributesRecord

/**
 * This class is responsible to fill data into DB and also into Cache.
 *
 */
class TestSetupInitializer{

    static def correlationInfos = [
        [
            'additionalInfo' : "targetAdditionalInformation:DN2=ManagedElement\\=1,Equipment\\=1;CI={\"C\": [{\"I\": \"201f0123-88ca-23a2-7451-8B5872ac457b\",\"n\": \"vRC\"}]};",
            'root' : CorrelationType.PRIMARY,
            'ciFirstGroup' : '81d4fae-7dec-11d0-a765-00a0c91e6bf6',
            'ciSecondGroup' : '',
            'presentSeverity' : 'MINOR'
        ],
        [
            'additionalInfo' : "targetAdditionalInformation:CI={\"C\": [{\"I\": \"201f0123-88ca-23a2-7451-8B5872ac457b\",\"n\": \"vRC\"}]};",
            'root' : CorrelationType.SECONDARY,
            'ciFirstGroup' : '81d4fae-7dec-11d0-a765-00a0c91e6bf6',
            'ciSecondGroup' : 'f91a6e32-e523-b217-7C3912ad3012',
            'presentSeverity' : 'MAJOR'
        ],
        [
            'additionalInfo' : "sourceType:#targetAdditionalInformation:additionalInfo#fmxToken:UPDATE_ALARM¡¿§¡¿§NSX_Shortlived_and_Frequent:Short-lived alarm filtering and Frequent alarm creation:202¡¿§¡¿§6",
            'root' : CorrelationType.NOT_APPLICABLE,
            'ciFirstGroup' : '',
            'ciSecondGroup' : '',
            'presentSeverity' : 'WARNING'
        ],
        [
            'additionalInfo' : "targetAdditionalInformation:CI={\"C\": [{\"I\": \"201f0123-88ca-23a2-7451-8B5872ac457b\",\"n\": \"vRC\"}]}",
            'root' : "",
            'ciFirstGroup' : '',
            'ciSecondGroup' : 'f91a6e32-e523-b217-7C3912ad3013',
            'presentSeverity' : 'WARNING'
        ],
        [
            'additionalInfo' : "targetAdditionalInformation:addInfo",
            'root' : null,
            'ciFirstGroup' : '81d4fae-7dec-11d0-a765-00a0c91e6bf7',
            'ciSecondGroup' : 'f91a6e32-e523-b217-7C3912ad3014',
            'presentSeverity' : 'MINOR'
        ],
        [
            'additionalInfo' : "targetAdditionalInformation:CI={\"C\":[{\"I\":\"13aec000-d3cf-11e7-8e92-98c5db77d231\",\"n\": \"RadioNode\"}],\"P\":\"13ae0000-0036-11e7-8e92-98c5db77d231\"};PLMN ID-eNB ID 1=26280-210757;PLMN ID-eNB ID 2=26280-211205;PLMN ID-eNB ID 3=26280-210701;PLMN ID-eNB ID 4=26280-210702;PLMN ID-eNB;]]\"",
            'root' : CorrelationType.PRIMARY,
            'ciFirstGroup' : '13ae0000-0036-11e7-8e92-98c5db77d231',
            'ciSecondGroup' : '',
            'presentSeverity' : 'MAJOR'
        ]
    ]

    void persistsVisibleAlarms(runtimeDps) {
        correlationInfos.eachWithIndex {correlationInfo, index ->
            final Map<String, Object> OpenAlarmMap = new HashMap<String, Object>()
            createVisibleAlarms(OpenAlarmMap, index, correlationInfo.additionalInfo, correlationInfo.root, correlationInfo.ciFirstGroup, correlationInfo.ciSecondGroup, correlationInfo.presentSeverity)
            def po = runtimeDps.addPersistenceObject().namespace("FM").type("OpenAlarm").addAttributes(OpenAlarmMap).build()
            def poId = po.getPoId()
            def visbility = po.getAttribute("visibility")
            def eventTime = po.getAttribute("eventTime")
            def additionalInformation = po.getAttribute("additionalInformation")
            def fdn = po.getAttribute("fdn")
            println("Alarm Created with PoId $poId and fdn $fdn and eventTime $eventTime and additionalInformation $additionalInformation")
        }

        final Map<String, Object> OpenAlarmMap = new HashMap<String, Object>()
        OpenAlarmMap.put("fdn", "NetworkElement=AQS_ARQUILLIAN001")
        OpenAlarmMap.put("eventTime", new Date(1532614958671-3*60000))
        OpenAlarmMap.put("presentSeverity", "MINOR")
        OpenAlarmMap.put("visibility", true)
        OpenAlarmMap.put("probableCause", "probableCause1")
        OpenAlarmMap.put("specificProblem", "specificProblem")
        def po = runtimeDps.addPersistenceObject().namespace("FM").type("OpenAlarm").addAttributes(OpenAlarmMap).build()
        def poId = po.getPoId()
        def visbility = po.getAttribute("visibility")
        def eventTime = po.getAttribute("eventTime")
        def fdn = po.getAttribute("fdn")
        println("Alarm Created with PoId $poId and fdn $fdn and eventTime $eventTime")
    }

    void createVisibleAlarms(OpenAlarmMap, offset, additionalInformation, root, ciFirstGroup, ciSecondGroup, presentSeverity) {
        OpenAlarmMap.put("fdn", "NetworkElement=AQS_ARQUILLIAN001")
        OpenAlarmMap.put("objectOfReference", "NetworkElement=AQS_ARQUILLIAN001")
        OpenAlarmMap.put("eventTime", new Date(1532614958671 - offset*60000))
        OpenAlarmMap.put("presentSeverity", presentSeverity)
        OpenAlarmMap.put("visibility", true)
        OpenAlarmMap.put("probableCause", "probableCause")
        OpenAlarmMap.put("specificProblem", "specificProblem")
        OpenAlarmMap.put("additionalInformation", additionalInformation)
        OpenAlarmMap.put(ROOT, root)
        OpenAlarmMap.put(CI_GROUP_1, ciFirstGroup)
        OpenAlarmMap.put(CI_GROUP_2, ciSecondGroup)
        OpenAlarmMap.put("pseudoPresentSeverity", PseudoSeverities.PSEUDO_SEVERITIES_MAP.get(OpenAlarmMap.get('presentSeverity')?"UNDEFINED": OpenAlarmMap.get('presentSeverity')))
    }

    void persistsHiddenAlarms(runtimeDps) {

        final Map<String, Object> OpenAlarmMap = new HashMap<String, Object>()
        OpenAlarmMap.put("fdn", "NetworkElement=AQS_ARQUILLIAN001")
        OpenAlarmMap.put("eventTime", new Date())
        OpenAlarmMap.put("presentSeverity", "MAJOR")
        OpenAlarmMap.put("visibility", false)
        OpenAlarmMap.put("probableCause", "probableCause")
        OpenAlarmMap.put("specificProblem", "specificProblem")
        OpenAlarmMap.put("additionalInformation", "targetAdditionalInformation:CI ={\"C\": [{\"I\": \"201f0123-88ca-23a2-7451-8B5872ac457b\",\"n\": \"vRC\"}]}")
        OpenAlarmMap.put(ROOT, CorrelationType.PRIMARY)
        OpenAlarmMap.put(CI_GROUP_1, "81d4fae-7dec-11d0-a765-00a0c91e6bf6")
        OpenAlarmMap.put(CI_GROUP_2, "")
        def po = runtimeDps.addPersistenceObject().namespace("FM").type("OpenAlarm").addAttributes(OpenAlarmMap).build()
        def poId = po.getPoId()
        def visbility = po.getAttribute("visibility")
        def fdn = po.getAttribute("fdn")
        println("Alarm Created with visibility id $visbility ,PoId $poId and fdn $fdn")
    }

    void addDataToCache(fmxAdditionalAttributeCache) {

        Map<String, Set<String>>  userAttributes = ['user1':[
                'fmxenabledattribute1',
                'fmxenabledattribute2'
            ], 'user2':[
                'fmxenabledattribute3',
                'fmxenabledattribute2'
            ]]
        FmxAddtionalAttributesRecord fmxAddtionalAttributesRecord = new FmxAddtionalAttributesRecord(additionalAttibuteList : [
            'fmxenabledattribute1',
            'fmxenabledattribute2',
            'fmxenabledattribute3'
        ], userAttributes:userAttributes)
        FmxAddtionalAttributesRecord fmxDisableAddtionalAttributesRecord = new FmxAddtionalAttributesRecord(additionalAttibuteList : [
            'fmxdisbledattribute4',
            'fmxdisbledattribute5',
            'fmxdisbledattribute6'
        ], userAttributes:new HashMap())
        fmxAdditionalAttributeCache.put(true, fmxAddtionalAttributesRecord)
        fmxAdditionalAttributeCache.put(false, fmxDisableAddtionalAttributesRecord)
        println("Data added to fmxAdditionalAttributeCache")
    }

    void persistsNodes( runtimeDps ) {
        DataBucket liveBucket = runtimeDps.build().getLiveBucket()
        for(int i=0;i<3;i++){
            liveBucket = runtimeDps.build().getLiveBucket()
            final ManagedObject networkElement = liveBucket.getMibRootBuilder().namespace("OSS_NE_DEF").version("2.0.0").name("AQS_GROOVY00"+i).type("NetworkElement").create()
            String fdn = networkElement.getFdn()
            println("Node Created is $fdn")
        }
    }
}
