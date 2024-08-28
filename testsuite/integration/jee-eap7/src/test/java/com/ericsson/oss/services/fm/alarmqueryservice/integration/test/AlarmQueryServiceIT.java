package com.ericsson.oss.services.fm.alarmqueryservice.integration.test;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ADDITIONAL_INFORMATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_NUMBER;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ALARM_STATE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CI_GROUP_1;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.CI_GROUP_2;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_PO_ID;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TIME;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EVENT_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PRESENT_SEVERITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PRIMARY;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ROOT;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.SPECIFIC_PROBLEM;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.VISIBILITY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.FilterConstants.CRITICAL;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.SUCCESS;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.AQS_TEST_WAR;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.TEST_FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.TEST_FDN1;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.TEST_NE_PREFIX;
import static com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.TestConstants.TEST_OOR;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.context.classic.ContextServiceBean;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.SecurityPrivilegeServiceMock;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmCountResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmPoIdResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.Operator;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.SortingOrder;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmPoIdCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion.SortSequence;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.OORCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.OORExpression;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants;
import com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.AlarmQueryServiceTestBase;
import com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base.DummyDataCreator;
import com.ericsson.oss.services.fm.alarmqueryservice.integration.test.util.ServiceProxyProvider;

/**
 * In tests 10 alarms created for each node(9 alarms with visibility true and one with false) and tests will be performed on same.
 */
@RunWith(Arquillian.class)
public class AlarmQueryServiceIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmQueryServiceIT.class);

    @Inject
    private ServiceProxyProvider serviceProxyProvider;

    @Inject
    private DummyDataCreator dummyDataCreator;

