
package com.ericsson.oss.services.fm.alarmqueryservice.api;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmCountResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmPoIdResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmPoIdCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmSortCriterion;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeAlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeEventTimeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.CompositeNodeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.ExpectedOutputAttributes;
import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.OORCriteria;

/**
 * The interface that provide contract for retrieval of Open / Historical alarms
 *
 **/

@EService
@Remote
public interface AlarmQueryService {

    /**
     * Retrieves alarms based on {@link AlarmPoIdCriteria}. A list of attributes ( {@link ExpectedOutputAttributes}) to be retrieved as part of the
     * response can also be an input to this method.
     * <p>
     * When ExpectedOuputAttributes is not set, the response consists of all the attributes of an alarm(excluding node PoId and comments history)
     * <p>
     *
     * @param alarmPoIdCriteria
     *            - an encapsulation of alarm PoIds
     * @param expectedOutputAttributes
     *            - a projection of expected alarm attributes, in addition to node PoId and comments history
     * @param authorizationRequired
     *            - authorization state, which determines whether authorization is required for a particular client. Currently, authorization is
     *            required only for GUI and CLI clients.
     * @return {@link AlarmAttributeResponse} - The abstraction of alarms matching the specified criteria
     */
    AlarmAttributeResponse getAlarms(AlarmPoIdCriteria alarmPoIdCriteria, ExpectedOutputAttributes expectedOutputAttributes,
                                     final boolean authorizationRequired);

    /**
     * Retrieves alarms based on {@link AlarmPoIdCriteria}. A list of attributes ( {@link ExpectedOutputAttributes}) to be retrieved as part of the
     * response can also be an input to this method.
     * <p>
     * When ExpectedOuputAttributes is not set, the response consists of all the attributes of an alarm(excluding node PoId and comments history)
     * <p>
     *
     * @param alarmPoIdCriteria
     *            - an encapsulation of alarm PoIds
     * @param expectedOutputAttributes
     *            - a projection of expected alarm attributes, in addition to node PoId and comments history
     * @param authorizationRequired
     *            - authorization state, which determines whether authorization is required for a particular client. Currently, authorization is
     *            required only for GUI and CLI clients.
     * @param tbacValidationRequired
     *            - validation state, which determines whether tbac validation is required for a particular client. Currently, tbac is required only
     *            get alarm details use case.
     * @return {@link AlarmAttributeResponse} - The abstraction of alarms matching the specified criteria
     */
    AlarmAttributeResponse getAlarms(AlarmPoIdCriteria alarmPoIdCriteria, ExpectedOutputAttributes expectedOutputAttributes,
                                     final boolean authorizationRequired, final boolean tbacValidationRequired);

    /**
     * Retrieves alarms based on {@link CompositeNodeCriteria}. A list of attributes ( {@link ExpectedOutputAttributes}) to be retrieved as part of
     * the response can also be an input to this method.
     * <p>
     * When ExpectedOuputAttributes is not set, the response consists of all the attributes of an alarm(excluding node PoId and comments history)
     * <p>
     *
     * @param compositeNodeCriteria
     *            - an encapsulation of nodes list and alarm attributes
     * @param expectedOutputAttributes
     *            - a projection of expected alarm attributes, in addition to node PoId and comments history
     * @param authorizationRequired
     *            - authorization state, which determines whether authorization is required for a particular client. Currently, authorization is
     *            required only for GUI and CLI clients.
     * @return {@link AlarmAttributeResponse} - The abstraction of alarms matching the specified criteria
     */
    AlarmAttributeResponse getAlarms(CompositeNodeCriteria compositeNodeCriteria, ExpectedOutputAttributes expectedOutputAttributes,
                                     boolean authorizationRequired);

    /**
     * Retrieves alarms count based on {@link CompositeNodeCriteria}.
     * <p>
     *
     * @param compositeNodeCriteria
     *            - an encapsulation of nodes list and alarm attributes
     * @param authorizationRequired
     *            - authorization state, which determines whether authorization is required for a particular client. Currently, authorization is
     *            required only for GUI and CLI clients.
     * @return {@link AlarmCountResponse} - The abstraction of alarms matching the specified criteria
     */
    AlarmCountResponse getAlarmCount(CompositeNodeCriteria compositeNodeCriteria, boolean authorizationRequired);

