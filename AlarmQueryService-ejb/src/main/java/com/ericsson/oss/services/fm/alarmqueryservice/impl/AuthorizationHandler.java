/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.impl;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.ALARM_SEARH;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.QUERY;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.OPEN_ALARMS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.security.accesscontrol.EPredefinedRole;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;

/**
 *
 * A class responsible for checking the authorization Only authorized users such as Administrator and Operator can invoke designated methods. <br>
 * In case of un-authorized access, {@link com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException} will be thrown.
 *
 *
 */
public class AuthorizationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationHandler.class);

    /**
    * Authorization check on "Query for Open or History alarms data."
    */
    @Authorize(resource = ALARM_SEARH, action = QUERY, role = { EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR })
    public void checkAuthorization() {
        LOGGER.debug("User is Authorized");
    }

    /**
    * Authorization check on "Query for Open alarms data."
    */
    @Authorize(resource = OPEN_ALARMS, action = QUERY, role = { EPredefinedRole.ADMINISTRATOR, EPredefinedRole.OPERATOR })
    public void checkAuthorizationForOpenAlarm() {
    LOGGER.debug("User is Authorized");
    }
}
