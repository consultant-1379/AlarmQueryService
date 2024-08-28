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

package com.ericsson.oss.services.alarm.query.service.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;
import com.ericsson.oss.services.alarm.query.service.models.AlarmLogData;
import com.ericsson.oss.services.alarm.query.service.models.AlarmQueryData;
import com.ericsson.oss.services.alarm.query.service.models.AlarmQueryResponse;
import com.ericsson.oss.services.alarm.query.service.models.NodeMatchType;

/**
 *
 * The interface that provide contract for retrieval of Open / Historical alarms.
 *
 *
 **/
@EService
@Remote
public interface AlarmQueryService {

    /**
     *
     * This method take AlarmQueryData as input and based on the attributes ofAlarmQueryData it returns the AlarmQuery response.
     *
     * @Method getAlarmList
     * @param alarmQueryData
     *
     **/
    AlarmQueryResponse getAlarmList(AlarmQueryData alarmQueryData);

    /**
     *
     * This method take AlarmQueryData as input and based on the Authorization of AlarmQueryData it returns the AlarmQuery response.
     *
     * @Method getAlarmList
     * @param alarmQueryData
     *
     **/
    AlarmQueryResponse getAlarmList(AlarmQueryData alarmQueryData, boolean authorized);

    /**
     *
     * This method take nodes, alarmAttributes and previousCommentsRequired as input
     *
     * it returns the AlarmQuery response along with previous comments if it requires.
     *
     * @Method getAlarmList
     * @param nodes
     * @param alarmAttributes
     *            : alarmAttributes should contain list of Strings in following format "attribute name#attribute value#operator"
     * @param previousCommentsRequired
     *
     **/
    @Deprecated
    AlarmQueryResponse getAlarmList(List<String> nodes, List<String> oors, List<String> alarmAttributes, boolean previousCommentsRequired);

    /**
     *
     * This method takes the list of Nodes, ObjectOfReferences, AlarmAttributes and OutputAttributes
     *
     * It returns the List of Map<String,Object>. i.e Map has all the required output Attribute Names & Values as Key & Value respectively.
     *
     * @param nodes
     * @param oors
     * @param alarmAttributes
     *            : It should contain list of Strings in following format "attributeName#attributeValue#operator"
     * @param outputAttribute
     *            : It should contain list of OutputAttributes that the user want to see on the Output Console
     * @param previousCommentsRequired
     *
     **/
    List<Map<String, Object>> getAlarmListForCLI(List<String> nodes, List<String> oors, List<String> alarmAttributes, List<String> outputAttribute);

    /**
     *
     * This method take nodes, alarmAttributes and dates as input
     *
     * it returns the AlarmQuery response with out previous comments
     *
     * returned AlarmQuery response have alarms which are modified between the dates specified.
     *
     * @Method getAlarmList
     * @param nodes
     * @param alarmAttributes
     *            : alarmAttributes should contain list of Strings in following format "attribute name#attribute value#operator"
     * @param firstRecordTime
     *
     **/

    AlarmQueryResponse getRecentlyUpdatedAlarms(List<String> nodes, List<String> oors, List<String> alarmAttributes, List<Date> dates,
                                                List<String> outputAttributes);

    /**
     *
     * Executes the query and presents the result as a <b> count</b> of how many instances are matched against the inputs.
     *
     * it returns count of alrams.
     *
     * @param nodes
     *            fdns of the nodes
     *
     * @param alarmAttributes
     *            : alarmAttributes should contain list of Strings in following format "attribute name#attribute value#operator"
     *
     **/

    Map<String, Long> getAlarmsCount(List<String> nodes);

    /**
     *
     * Executes the query and presents the result as a <b> count</b> of how many instances are matched against the inputs before the specified
     * timestamp.
     *
     * it returns count of alrams.
     *
     * @param nodes
     *            fdns of the nodes
     *
     * @param timeStamp
     *            : counts less than or equal to this timestamp are sent
     *
     **/

    Map<String, Long> getFilterCount(List<String> nodes, Long lastUpdatedTime);