    /**
     * Retrieves alarms count based on {@link CompositeEventTimeCriteria}.
     * <p>
     *
     * @param compositeEventTimeCriteria
     *            - an encapsulation of nodes list and alarm attributes
     * @param authorizationRequired
     *            - authorization state, which determines whether authorization is required for a particular client. Currently, authorization is
     *            required only for GUI and CLI clients.
     * @return {@link AlarmCountResponse} - The abstraction of alarms matching the specified criteria
     */
    AlarmCountResponse getAlarmCount(CompositeEventTimeCriteria compositeEventTimeCriteria, boolean authorizationRequired);

    /**
     * Retrieves alarms based on {@link CompositeEventTimeCriteria}. A list of attributes ( {@link ExpectedOutputAttributes}) to be retrieved as part
     * of the response can also be an input to this method. <br>
     * <p>
     * When ExpectedOuputAttributes is not set, the response consists of all the attributes of an alarm(excluding node PoId and comments history)
     * <p>
     *
     * @param compositeEventTimeCriteria
     *            - an encapsulation of event time of the alarm along with nodes and alarm attributes
     * @param authorizationRequired
     *            - authorization state, which determines whether authorization is required for a particular client. Currently, authorization is
     *            required only for GUI and CLI clients.
     * @return {@link AlarmAttributeResponse} - The abstraction of alarms matching the specified criteria
     */
    AlarmAttributeResponse getAlarms(CompositeEventTimeCriteria compositeEventTimeCriteria, ExpectedOutputAttributes expectedOutputAttributes,
                                     final boolean authorizationRequired);

    /**
     * Retrieve openAlarms based on a list of {@link CompositeNodeCriteria}. A list of attributes ( {@link ExpectedOutputAttributes}) to be retrieved
     * as part of the response can also be an input to this method. This method actually provides a temporary way to execute a dps query based on an
     * OR condition. It will be replaced by the effective API functionality of QueryService in a future sprint
     * <p>
     * When ExpectedOuputAttributes is not set, the response consists of all the attributes of an alarm(excluding node PoId and comments history)
     * <p>
     *
     * @param compositeNodeCriterias
     *            - an encapsulation of nodes list and alarm attributes
     * @param expectedOutputAttributes
     *            - a projection of expected alarm attributes, in addition to node PoId and comments history
     * @param alarmSortCriteria
     *            - sort criterion for alarms
     * @param authorizationRequired
     *            - authorization state, which determines whether authorization is required for a particular client. Currently, authorization is
     *            required only for GUI and CLI clients.
     * @return {@link AlarmAttributeResponse} - The abstraction of alarms matching the specified criteria
     */
    AlarmAttributeResponse getAlarms(final List<CompositeNodeCriteria> compositeNodeCriterias,
                                     final ExpectedOutputAttributes expectedOutputAttributes, List<AlarmSortCriterion> alarmSortCriteria,
                                     final boolean authorizationRequired);

    /**
     * Retrieves PoIds(A unique identity assigned to each alarm) of the alarms based on {@link CompositeNodeCriteria}.
     * <p>
     *
     * @param compositeNodeCriteria
     *            - an encapsulation of nodes and alarm attributes
     * @param authorizationRequired
     *            - authorization state, which determines whether authorization is required for a particular client. Currently, authorization is
     *            required only for GUI and CLI clients.
     * @return {@link AlarmAttributeResponse} - The PoIds of alarms matching the specified criteria
     */
    AlarmPoIdResponse getAlarmPoIds(CompositeNodeCriteria compositeNodeCriteria, final boolean authorizationRequired);

    /**
     * Retrieves PoIds (A unique identity assigned to each alarm) of the alarms based on {@link OORCriteria}. <br>
     *
     * @throws {@link
     *             AttributeConstraintViolationException}for the improper data set in {@link OORCriteria}.<br>
     * @param oorCriteria
     *            {@link OORCriteria}
     * @return the List PoIds which satisfies the supplied criteria
     */
    AlarmPoIdResponse getAlarmPoIds(OORCriteria oorCriteria);