/*    @Deployment(name = AQS_EAR, testable = false, managed = true, order = 5)
    public static EnterpriseArchive createAQSDeployment() {
        LOGGER.info("******Creating Query Service deployment and deploying it to server******");
        return Deployments.createEnterpriseArchiveDeployment(Artifact.ALARM_QUERY_SERVICE);
    }*/

    @Deployment(name = AQS_TEST_WAR, testable = true, managed = true, order = 6)
    public static WebArchive createAQSTestWarDeployment() {
        LOGGER.info("******Creating Query Service deployment and deploying it to server******");
        return AlarmQueryServiceTestBase.createTestArchive();
    }

    @Before
    public void setup() {
        final String HTTP_HEADER_USERNAME_KEY = "X-Tor-UserID";
        final ContextServiceBean contextService = new ContextServiceBean();
        contextService.setContextValue(HTTP_HEADER_USERNAME_KEY, SecurityPrivilegeServiceMock.FM_USER);
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(1)
    public void setupTargetGroups() {
        serviceProxyProvider.getTargetGroupRegistry().addNodeToTargetGroup(TEST_NE_PREFIX + 25, SecurityPrivilegeServiceMock.SEC_TARGET_GROUP_1);
        serviceProxyProvider.getTargetGroupRegistry().addNodeToTargetGroup(TEST_NE_PREFIX + 26, SecurityPrivilegeServiceMock.SEC_TARGET_GROUP_1);
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(2)
    public void testGetAlarms_CompositeNodeCriteriaWithSpecificProblemAndNode_OneAlarmReturned() {

        final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();
        final List<String> nodes = new ArrayList<>();
        nodes.add(TEST_FDN);
        compositeNodeCriteria.setNodes(nodes);

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem1");
        alarmAttributeCriteria.setOperator(Operator.EQ);
        final List<AlarmAttributeCriteria> attributes = new ArrayList<>();
        attributes.add(alarmAttributeCriteria);

        final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();

        final List<String> outputAttributes = new ArrayList<>();
        outputAttributes.add(ALARM_NUMBER);
        outputAttributes.add(SPECIFIC_PROBLEM);
        outputAttributes.add(PRESENT_SEVERITY);
        expectedOutputAttributes.setOutputAttributes(outputAttributes);

        compositeNodeCriteria.setAlarmAttributeCriteria(attributes);
        compositeNodeCriteria.setSortAttribute(SPECIFIC_PROBLEM);
        compositeNodeCriteria.setSortDirection(SortingOrder.DESCENDING);
        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeNodeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                " AlarmQueryResponse returned in testGetAlarms_CompositeNodeCriteriaWithSpecificProblemAndNode_OneAlarmReturned compositeNodeCriteria :: {} AlarmQueryResponse are :: {}",
                compositeNodeCriteria, alarmQueryResponse);
        Assert.assertEquals(1, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(3)
    public void testGetAlarms_CompositeNodeCriteriaWithpresentSeverityAndNode_ThreeAlarmsReturned() {

        final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();
        final List<String> nodes = new ArrayList<>();
        nodes.add(TEST_FDN);
        compositeNodeCriteria.setNodes(nodes);

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        alarmAttributeCriteria.setAttributeName(PRESENT_SEVERITY);
        alarmAttributeCriteria.setAttributeValue(EnumConstants.CRITICAL);
        alarmAttributeCriteria.setOperator(Operator.EQ);
        final List<AlarmAttributeCriteria> attributes = new ArrayList<>();
        attributes.add(alarmAttributeCriteria);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(false);

        compositeNodeCriteria.setAlarmAttributeCriteria(attributes);
        compositeNodeCriteria.setSortAttribute(SPECIFIC_PROBLEM);
        compositeNodeCriteria.setSortDirection(SortingOrder.DESCENDING);
        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeNodeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                " AlarmQueryResponse returned in testGetAlarms_CompositeNodeCriteriaWithpresentSeverityAndNode_OneAlarmReturned compositeNodeCriteria :: {} AlarmQueryResponse are :: {}",
                compositeNodeCriteria, alarmQueryResponse);

        Assert.assertEquals(3, alarmQueryResponse.getAlarmRecords().size());

    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(4)
    public void testGetAlarms_CompositeNodeCriteriaOnlyNodes_NineAlarmsReturned() {

        final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();
        final List<String> nodes = new ArrayList<>();
        nodes.add(TEST_FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(false);
        compositeNodeCriteria.setNodes(nodes);
        compositeNodeCriteria.setSortAttribute(SPECIFIC_PROBLEM);
        compositeNodeCriteria.setSortDirection(SortingOrder.DESCENDING);
        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeNodeCriteria, expectedOutputAttributes, true);
        LOGGER.info(
                "AlarmQueryResponse returned in testGetAlarms_CompositeNodeCriteriaOnlyNodes_ThreeAlarmsReturned compositeNodeCriteria :: {} AlarmQueryResponse are :: {}",
                compositeNodeCriteria, alarmQueryResponse);

        Assert.assertEquals(9, alarmQueryResponse.getAlarmRecords().size());

        // Add visibility in attrs
        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        alarmAttributeCriteria.setAttributeName(VISIBILITY);
        alarmAttributeCriteria.setAttributeValue(false);
        alarmAttributeCriteria.setOperator(Operator.EQ);

        compositeNodeCriteria.setAlarmAttributeCriteria(Arrays.asList(alarmAttributeCriteria));
        final AlarmAttributeResponse alarmQueryResponse1 = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeNodeCriteria, expectedOutputAttributes, true);
        LOGGER.info(
                "AlarmQueryResponse1 returned in testGetAlarms_CompositeNodeCriteriaOnlyNodes_ThreeAlarmsReturned compositeNodeCriteria :: {} AlarmQueryResponse are :: {}",
                compositeNodeCriteria, alarmQueryResponse1);
        // Even if visibility=false is added to criteria, still no alarms should be returned
        Assert.assertEquals(0, alarmQueryResponse1.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(5)
    public void testGetAlarms_CompositeNodeCriteriaOnlyNodes_With_VisibilityFalse_NoAlarmsReturned() {
        final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();
        final List<String> nodes = new ArrayList<>();
        nodes.add(TEST_FDN1);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(false);
        compositeNodeCriteria.setNodes(nodes);
        compositeNodeCriteria.setSortAttribute(SPECIFIC_PROBLEM);
        compositeNodeCriteria.setSortDirection(SortingOrder.DESCENDING);

        // Add visibility in alarm attributes
        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        alarmAttributeCriteria.setAttributeName(VISIBILITY);
        alarmAttributeCriteria.setAttributeValue(false);
        alarmAttributeCriteria.setOperator(Operator.EQ);
        compositeNodeCriteria.setAlarmAttributeCriteria(Arrays.asList(alarmAttributeCriteria));
        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeNodeCriteria, expectedOutputAttributes, true);
        LOGGER.info("AlarmQueryResponse returned in testGetAlarms_CompositeNodeCriteriaOnlyNodes_With_VisibilityFalse_NoAlarmsReturned "
                + "compositeNodeCriteria :: {} AlarmQueryResponse are :: {}", compositeNodeCriteria, alarmQueryResponse);
        // Even if visibility=false is added to criteria, still no alarms should be returned
        Assert.assertEquals(0, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(6)
    public void testGetAlarms_CompositeNodeCriteriaWithNoData_ZeroAlarmsReturned() {
        final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();
        final List<String> nodes = new ArrayList<>();
        nodes.add(TEST_FDN);
        compositeNodeCriteria.setNodes(nodes);

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem5");
        alarmAttributeCriteria.setOperator(Operator.EQ);
        final List<AlarmAttributeCriteria> attributes = new ArrayList<>();
        attributes.add(alarmAttributeCriteria);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(false);

        compositeNodeCriteria.setAlarmAttributeCriteria(attributes);
        compositeNodeCriteria.setSortAttribute(SPECIFIC_PROBLEM);
        compositeNodeCriteria.setSortDirection(SortingOrder.DESCENDING);
        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeNodeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                " AlarmQueryResponse returned in testGetAlarms_CompositeNodeCriteriaWithNoData_ZeroAlarmsReturned compositeNodeCriteria :: {} AlarmQueryResponse are :: {}",
                compositeNodeCriteria, alarmQueryResponse);

        Assert.assertEquals(0, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(7)
    public void testGetAlarms_PoIdNodeCriteria_18AlarmsReturned() {
        final AlarmPoIdCriteria poIdCriteria = new AlarmPoIdCriteria();

        poIdCriteria.setPoIds(DummyDataCreator.getAlarmPoIds());

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(false);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(poIdCriteria, expectedOutputAttributes, true);
        LOGGER.info(" AlarmQueryResponse returned in testGetAlarms_PoIdNodeCriteria_ThreeAlarmsReturned "
                + "poIdCriteria :: {} AlarmQueryResponse are :: {}", poIdCriteria, alarmQueryResponse);

        Assert.assertEquals(18, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(8)
    public void testGetAlarms_PoIdCriteria_18AlarmsReturnedAndAssertNodeId() {
        final AlarmPoIdCriteria poIdCriteria = new AlarmPoIdCriteria();
        poIdCriteria.setPoIds(DummyDataCreator.getAlarmPoIds());

        final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
        expectedOutputAttributes.setNodeIdRequired(true);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(poIdCriteria, expectedOutputAttributes, true);
        LOGGER.info(" AlarmQueryResponse returned in testGetAlarms_PoIdCriteria_18AlarmsReturnedAndAssertNodeId "
                + "poIdCriteria :: {} AlarmQueryResponse are :: {}", poIdCriteria, alarmQueryResponse);

        Assert.assertEquals(18, alarmQueryResponse.getAlarmRecords().size());
        final List<Long> meContextPoIds = DummyDataCreator.getMeContextPoIds();
        LOGGER.info("The MeContext MO's poIds are {}", meContextPoIds);
        for (final AlarmRecord alarmRecord : alarmQueryResponse.getAlarmRecords()) {
            final String nodeId = alarmRecord.getNodeId();
            LOGGER.info("The node in alarm is {}", nodeId);
            meContextPoIds.contains(Long.valueOf(nodeId));
        }
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(9)
    public void testGetAlarms_compositeEventTimeCriteriaWithLE_TwoAlarmsReturned() {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem1");
        alarmAttributeCriteria.setOperator(Operator.EQ);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setFromTime(new Date());
        compositeEventTimeCriteria.setToTime(new Date(new Date().getTime() + 24 * 3600 * 1000));

        compositeEventTimeCriteria.setOperator(Operator.LE);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(true);
        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info("AlarmQueryResponse returned in testGetAlarms_compositeEventTimeCriteriaWithLE_OneAlarmReturned "
                + "compositeEventTimeCriteria :: {} AlarmQueryResponse are :: {}", compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(2, alarmQueryResponse.getAlarmRecords().size());

    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(10)
    public void testGetAlarms_compositeEventTimeCriteriaWithGE_TwoAlarmsReturned() {

        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem1");
        alarmAttributeCriteria.setOperator(Operator.EQ);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setFromTime(new Date(new Date().getTime() - 38 * 3600 * 1000));
        compositeEventTimeCriteria.setToTime(new Date(new Date().getTime() - 48 * 3600 * 1000));

        compositeEventTimeCriteria.setOperator(Operator.GE);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(true);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info("AlarmQueryResponse returned in testGetAlarms_compositeEventTimeCriteriaWithGE_OneAlarmReturned "
                + "compositeEventTimeCriteria :: {} AlarmQueryResponse are :: {}", compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(2, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(11)
    public void testGetAlarms_compositeEventTimeCriteriaWithBothEventTimes_TwoAlarmsReturned() {

        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem1");
        alarmAttributeCriteria.setOperator(Operator.EQ);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setFromTime(new Date(new Date().getTime() - 38 * 3600 * 1000));
        compositeEventTimeCriteria.setToTime(new Date(new Date().getTime() + 48 * 3600 * 1000));

        compositeEventTimeCriteria.setOperator(Operator.BETWEEN);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(true);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info("AlarmQueryResponse returned in testGetAlarms_compositeEventTimeCriteriaWithBothEventTimes_OneAlarmReturned "
                + "compositeEventTimeCriteria :: {} AlarmQueryResponse are :: {}", compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(2, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(12)
    public void testGetAlarms_compositeEventTimeCriteriaWithGT_TwoAlarmsReturned() {

        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem1");
        alarmAttributeCriteria.setOperator(Operator.EQ);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setFromTime(new Date(new Date().getTime() - 38 * 3600 * 1000));
        compositeEventTimeCriteria.setToTime(new Date(new Date().getTime() + 48 * 3600 * 1000));

        compositeEventTimeCriteria.setOperator(Operator.GT);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(true);
        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info("AlarmQueryResponse returned in testGetAlarms_compositeEventTimeCriteriaWithGT_OneAlarmReturned "
                + "compositeEventTimeCriteria :: {} AlarmQueryResponse are :: {}", compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(2, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(13)
    public void testGetAlarms_compositeEventTimeCriteriaWithLT_TwoAlarmsReturned() {

        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem1");
        alarmAttributeCriteria.setOperator(Operator.EQ);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setFromTime(new Date(new Date().getTime() - 38 * 3600 * 1000));
        compositeEventTimeCriteria.setToTime(new Date(new Date().getTime() + 48 * 3600 * 1000));

        compositeEventTimeCriteria.setOperator(Operator.LT);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(true);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info("AlarmQueryResponse returned in testGetAlarms_compositeEventTimeCriteriaWithLT_OneAlarmReturned "
                + "compositeEventTimeCriteria :: {} AlarmQueryResponse are :: {}", compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(2, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(14)
    public void testGetAlarms_compositeEventTimeCriteria_TwoAlarmsReturned() {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem1");
        alarmAttributeCriteria.setOperator(Operator.EQ);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(true);
        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info("AlarmQueryResponse returned in testGetAlarms_compositeEventTimeCriteria_OneAlarmReturned "
                + "compositeEventTimeCriteria :: {} AlarmQueryResponse are :: {}", compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(2, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(15)
    public void testGetAlarmsCountBySeverity_CompositeNodeCriteria_SeverityBasedAlarmsCount() {
        final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();
        final List<String> nodes = new ArrayList<>();
        nodes.add(TEST_FDN);
        compositeNodeCriteria.setNodes(nodes);
        final Map<String, Long> severityMap = serviceProxyProvider.getAlarmQueryService().getAlarmCountBySeverity(compositeNodeCriteria, true);

        final long count = severityMap.get(CRITICAL);
        LOGGER.info(
                " CRITICAL count returned in testGetAlarmsCountBySeverity_CompositeNodeCriteria_SeverityBasedAlarmsCount compositeEventTimeCriteria :: {} count is :: {}",
                compositeNodeCriteria, count);
        Assert.assertEquals(3L, count);

    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(16)
    public void testGetAlarms_Null_EighteenAlarmReturned() {
        final AlarmPoIdResponse alarmPoIdResponse = serviceProxyProvider.getAlarmQueryService().getAllAlarmPoIds();

        LOGGER.info("PoIds returned in testGetAlarms_Null_OneAlarmReturned PoIds are :: {}", alarmPoIdResponse.getPoIds());

        Assert.assertEquals(18, alarmPoIdResponse.getPoIds().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(17)
    public void testGetPoIds_OOR_NineAlarmsReturned() {
        final OORCriteria oORCriteria = getOORCriteria(TEST_OOR, Operator.EQ);

        final List<Long> poIds = serviceProxyProvider.getAlarmQueryService().getAlarmPoIds(oORCriteria).getPoIds();

        LOGGER.info("PoIds returned in testGetPoIds_OOR_NineAlarmsReturned oORCriteria :: {} PoIds are :: {}", oORCriteria, poIds);
        Assert.assertEquals(9, poIds.size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(18)
    public void testGetAlarms_CompositeEventTimeCriteriawithoutEventTime_TwoAlarmsReturned() {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem1");
        alarmAttributeCriteria.setOperator(Operator.CONTAINS);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(true);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                "AlarmQueryResponse returned in testGetAlarms_CompositeEventTimeCriteriawithoutEventTime_OneAlarmReturned compositeNodeCriteria :: {} alarmQueryResponse are :: {}",
                compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(2, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(19)
    public void testGetAlarms_CompositeEventTimeCriteriaCONTAINS_TwoAlarmsReturned() {

        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem1");
        alarmAttributeCriteria.setOperator(Operator.CONTAINS);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(true);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                "AlarmQueryResponse returned in testGetAlarms_CompositeEventTimeCriteriaCONTAINS_OneAlarmReturned compositeNodeCriteria :: {} alarmQueryResponse are :: {}",
                compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(2, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(20)
    public void testGetAlarms_CompositeEventTimeCriteriaENDSWITH_TwoAlarmsReturned() {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem1");
        alarmAttributeCriteria.setOperator(Operator.ENDS_WITH);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(true);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                "AlarmQueryResponse returned in testGetAlarms_CompositeEventTimeCriteriaENDSWITH_OneAlarmReturned compositeNodeCriteria :: {} alarmQueryResponse are :: {}",
                compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(2, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(21)
    public void testGetAlarms_CompositeEventTimeCriteriaSTARTSWITH_TwoAlarmsReturned() {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem1");
        alarmAttributeCriteria.setOperator(Operator.STARTS_WITH);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(true);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                "AlarmQueryResponse returned in testGetAlarms_CompositeEventTimeCriteriaSTARTSWITH_OneAlarmReturned compositeNodeCriteria :: {} alarmQueryResponse are :: {}",
                compositeEventTimeCriteria, alarmQueryResponse);
        Assert.assertEquals(2, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(22)
    public void testGetAlarms_compositeEventTimeCriteriaWithSTARTSWITH_ZeroAlarmReturned() {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("ecificProblem");
        alarmAttributeCriteria.setOperator(Operator.STARTS_WITH);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(false);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                "AlarmQueryResponse returned in testGetAlarms_compositeEventTimeCriteriaWithSTARTSWITH_ZeroAlarmReturned compositeNodeCriteria :: {} alarmQueryResponse are :: {}",
                compositeEventTimeCriteria, alarmQueryResponse);
        Assert.assertEquals(0, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(23)
    public void testGetAlarms_compositeEventTimeCriteria_ZeroAlarmReturned() {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem");
        alarmAttributeCriteria.setOperator(Operator.ENDS_WITH);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
        final List<String> outputAttributes = new ArrayList<>();
        outputAttributes.add(ALARM_NUMBER);
        outputAttributes.add(SPECIFIC_PROBLEM);
        outputAttributes.add(ALARM_STATE);
        expectedOutputAttributes.setOutputAttributes(outputAttributes);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                "AlarmQueryResponse returned in testGetAlarms_compositeEventTimeCriteria_ZeroAlarmReturned compositeNodeCriteria :: {} alarmQueryResponse are :: {}",
                compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(0, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(24)
    public void testGetAlarmsPoIds_CompositeNodeCriteriaNodesAndOtherAttributes_PoIds() {

        final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        alarmAttributeCriteria.setAttributeName(ALARM_NUMBER);
        alarmAttributeCriteria.setAttributeValue(111);
        alarmAttributeCriteria.setOperator(Operator.EQ);
        final List<AlarmAttributeCriteria> attributes = new ArrayList<>();
        attributes.add(alarmAttributeCriteria);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(false);

        compositeNodeCriteria.setAlarmAttributeCriteria(attributes);

        final List<Long> poIds = serviceProxyProvider.getAlarmQueryService().getAlarmPoIds(compositeNodeCriteria, true).getPoIds();

        LOGGER.info(
                "poids returned in testGetAlarmsPoIds_CompositeNodeCriteriaNodesAndOtherAttributes_PoIds compositeNodeCriteria :: {} PoIds are :: {}",
                compositeNodeCriteria, poIds);
        Assert.assertEquals(2, poIds.size());

    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(25)
    public void testGetAlarmPoIds_CompositeNodeCriteriaOtherAttributes_PoIds() {

        final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        alarmAttributeCriteria.setAttributeName(EVENT_TYPE);
        alarmAttributeCriteria.setAttributeValue("eventType3");
        alarmAttributeCriteria.setOperator(Operator.EQ);
        final List<AlarmAttributeCriteria> attributes = new ArrayList<>();
        attributes.add(alarmAttributeCriteria);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(false);

        compositeNodeCriteria.setAlarmAttributeCriteria(attributes);

        final List<Long> poIds = serviceProxyProvider.getAlarmQueryService().getAlarmPoIds(compositeNodeCriteria, true).getPoIds();
        LOGGER.info("poids returned in testGetAlarmPoIds_CompositeNodeCriteriaOtherAttributes_PoIds compositeNodeCriteria :: {} PoIds are :: {}",
                compositeNodeCriteria, poIds);

        Assert.assertEquals(2, poIds.size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(26)
    public void testGetHistoricalAlarms_CompositeEventTimeCriteria_ZeroAlarmsReturned() {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem1");
        alarmAttributeCriteria.setOperator(Operator.CONTAINS);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute("fdn");

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(false);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getHistoricalAlarms(compositeEventTimeCriteria, true);

        LOGGER.info(
                "alarmQueryResponse returned in testGetAlarms_CompositeNodeCriteriaOtherAttributes_with compositeNodeCriteria :: {} alarmQueryResponse are :: {}",
                compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(0, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(27)
    public void testGetAlarmsCountBySeverity_CompositeNodeCriteria_GetAlarmsCount() {
        final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();
        final List<String> nodes = new ArrayList<>();
        nodes.add(TEST_FDN);
        compositeNodeCriteria.setNodes(nodes);
        final AlarmCountResponse alarmCountResponse = serviceProxyProvider.getAlarmQueryService().getAlarmCount(compositeNodeCriteria, true);
        assertEquals(SUCCESS, alarmCountResponse.getResponse());
        final long count = alarmCountResponse.getAlarmCount();
        LOGGER.info(
                " getAlarmCount returned in testGetAlarmsCountBySeverity_CompositeNodeCriteria_GetAlarmsCount compositeNodeCriteria :: {} count is :: {}",
                compositeNodeCriteria, count);
        Assert.assertEquals(SUCCESS, alarmCountResponse.getResponse());
        Assert.assertEquals(9, count);
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(28)
    public void testGetAlarmsCountBySeverity_CompositeEventTimeCriteria_GetAlarmsCount() {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();
        final List<String> nodes = new ArrayList<>();
        nodes.add(TEST_FDN);
        compositeEventTimeCriteria.setNodes(nodes);
        final AlarmCountResponse alarmCountResponse = serviceProxyProvider.getAlarmQueryService().getAlarmCount(compositeEventTimeCriteria, true);
        assertEquals(SUCCESS, alarmCountResponse.getResponse());
        final long count = alarmCountResponse.getAlarmCount();
        LOGGER.info(
                " getAlarmCount returned in testGetAlarmsCountBySeverity_CompositeEventTimeCriteria_GetAlarmsCount compositeEventTimeCriteria :: {} count is :: {}",
                compositeEventTimeCriteria, count);
        Assert.assertEquals(SUCCESS, alarmCountResponse.getResponse());
        Assert.assertEquals(9, count);
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(29)
    public void testGetAlarms_CompositeNodeCriteriaOnlyNodesSortAttributes_NineAlarmsReturned() {

        final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();
        final List<String> nodes = new ArrayList<>();
        nodes.add(TEST_FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(false);
        final List<String> sortAttributes = new ArrayList<>();
        sortAttributes.add(FDN);
        compositeNodeCriteria.setSortAttribute(FDN);
        compositeNodeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeNodeCriteria.setNodes(nodes);
        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeNodeCriteria, expectedOutputAttributes, true);
        LOGGER.info(
                " AlarmQueryResponse returned in testGetAlarms_CompositeNodeCriteriaOnlyNodes_ThreeAlarmsReturned compositeNodeCriteria :: {} AlarmQueryResponse are :: {}",
                compositeNodeCriteria, alarmQueryResponse);

        Assert.assertEquals(9, alarmQueryResponse.getAlarmRecords().size());

    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(30)
    public void testGetAlarms_compositeEventTimeCriteriaSortAttributes_TwoAlarmsReturned() {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem1");
        alarmAttributeCriteria.setOperator(Operator.EQ);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(EVENT_TIME);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(true);
        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info("AlarmQueryResponse returned in testGetAlarms_compositeEventTimeCriteria_OneAlarmReturned "
                + "compositeEventTimeCriteria :: {} AlarmQueryResponse are :: {}", compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(2, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(31)
    public void testGetAlarms_compositeNodeCriteriaMultiSortAttributes_DESCENDING_ASCENDING() {
        LOGGER.info("EXecuting testGetAlarms_compositeNodeCriteriaMultiSortAttributes_DESCENDING_ASCENDING---------------->");

        final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();

        final AlarmSortCriterion firstAlarmSortCriterion = new AlarmSortCriterion();
        firstAlarmSortCriterion.setSortAttribute(SPECIFIC_PROBLEM);
        firstAlarmSortCriterion.setSortOrder(SortingOrder.DESCENDING);
        firstAlarmSortCriterion.setSortSequence(SortSequence.FIRST_LEVEL_SORT);
        final AlarmSortCriterion secondAlarmSortCriterion = new AlarmSortCriterion();
        secondAlarmSortCriterion.setSortAttribute(EVENT_TYPE);
        secondAlarmSortCriterion.setSortOrder(SortingOrder.ASCENDING);
        secondAlarmSortCriterion.setSortSequence(SortSequence.SECOND_LEVEL_SORT);

        final List<AlarmSortCriterion> alarmSortCriteria = new ArrayList<>();

        alarmSortCriteria.add(firstAlarmSortCriterion);
        alarmSortCriteria.add(secondAlarmSortCriterion);

        compositeNodeCriteria.setAlarmSortCriteria(alarmSortCriteria);

        final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
        final List<String> outputAttributes = new ArrayList<>();
        outputAttributes.add(ALARM_NUMBER);
        outputAttributes.add(SPECIFIC_PROBLEM);
        outputAttributes.add(EVENT_TYPE);
        outputAttributes.add(EVENT_PO_ID);

        expectedOutputAttributes.setOutputAttributes(outputAttributes);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeNodeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                " AlarmQueryResponse returned in testGetAlarms_compositeNodeCriteriaMultiSortAttributes_DESCENDING_ASCENDING compositeEventTimeCriteria :: {} AlarmQueryResponse are :: {}",
                compositeNodeCriteria, alarmQueryResponse);

        Assert.assertEquals(18, alarmQueryResponse.getAlarmRecords().size());
        Assert.assertEquals("eventType03", alarmQueryResponse.getAlarmRecords().get(0).getEventType());
        Assert.assertEquals("eventType19", alarmQueryResponse.getAlarmRecords().get(2).getEventType());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(32)
    public void testGetAlarms_compositeNodeCriteriaMultiSortAttributes_ASCENDING_DESCENDING() {
        LOGGER.info("EXecuting testGetAlarms_compositeNodeCriteriaMultiSortAttributes_ASCENDING_DESCENDING---------------->");
        final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();

        final AlarmSortCriterion firstAlarmSortCriterion = new AlarmSortCriterion();
        firstAlarmSortCriterion.setSortAttribute(SPECIFIC_PROBLEM);
        firstAlarmSortCriterion.setSortOrder(SortingOrder.ASCENDING);
        firstAlarmSortCriterion.setSortSequence(SortSequence.FIRST_LEVEL_SORT);
        final AlarmSortCriterion secondAlarmSortCriterion = new AlarmSortCriterion();
        secondAlarmSortCriterion.setSortAttribute(EVENT_TYPE);
        secondAlarmSortCriterion.setSortOrder(SortingOrder.DESCENDING);
        secondAlarmSortCriterion.setSortSequence(SortSequence.SECOND_LEVEL_SORT);

        final List<AlarmSortCriterion> alarmSortCriteria = new ArrayList<>();

        alarmSortCriteria.add(firstAlarmSortCriterion);
        alarmSortCriteria.add(secondAlarmSortCriterion);

        compositeNodeCriteria.setAlarmSortCriteria(alarmSortCriteria);

        final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
        final List<String> outputAttributes = new ArrayList<>();
        outputAttributes.add(ALARM_NUMBER);
        outputAttributes.add(SPECIFIC_PROBLEM);
        outputAttributes.add(EVENT_TYPE);
        outputAttributes.add(EVENT_PO_ID);

        expectedOutputAttributes.setOutputAttributes(outputAttributes);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeNodeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                " AlarmQueryResponse returned in testGetAlarms_compositeNodeCriteriaMultiSortAttributes_ASCENDING_DESCENDING compositeEventTimeCriteria :: {} AlarmQueryResponse are :: {}",
                compositeNodeCriteria, alarmQueryResponse);

        Assert.assertEquals(18, alarmQueryResponse.getAlarmRecords().size());
        Assert.assertEquals("eventType3", alarmQueryResponse.getAlarmRecords().get(5).getEventType());
        Assert.assertEquals("eventType11", alarmQueryResponse.getAlarmRecords().get(6).getEventType());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(33)
    public void testGetAlarms_compositeEventTimeCriteriaMultiSortAttributes_DESCENDING_ASCENDING() {
        LOGGER.info("EXecuting testGetAlarms_compositeEventTimeCriteriaMultiSortAttributes_DESCENDING_ASCENDING---------------->");
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        compositeEventTimeCriteria.setToTime(new Date());
        compositeEventTimeCriteria.setOperator(Operator.LE);

        final AlarmSortCriterion firstAlarmSortCriterion = new AlarmSortCriterion();
        firstAlarmSortCriterion.setSortAttribute(SPECIFIC_PROBLEM);
        firstAlarmSortCriterion.setSortOrder(SortingOrder.DESCENDING);
        firstAlarmSortCriterion.setSortSequence(SortSequence.FIRST_LEVEL_SORT);
        final AlarmSortCriterion secondAlarmSortCriterion = new AlarmSortCriterion();
        secondAlarmSortCriterion.setSortAttribute(EVENT_TYPE);
        secondAlarmSortCriterion.setSortOrder(SortingOrder.ASCENDING);
        secondAlarmSortCriterion.setSortSequence(SortSequence.SECOND_LEVEL_SORT);

        final List<AlarmSortCriterion> alarmSortCriteria = new ArrayList<>();

        alarmSortCriteria.add(firstAlarmSortCriterion);
        alarmSortCriteria.add(secondAlarmSortCriterion);

        compositeEventTimeCriteria.setAlarmSortCriteria(alarmSortCriteria);

        final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
        final List<String> outputAttributes = new ArrayList<>();
        outputAttributes.add(ALARM_NUMBER);
        outputAttributes.add(SPECIFIC_PROBLEM);
        outputAttributes.add(EVENT_TYPE);
        outputAttributes.add(EVENT_PO_ID);

        expectedOutputAttributes.setOutputAttributes(outputAttributes);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                " AlarmQueryResponse returned in testGetAlarms_compositeEventTimeCriteriaMultiSortAttributes_DESCENDING_ASCENDING compositeEventTimeCriteria :: {} AlarmQueryResponse are :: {}",
                compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(16, alarmQueryResponse.getAlarmRecords().size());
        Assert.assertEquals("eventType03", alarmQueryResponse.getAlarmRecords().get(0).getEventType());
        Assert.assertEquals("eventType19", alarmQueryResponse.getAlarmRecords().get(2).getEventType());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(34)
    public void testGetAlarms_compositeEventTimeCriteriaMultiSortAttributes_ASCENDING_DESCENDING() {
        LOGGER.info("EXecuting testGetAlarms_compositeEventTimeCriteriaMultiSortAttributes_ASCENDING_DESCENDING---------------->");
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        compositeEventTimeCriteria.setToTime(new Date());
        compositeEventTimeCriteria.setOperator(Operator.LE);

        final AlarmSortCriterion firstAlarmSortCriterion = new AlarmSortCriterion();
        firstAlarmSortCriterion.setSortAttribute(SPECIFIC_PROBLEM);
        firstAlarmSortCriterion.setSortOrder(SortingOrder.ASCENDING);
        firstAlarmSortCriterion.setSortSequence(SortSequence.FIRST_LEVEL_SORT);
        final AlarmSortCriterion secondAlarmSortCriterion = new AlarmSortCriterion();
        secondAlarmSortCriterion.setSortAttribute(EVENT_TYPE);
        secondAlarmSortCriterion.setSortOrder(SortingOrder.DESCENDING);
        secondAlarmSortCriterion.setSortSequence(SortSequence.SECOND_LEVEL_SORT);

        final List<AlarmSortCriterion> alarmSortCriteria = new ArrayList<>();

        alarmSortCriteria.add(firstAlarmSortCriterion);
        alarmSortCriteria.add(secondAlarmSortCriterion);

        compositeEventTimeCriteria.setAlarmSortCriteria(alarmSortCriteria);

        final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();
        final List<String> outputAttributes = new ArrayList<>();
        outputAttributes.add(ALARM_NUMBER);
        outputAttributes.add(SPECIFIC_PROBLEM);
        outputAttributes.add(EVENT_TYPE);
        outputAttributes.add(EVENT_PO_ID);

        expectedOutputAttributes.setOutputAttributes(outputAttributes);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                " AlarmQueryResponse returned in testGetAlarms_compositeEventTimeCriteriaMultiSortAttributes_ASCENDING_DESCENDING compositeEventTimeCriteria :: {} AlarmQueryResponse are :: {}",
                compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(16, alarmQueryResponse.getAlarmRecords().size());
        Assert.assertEquals("eventType11", alarmQueryResponse.getAlarmRecords().get(5).getEventType());
        Assert.assertEquals("eventType10", alarmQueryResponse.getAlarmRecords().get(6).getEventType());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(35)
    public void setupForTBACTestcases() {
        LOGGER.info("Removing node : AQS_ARQUILLIAN0025 from targetgroup");
        serviceProxyProvider.getTargetGroupRegistry().removeNodeFromTargetGroup(TEST_NE_PREFIX + 25, SecurityPrivilegeServiceMock.SEC_TARGET_GROUP_1);
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(36)
    // As AQS_ARQUILLIAN0025 removed from TBAC group we should only get one alarm which is created with SpecificProblem1 for AQS_ARQUILLIAN0026
    public void testGetAlarms_CompositeEventTimeCriteriaTBACForCONTAINS_OneAlarmReturned() {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        final List<AlarmAttributeCriteria> otherAlarmAttributes = new ArrayList<>();
        alarmAttributeCriteria.setAttributeName(SPECIFIC_PROBLEM);
        alarmAttributeCriteria.setAttributeValue("SpecificProblem1");
        alarmAttributeCriteria.setOperator(Operator.CONTAINS);
        otherAlarmAttributes.add(alarmAttributeCriteria);
        compositeEventTimeCriteria.setAlarmAttributeCriteria(otherAlarmAttributes);

        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(true);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                "AlarmQueryResponse returned in testGetAlarms_CompositeEventTimeCriteriaTBACForCONTAINS_NoAlarmReturned compositeNodeCriteria :: {} alarmQueryResponse are :: {}",
                compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(1, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(37)
    public void testGetAlarmsWithTBAC_CompositeEventTimeCriteriaTBACForSpecificNodes() {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();
        final List<String> fdns = new ArrayList<>();
        fdns.addAll(Arrays.asList(TEST_FDN, TEST_FDN1));
        compositeEventTimeCriteria.setNodes(fdns);
        compositeEventTimeCriteria.setSortDirection(SortingOrder.ASCENDING);
        compositeEventTimeCriteria.setSortAttribute(FDN);

        final ExpectedOutputAttributes expectedOutputAttributes = getExpectedOutputAttributes(true);

        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeEventTimeCriteria, expectedOutputAttributes, true);

        LOGGER.info(
                "AlarmQueryResponse returned in testGetAlarmsWithTBAC_CompositeEventTimeCriteriaTBACForSpecificNodes :: {} alarmQueryResponse are :: {}",
                compositeEventTimeCriteria, alarmQueryResponse);

        Assert.assertEquals(9, alarmQueryResponse.getAlarmRecords().size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(38)
    public void testGetAlarmsCountWithTBAC_CompositeEventTimeCritera() {
        final CompositeEventTimeCriteria compositeEventTimeCriteria = new CompositeEventTimeCriteria();
        final List<String> fdns = new ArrayList<>();
        fdns.addAll(Arrays.asList(TEST_FDN, TEST_FDN1));
        compositeEventTimeCriteria.setNodes(fdns);
        final AlarmCountResponse alarmCountResponse = serviceProxyProvider.getAlarmQueryService().getAlarmCount(compositeEventTimeCriteria, true);
        final long count = alarmCountResponse.getAlarmCount();
        LOGGER.info(" getAlarmCount returned in testGetAlarmsCountTBAC_CompositeEventTimeCriteria compositeEventTimeCriteria :: {} count is :: {}",
                compositeEventTimeCriteria, count);
        Assert.assertEquals(9, count);
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(39)
    public void testGetPoIds_NullOORCriteria_Returns_All_Alarms() {
        final List<Long> poIds = serviceProxyProvider.getAlarmQueryService().getAlarmPoIds(null).getPoIds();

        LOGGER.info("PoIds returned in testGetPoIds_NullOORCriteria_Returns_All_Alarms null oORCriteria are :: {}", poIds);
        Assert.assertEquals(18, poIds.size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(40)
    public void testGetPoIds_OORCriteria_Returns_All_Alarms() {
        final OORCriteria oORCriteria = getOORCriteria("ENodeBFunction", Operator.CONTAINS);
        final List<Long> poIds = serviceProxyProvider.getAlarmQueryService().getAlarmPoIds(oORCriteria).getPoIds();

        LOGGER.info("PoIds returned in testGetPoIds_OORCriteria_Returns_All_Alarms oORCriteria :: {} PoIds are :: {}", oORCriteria, poIds);
        Assert.assertEquals(18, poIds.size());
    }

    @Test
    @OperateOnDeployment(AQS_TEST_WAR)
    @InSequence(41)
    public void testGetAlarms_RootCriteria() {

        final CompositeNodeCriteria compositeNodeCriteria = new CompositeNodeCriteria();
        final List<String> nodes = new ArrayList<>();
        nodes.add(TEST_FDN);
        compositeNodeCriteria.setNodes(nodes);

        final AlarmAttributeCriteria alarmAttributeCriteria = new AlarmAttributeCriteria();
        alarmAttributeCriteria.setAttributeName(ROOT);
        alarmAttributeCriteria.setAttributeValue(PRIMARY);
        alarmAttributeCriteria.setOperator(Operator.EQ);
        final List<AlarmAttributeCriteria> attributes = new ArrayList<>();
        attributes.add(alarmAttributeCriteria);

        final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();

        final List<String> outputAttributes = new ArrayList<>();
        outputAttributes.add(ALARM_NUMBER);
        outputAttributes.add(ADDITIONAL_INFORMATION);
        outputAttributes.add(ROOT);
        outputAttributes.add(CI_GROUP_1);
        outputAttributes.add(CI_GROUP_2);
        expectedOutputAttributes.setOutputAttributes(outputAttributes);

        compositeNodeCriteria.setAlarmAttributeCriteria(attributes);
        compositeNodeCriteria.setSortAttribute(ADDITIONAL_INFORMATION);
        final AlarmAttributeResponse alarmQueryResponse = serviceProxyProvider.getAlarmQueryService()
                .getAlarms(compositeNodeCriteria, expectedOutputAttributes, true);

        LOGGER.info(" AlarmQueryResponse returned in testGetAlarms_RootCriteria compositeNodeCriteria :: {} AlarmQueryResponse are :: {}",
                compositeNodeCriteria, alarmQueryResponse);

        Assert.assertEquals(1, alarmQueryResponse.getAlarmRecords().size());
        Assert.assertEquals(
                "targetAdditionalInformation:CI={\"P\":\"81d4fae-7dec-11d0-a765-00a0c91e6bf6\",\"C\":[{\"I\":\"201f0123-88ca-23a2-7451-8B5872ac457b\",\"n\":\"vRC\"}]};",
                alarmQueryResponse.getAlarmRecords().get(0).getAdditionalInformation());
    }

    private OORCriteria getOORCriteria(final String oorString, final Operator operator) {
        final OORCriteria oorCriteria = new OORCriteria();
        final OORExpression oorExpression = new OORExpression();
        oorExpression.setObjectOfReference(oorString);
        oorExpression.setOperator(operator);
        final List<OORExpression> oorExpressions = new ArrayList<>();
        oorExpressions.add(oorExpression);
        oorCriteria.setOorExpressions(oorExpressions);
        return oorCriteria;
    }

    private ExpectedOutputAttributes getExpectedOutputAttributes(final boolean fdnRequired) {
        final ExpectedOutputAttributes expectedOutputAttributes = new ExpectedOutputAttributes();

        final List<String> outputAttributes = new ArrayList<>();
        outputAttributes.add(ALARM_NUMBER);
        outputAttributes.add(SPECIFIC_PROBLEM);
        if (fdnRequired) {
            outputAttributes.add(FDN);
        }
        expectedOutputAttributes.setOutputAttributes(outputAttributes);
        return expectedOutputAttributes;
    }
}