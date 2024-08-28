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

package com.ericsson.oss.services.fm.alarmqueryservice.api.exception;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;

/**
 * An Exception class that provides information on any incorrect input received by AlarmQueryService. <br>
 * This exception will be thrown by AlarmQueryService on its methods that will not return{@link AlarmAttributeResponse}". Eg. List<Long>
 * getPoIds(OORCriteria oorCriteria), List<Long> getAllPoIds().
 *
 */

public class AttributeConstraintViolationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final String ERROR_MESSAGE_PREFIX = "Exception occurred :: %s";
    private static final String ERROR_MESSAGE = ERROR_MESSAGE_PREFIX + "; Affected Entity=%s; Supplied Value=%s";

    public AttributeConstraintViolationException(final String message, final Throwable cause) {
        super(getDetails(message), cause);
    }

    public AttributeConstraintViolationException(final String message) {
        super(String.format(message));
    }

    public AttributeConstraintViolationException(final String details, final String entityName, final Object suppliedValue) {
        super(getDetails(details, entityName, suppliedValue));
    }

    private static String getDetails(final String details, final String entityName, final Object suppliedValue) {
        return String.format(ERROR_MESSAGE, details, entityName, suppliedValue);
    }

    private static String getDetails(final String details) {
        return String.format(ERROR_MESSAGE_PREFIX, details);
    }
}
