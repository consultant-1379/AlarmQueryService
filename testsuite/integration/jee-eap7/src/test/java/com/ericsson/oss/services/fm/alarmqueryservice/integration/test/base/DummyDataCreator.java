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

package com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ACK_OPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ACK_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ADDITIONAL_INFORMATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_NUMBER;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_STATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.BACKUP_OBJECT_INSTANCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.BACKUP_STATUS;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CEASE_OPERATOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.NOT_APPLICABLE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.OBJECT_OF_REFERENCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PREVIOUS_SEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PRIMARY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PROBABLE_CAUSE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PROBLEM_DETAIL;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PROBLEM_TEXT;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PROPOSED_REPAIR_ACTION;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.RECORD_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ROOT;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.SECONDARY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.SPECIFIC_PROBLEM;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.TREND_INDICATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants.ACTIVE_ACKNOWLEDGED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants.ACTIVE_UNACKNOWLEDGED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants.ALARM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants.CLEARED_UNACKNOWLEDGED;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants.CRITICAL;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants.INDETERMINATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants.MAJOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants.MINOR;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants.WARNING;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.FM;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.OPEN_ALARM;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.CPP_MED;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.CPP_PLATFORM_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.DEFAULT_VERSION;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.ERBS_NODE_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.FM_ALARM_SUPERVISION;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.FM_FUNCION_PREFIX;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.MEDIATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.ME_CONTEXT;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.ME_CONTEXT_PREFIX;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.NETWORK_ELEMENT_PREFIX;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.OSS_NE_DEF;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.OSS_NE_DEF_VERSION;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.OSS_NE_FM_DEF;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.OSS_TOP;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.OSS_TOP_VERSION;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.TEST_NE_PREFIX;
import static com.ericsson.oss.services.fm.common.constants.AddInfoConstants.CI_GROUP_1;
import static com.ericsson.oss.services.fm.common.constants.AddInfoConstants.CI_GROUP_2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.BucketProperties;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.models.ned.fm.function.FmSyncStatus100;