    /**
     *
     *
     * This method take objectOfReference and matchType as input
     *
     * it returns PoIds.
     *
     * @Method getPoIdsList
     * @param node
     *            : object Of Reference stored in open alarm matchType : can be CONTAINS/EQULAS/ALL
     *
     **/
    List<Long> getPoIdsList(String node, String objectOfReference, NodeMatchType matchType);

    /**
     *
     * This method take AlarmLogData as input and based on the attributes of AlarmLogData it returns the AlarmQuery response. returned AlarmQuery
     * response have history alarms.
     *
     * @Method getHistoryAlarmList
     * @param alarmLogData
     *
     **/
    AlarmQueryResponse getHistoryAlarmList(AlarmLogData alarmLogData);

    /**
     *
     * This method take nodes, alarmAttributes as input
     *
     * it returns the PoIdsList as output.
     *
     *
     * @Method getPoIdsList
     * @param nodes
     * @param alarmAttributes
     *            : alarmAttributes should contain list of Strings in following format "attribute name#attribute value#operator"
     *
     **/

    List<Long> fetchPoIdsBasedFilters(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes);

    /**
     *
     * This method take nodes, oors, alarmAttributes, sort Attributes and sort mode as input
     *
     * it returns the PoIdsList along with sort attribute as output.
     *
     *
     * @Method fetchPoIdsBasedFilters
     * @param oors
     *            :Object of reference of the NetworkElement.
     * @param nodes
     *            : Fully Distinguished name of the NetworkElement
     * @param alarmAttributes
     *            : alarmAttributes should contain list of Strings in following format "attribute name#attribute value#operator"
     * @param sortAttribute
     *            : sortAttribute specifies on what alarm attribute sorting should happen
     * @param sortMode
     *            : sortMode specifies on what direction sorting should happen either ascending or descending
     *
     **/

    List<Object[]> fetchPoIdsBasedFilters(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes, String sortAttribute,
                                          String sortMode);

    /**
     *
     * This method take nodes, oors, alarmAttributes, sort Attributes, sort mode and lastUpdatedTime as input
     *
     * it returns the PoIdsList which have lastUpdated greater than lastUpdatedTime
     *
     *
     * @Method fetchPoIdsBasedFilters
     * @param oors
     *            :Object of reference of the NetworkElement.
     * @param nodes
     *            : Fully Distinguished name of the NetworkElement
     * @param alarmAttributes
     *            : alarmAttributes should contain list of Strings in following format "attribute name#attribute value#operator"
     * @param sortAttribute
     *            : sortAttribute specifies on what alarm attribute sorting should happen
     * @param sortMode
     *            : sortMode specifies on what direction sorting should happen either ascending or descending
     *
     * @param lastUpdatedTime
     *            : ids having time more than this time will be returned
     *
     **/

    List<Long> fetchPoIdsBasedFilters(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes, String sortAttribute,
                                      String sortMode, Long lastUpdatedTime);

    /**
     *
     * This method take nodes, alarmAttributes as input Executes the query and presents the result as a list. Will return a projection with an List on
     * which the type of data will be an Object array. The order of the alarmNumber and objectOfrerefrence. It is the responsibility of the users of
     * this method to do the right type of casting.
     *
     *
     * @Method getPoIdsList
     * @param nodes
     * @param alarmAttributes
     *            : alarmAttributes should contain list of Strings in following format "attribute name#attribute value#operator"
     *
     **/

    List<Object[]> getAlarmNumbersAndObjectOfRefrences(final List<String> nodes, final List<String> oors, final List<String> alarmAttributes);

    /**
     *
     * This method take AlarmLogData as input and based on the attributes of AlarmLogData it returns the AlarmQuery response. returned AlarmQuery
     * response have history alarms.This is for internal application use to avoid user RBAC.
     *
     * @Method getHistoryAlarmList
     * @param alarmLogData
     * @param isAuthorized
     *
     **/
    AlarmQueryResponse getHistoryAlarmList(AlarmLogData alarmLogData, boolean isAuthorized);
}