    /**
     * Retrieves all the PoIds (A unique identity assigned to each alarm) from Data store
     *
     * @return the List all the PoIds present data store.
     */
    AlarmPoIdResponse getAllAlarmPoIds();

    /**
     * Retrieves Alarms Count By Severity with the given alarms based on {@link CompositeNodeCriteria}.
     *
     * @param compositeNodeCriteria
     *            {@link CompositeNodeCriteria}
     * @throws --
     *             A runtime exception can be thrown in case of EXCEPTION.
     * @return the filter count map -- contains number of alarms against each severity for the nodes set in NodeCriteria<br>
     *         eg : Critical,10
     */
    Map<String, Long> getAlarmCountBySeverity(CompositeNodeCriteria compositeNodeCriteria, final boolean authorizationRequired);

    /**
     * Retrieves historical alarms based on {@link CompositeEventTimeCriteria}.
     *
     * @param compositeEventTimeCriteria
     *            - an encapsulation of alarm event time of alarm along with nodes and alarm attributes.
     * @param authorizationRequired
     *            - authorization state, which determines whether authorization is required for a particular client. Currently, authorization is
     *            required only for GUI and CLI clients. <br>
     *            Other clients which doesn't need authorization should set it to false
     * @return {@link AlarmAttributeResponse} - The abstraction of alarms matching the specified criteria
     */
    AlarmAttributeResponse getHistoricalAlarms(final CompositeEventTimeCriteria compositeEventTimeCriteria, final boolean authorizationRequired);

    /**
     * Retrieves historical alarms based on {@link CompositeAlarmAttributeCriteria}.
     *
     * @param compositeAlarmAttributeCriteria
     *            - an encapsulation of alarm attributes.
     * @return {@link AlarmAttributeResponse} - The abstraction of alarms matching the specified criteria
     */
    AlarmAttributeResponse getHistoricalAlarms(List<CompositeAlarmAttributeCriteria> compositeAlarmAttributeCriteria,
                                               final boolean authorizationRequired);

    /**
     * Retrieves the additional attributes.
     *
     * @throws --
     *             A runtime exception can be thrown in case of EXCEPTION.
     * @return List<String> - Returns the list of additional attributes.
     */
    List<String> getAlarmAdditionalAttributes();

    /**
     * Retrieves openAlarms and count.
     *
     * @param compositeEventTimeCriteria
     *            - an encapsulation of alarm event time of alarm along with nodes and alarm attributes.
     * @param maxNumberOfAlarmsInCli
     *            -maximum number of alarms that can be retrieved by cli.
     * @param expectedOutputAttributes
     *            - a projection of expected alarm attributes, in addition to node PoId and comments history
     * @return {@link AlarmAttributeResponse} - The abstraction of alarms matching the specified criteria
     */
    AlarmAttributeResponse getOpenAlarmsWithCount(final CompositeEventTimeCriteria compositeEventTimeCriteria, final Integer maxNumberOfAlarmsInCli,
                                                  final ExpectedOutputAttributes expectedOutputAttributes,
                                                  final AlarmAttributeResponse alarmAttributeResponse);

    /**
     * Retrieves Correlated Alarms Count By Severity with the given alarms based on List<{@link CompositeNodeCriteria}>
     *
     * @param compositeNodeCriterias
     *            - list of {@link CompositeNodeCriteria}, an encapsulation of nodes and alarm attributes
     * @param authorizationRequired
     *            - authorization state, which determines whether authorization is required for a particular client. Currently, authorization is
     *            required only for GUI and CLI clients. <br>
     *            Other clients which doesn't need authorization should set it to false
     * @return Map<String,Long> - the filter count map. It contains number of alarms against each severity for the nodes/correlation groups set in
     *         compositeNodeCriterias <br>
     *         eg : Critical,10
     */
    Map<String, Long> getAlarmCountBySeverity(final List<CompositeNodeCriteria> compositeNodeCriterias, boolean authorizationRequired);
}