@Singleton
@Startup
public class DummyDataCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyDataCreator.class);

    private static List<Long> alarmPoIds = new ArrayList<Long>();

    private static List<Long> meContextPoIds = new ArrayList<Long>();

    public static List<Long> getAlarmPoIds() {
        return alarmPoIds;
    }

    public static List<Long> getMeContextPoIds() {
        return meContextPoIds;
    }

    @EServiceRef
    private DataPersistenceService dataPersistenceService;

    Query<TypeRestrictionBuilder> typeQuery;
    QueryExecutor queryExecutor;

    @PostConstruct
    public void createNE() {
        LOGGER.info("Creating The Network Element  and Alarms Under it for Testing ");
        for (Integer i = 25; i < 27; i++) {
            final String objectOfReference = "MeContext=AQS_ARQUILLIAN00" + i.toString() + ",ManagedElement=1,ENodeBFunction=1";
            createTestAlarms(objectOfReference, i);
        }
        createTestObjects(TEST_NE_PREFIX);
    }

    @PreDestroy
    public void removeall() {
        LOGGER.info(" Clearing the Network Element  and Alarms Under it for Testing ");
        for (int i = 25; i < 27; i++) {
            try {
                cleanDps(TEST_NE_PREFIX + i);
            } catch (final Exception e) {
            }
        }
    }

    public void cleanDps(final String nodeName) throws Exception {
        LOGGER.debug("--> Cleaning DPS...");

        final DataBucket dataBucket = dataPersistenceService.getDataBucket("Live", BucketProperties.SUPPRESS_MEDIATION,
                BucketProperties.SUPPRESS_CONSTRAINTS);

        final String meContextFdn = ME_CONTEXT_PREFIX + nodeName;
        final String neElementFdn = NETWORK_ELEMENT_PREFIX + nodeName;
        final String cppConnMo = NETWORK_ELEMENT_PREFIX + nodeName + ",CppConnectivityInformation=1";
        final String fmSupMo = NETWORK_ELEMENT_PREFIX + nodeName + FM_ALARM_SUPERVISION;
        final String fmFunMo = NETWORK_ELEMENT_PREFIX + nodeName + FM_FUNCION_PREFIX;
        deleteRootMo(dataBucket, fmFunMo);
        deleteRootMo(dataBucket, fmSupMo);
        deleteRootMo(dataBucket, cppConnMo);
        deleteRootMo(dataBucket, meContextFdn);
        deleteRootMo(dataBucket, neElementFdn);
        LOGGER.info("Deleted root MOs MeContext, NetworkElement and child MO/POs for node {}", nodeName);
        deleteOpenAlarmsFromDb();
        LOGGER.info("--> Cleaned DPS.");
    }

    private void deleteOpenAlarmsFromDb() {
        final DataBucket liveBucket = dataPersistenceService.getLiveBucket();
        final Query<TypeRestrictionBuilder> typeQuery = dataPersistenceService.getQueryBuilder().createTypeQuery(FM, OPEN_ALARM);
        final Iterator<PersistenceObject> iterator = liveBucket.getQueryExecutor().execute(typeQuery);
        while (iterator.hasNext()) {
            final PersistenceObject objectToDelete = iterator.next();
            LOGGER.info("Deleting open alarm with attributes: {}", objectToDelete.getAllAttributes());
            liveBucket.deletePo(objectToDelete);
        }
    }

    public void createTestAlarms(final String objectOfReference, final long i) {
        createAlarm(objectOfReference, i, ACTIVE_UNACKNOWLEDGED, CRITICAL, "SpecificProblem1", "eventType1", "ProbableCause1", 111, new Date(), true, PRIMARY, "81d4fae-7dec-11d0-a765-00a0c91e6bf6", "");
        createAlarm(objectOfReference, i, ACTIVE_ACKNOWLEDGED, MAJOR, "SpecificProblem2", "eventType2", "ProbableCause2", 222,
                new Date(new Date().getTime()
                        + (24 * 3600 * 1000)), true, SECONDARY, "81d4fae-7dec-11d0-a765-00a0c91e6bf6", "");
        createAlarm(objectOfReference, i, CLEARED_UNACKNOWLEDGED, MINOR, "SpecificProblem3", "eventType3", "ProbableCause3", 333, new Date(
                new Date().getTime()
                        - (24 * 3600 * 1000)), true, NOT_APPLICABLE, "", "");
        createAlarm(objectOfReference, i, CLEARED_UNACKNOWLEDGED, CRITICAL, "SpecificProblem3", "eventType11", "ProbableCause15", 123,
                new Date(new Date().getTime() - (24 * 3600 * 1000)), true, NOT_APPLICABLE, "", "");
        createAlarm(objectOfReference, i, CLEARED_UNACKNOWLEDGED, WARNING, "SpecificProblem3", "eventType10", "ProbableCause80", 7812,
                new Date(new Date().getTime() - (24 * 3600 * 1000)), true, NOT_APPLICABLE, "", "");
        createAlarm(objectOfReference, i, CLEARED_UNACKNOWLEDGED, INDETERMINATE, "SpecificProblem4", "eventType8", "ProbableCause1245",
                1234124, new Date(
                        new Date().getTime() - (24 * 3600 * 1000)), true, NOT_APPLICABLE, "", "");
        createAlarm(objectOfReference, i, CLEARED_UNACKNOWLEDGED, MAJOR, "SpecificProblem4", "eventType19", "ProbableCause3724", 4564,
                new Date(new Date().getTime() - (24 * 3600 * 1000)), true, NOT_APPLICABLE, "", "");
        createAlarm(objectOfReference, i, CLEARED_UNACKNOWLEDGED, CRITICAL, "SpecificProblem4", "eventType25", "ProbableCause8273", 23452,
                new Date(
                        new Date().getTime() - (24 * 3600 * 1000)), true, NOT_APPLICABLE, "", "");
        createAlarm(objectOfReference, i, CLEARED_UNACKNOWLEDGED, WARNING, "SpecificProblem4", "eventType03", "ProbableCause123", 678,
                new Date(new Date().getTime() - (24 * 3600 * 1000)), true, NOT_APPLICABLE, "", "");
        // Create an alarm with visibility false and it should not be considered for any operations
        createAlarm(objectOfReference, i, CLEARED_UNACKNOWLEDGED, WARNING, "testSp", "testEt", "testPc", 99999,
                new Date(new Date().getTime() - (24 * 3600 * 1000)), false, NOT_APPLICABLE, "", "");

    }

    public void createTestObjects(final String nodeName) {
        for (int i = 25; i < 27; i++) {
            createTestObjects(nodeName + i, "1.2.3.4", ERBS_NODE_TYPE);
        }
    }

    public void deleteRootMo(final DataBucket db, final String fdn) {
        LOGGER.info(" Removing All MO with FDN : {}", fdn);
        try {
            final ManagedObject mo = db.findMoByFdn(fdn);
            if (mo != null) {
                LOGGER.debug("Deleting MO...");
                db.deletePo(mo);
            } else {
                LOGGER.debug("Unable to find MO for FDN : {}", fdn);
            }
        } catch (final Exception e) {
            LOGGER.error("Execption thrown while deleting MO : {}", e);
        }
    }

    public void createAlarm(final String objectOfReference, final long nodeNumber, final String alarmState, final String presentSeverity,
                            final String specificProblem, final String eventType, final String probableCause, final long alarmNumber,
                            final Date eventTime, final boolean visibility, final String root, final String ciGroup1, final String ciGroup2) {
        final DataBucket liveBucket = dataPersistenceService.getLiveBucket();

        final Map<String, Object> OpenAlarmMap = new HashMap<String, Object>();

        OpenAlarmMap.put(OBJECT_OF_REFERENCE, objectOfReference);
        final String fdn = NETWORK_ELEMENT_PREFIX + TEST_NE_PREFIX + nodeNumber;
        OpenAlarmMap.put(FDN, fdn);
        OpenAlarmMap.put(EVENT_TIME, eventTime);
        OpenAlarmMap.put(PRESENT_SEVERITY, presentSeverity);
        OpenAlarmMap.put(PROBABLE_CAUSE, probableCause);
        OpenAlarmMap.put(SPECIFIC_PROBLEM, specificProblem);
        OpenAlarmMap.put(ALARM_NUMBER, alarmNumber);
        OpenAlarmMap.put(EVENT_TYPE, eventType);
        OpenAlarmMap.put(BACKUP_OBJECT_INSTANCE, BACKUP_OBJECT_INSTANCE);
        OpenAlarmMap.put(RECORD_TYPE, ALARM);
        OpenAlarmMap.put(BACKUP_STATUS, true);
        OpenAlarmMap.put(VISIBILITY, visibility);
        OpenAlarmMap.put(TREND_INDICATION, "LESS_SEVERE");
        OpenAlarmMap.put(PREVIOUS_SEVERITY, CRITICAL);
        OpenAlarmMap.put(PROPOSED_REPAIR_ACTION, PROPOSED_REPAIR_ACTION);
        OpenAlarmMap.put(ALARM_ID, alarmNumber);
        OpenAlarmMap.put(ALARM_STATE, alarmState);
        OpenAlarmMap.put(CEASE_OPERATOR, " ");
        OpenAlarmMap.put(ACK_TIME, new Date());
        OpenAlarmMap.put(ACK_OPERATOR, ACK_OPERATOR);
        OpenAlarmMap.put(ADDITIONAL_INFORMATION, "targetAdditionalInformation:CI={\"C\":[{\"I\":\"201f0123-88ca-23a2-7451-8B5872ac457b\",\"n\":\"vRC\"}]};");
        OpenAlarmMap.put(ROOT, root);
        OpenAlarmMap.put(CI_GROUP_1, ciGroup1);
        OpenAlarmMap.put(CI_GROUP_2, ciGroup2);
        OpenAlarmMap.put(PROBLEM_DETAIL, PROBLEM_DETAIL);
        OpenAlarmMap.put(PROBLEM_TEXT, PROBLEM_TEXT);

        final PersistenceObject po = liveBucket.getPersistenceObjectBuilder().namespace(FM).type(OPEN_ALARM).version("1.0.1")
                .addAttributes(OpenAlarmMap).create();
        alarmPoIds.add(po.getPoId());
        LOGGER.info("Alarm Created with po id :{} and objectOfReference {} fdn:{} and alarmNumber:{}  eventTime {} ", po.getPoId(),
                po.getAttribute(OBJECT_OF_REFERENCE), po.getAttribute(FDN), po.getAttribute(ALARM_NUMBER), po.getAttribute(EVENT_TIME));
    }

    public void createTestObjects(final String nodeName, final String netsimIpAddress, final String neType) {
        LOGGER.debug("Creating test objects...");

        try {
            final DataBucket dataBucket = dataPersistenceService.getDataBucket("Live", BucketProperties.SUPPRESS_MEDIATION,
                    BucketProperties.SUPPRESS_CONSTRAINTS);

            final PersistenceObject entityAddressInfo = createEai(dataBucket);

            // NetworkElement
            final ManagedObject networkElement = createNetworkElementMo(dataBucket, nodeName, neType);
            LOGGER.info("FDN of the created network element {}", networkElement.getFdn());
            networkElement.setEntityAddressInfo(entityAddressInfo);
            // MeContext
            final ManagedObject meContextMO = createMeContextMO(dataBucket, nodeName);
            LOGGER.info("FDN of the created me context {}", meContextMO.getFdn());
            meContextMO.addAssociation("networkElementRef", networkElement);
            LOGGER.info("Created Association to network element in MeContext MO");

            final ManagedObject cppConnectivityInformation = createCppConnectivityInformationMO(dataBucket, networkElement, netsimIpAddress);

            entityAddressInfo.addAssociation("ciRef", cppConnectivityInformation);
            LOGGER.debug("Created ciAssociation in entityAddressInfo.");
            Thread.sleep(100);
            createFmSupervisionChild(dataBucket, networkElement);
            createFmFunctionMoChild(dataBucket, networkElement);

        } catch (final Exception e) {
            LOGGER.error("Exception thrown when creating test objects in db!", e);
        }

        LOGGER.info("--> Created test objects.");
    }

    public ManagedObject findMo(final String fdn) {
        final DataBucket liveBucket = dataPersistenceService.getLiveBucket();
        return liveBucket.findMoByFdn(fdn);
    }

    public Object getAttribute(final String fdn, final String attribute) {
        final DataBucket liveBucket = dataPersistenceService.getLiveBucket();
        final ManagedObject mo = liveBucket.findMoByFdn(fdn);

        return mo.getAttribute(attribute);
    }

    public void updateMoAttribute(final String fdn, final String attribute, final Object attributeValue) {
        final ManagedObject mo = findMo(fdn);
        mo.setAttribute(attribute, attributeValue);

    }

    private PersistenceObject createEai(final DataBucket dataBucket) {
        final List<String> targetNamespaceKeys = Arrays.asList(CPP_PLATFORM_TYPE);
        LOGGER.debug("Creating EAI PO ...");
        final PersistenceObject entityAddressInfo = dataBucket.getPersistenceObjectBuilder().namespace(MEDIATION).type("EntityAddressingInformation")
                .version(DEFAULT_VERSION).addAttribute("targetNamespaceKeys", targetNamespaceKeys).create();
        LOGGER.debug("Created EAI PO ...");
        return entityAddressInfo;
    }

    private ManagedObject createMeContextMO(final DataBucket dataBucket, final String nodeName) {
        LOGGER.debug("Creating MeContext MO (root MO)...");

        final ManagedObject meContextMO = dataBucket.getMibRootBuilder().namespace(OSS_TOP).type(ME_CONTEXT).version(OSS_TOP_VERSION).name(nodeName)
                .addAttribute("MeContextId", nodeName).create();
        LOGGER.info("Created MeContext MO (root MO).");
        meContextPoIds.add(meContextMO.getPoId());
        return meContextMO;
    }

    private ManagedObject createNetworkElementMo(final DataBucket dataBucket, final String nodeName, final String neType) {
        LOGGER.debug("Creating NetworkElement MO...");

        final Map<String, Object> moAttributes = new HashMap<String, Object>();
        moAttributes.put("networkElementId", nodeName);

        moAttributes.put("neType", neType);
        moAttributes.put("platformType", CPP_PLATFORM_TYPE);
        moAttributes.put("ossPrefix", ME_CONTEXT_PREFIX + nodeName);
        moAttributes.put("ossModelIdentity", "1294-439-662");

        final ManagedObject networkElement = dataBucket.getMibRootBuilder().type("NetworkElement").namespace(OSS_NE_DEF).version(OSS_NE_DEF_VERSION)
                .name(nodeName).addAttributes(moAttributes).create();
        LOGGER.info("Created NetworkElement MO.");

        final Map<String, Object> targetAttributes = new HashMap<String, Object>();
        targetAttributes.put("category", "NODE");

        targetAttributes.put("type", neType);
        targetAttributes.put("name", nodeName);
        targetAttributes.put("modelIdentity", "1294-439-662");

        final PersistenceObject target = dataBucket.getPersistenceObjectBuilder().namespace("DPS").type("Target").version(DEFAULT_VERSION)
                .addAttributes(targetAttributes).create();

        networkElement.setTarget(target);

        return networkElement;
    }

    private ManagedObject createCppConnectivityInformationMO(final DataBucket dataBucket, final ManagedObject parentMO,
                                                             final String netsimIpAddress) {
        LOGGER.debug("Creating CppConnectivityInformation MO...");

        final Map<String, Object> moAttributes = new HashMap<String, Object>();
        moAttributes.put("ipAddress", netsimIpAddress);
        moAttributes.put("CppConnectivityInformationId", "1");
        moAttributes.put("port", 80);

        final ManagedObject cppConnectivityInformation = dataBucket.getMibRootBuilder().parent(parentMO).name("1").namespace(CPP_MED)
                .version(DEFAULT_VERSION).type("CppConnectivityInformation").addAttributes(moAttributes).create();
        LOGGER.info("Created CppConnectivityInformation MO.");

        return cppConnectivityInformation;
    }

    private ManagedObject createFmSupervisionChild(final DataBucket liveBucket, final ManagedObject parentMO) {
        LOGGER.info("createSupervisionChild called: {}", parentMO.toString());
        final Map<String, Object> aFMSupervisionMO = new HashMap<String, Object>();
        aFMSupervisionMO.put("active", true);
        aFMSupervisionMO.put("automaticSynchronization", true);
        aFMSupervisionMO.put("FmAlarmSupervisionId", "1");
        aFMSupervisionMO.put("heartbeatinterval", 300);
        aFMSupervisionMO.put("heartbeatTimeout", 101);
        final ManagedObject fmSupervision = liveBucket.getMibRootBuilder().parent(parentMO).type("FmAlarmSupervision").namespace(OSS_NE_FM_DEF)
                .name("1").addAttributes(aFMSupervisionMO).version("1.1.0").create();
        LOGGER.info("Created the fmsupervision MO.");
        return fmSupervision;
    }

    private ManagedObject createFmFunctionMoChild(final DataBucket liveBucket, final ManagedObject networkElement) {
        LOGGER.info("createFmFunctionMoChild : {} ", networkElement.toString());
        final Map<String, Object> aFMFunctionMO = new HashMap<String, Object>();

        aFMFunctionMO.put("currentServiceState", FmSyncStatus100.IN_SERVICE.toString());
        final ManagedObject fmFunctionMO = liveBucket.getMibRootBuilder().parent(networkElement).type("FmFunction").namespace(OSS_NE_FM_DEF)
                .name("1").addAttributes(aFMFunctionMO).version(DEFAULT_VERSION).create();
        LOGGER.info("Created the fmFunction MO.");
        return fmFunctionMO;
    }

}
