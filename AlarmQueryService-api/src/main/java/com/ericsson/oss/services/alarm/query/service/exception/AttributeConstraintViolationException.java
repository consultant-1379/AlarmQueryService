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

package com.ericsson.oss.services.alarm.query.service.exception;

public class AttributeConstraintViolationException extends AlarmQueryServiceException
{

    private static final long serialVersionUID = 1L;

    private static final String ERROR_MESSAGE_ONLY_DETAILS = "Model Constraint Violated: Reason=%s";
    private static final String ERROR_MESSAGE = ERROR_MESSAGE_ONLY_DETAILS + "; Affected Entity=%s; Supplied Value=%s";

    public AttributeConstraintViolationException(final String message, final Throwable cause) {
        super(getDetails(message), cause);
    }

    public AttributeConstraintViolationException(final String message) {
        super(getDetails(message));
    }

    public AttributeConstraintViolationException(final String details,
                                                 final String entityName, final Object suppliedValue) {
        super(getDetails(details, entityName, suppliedValue));
    }

    private static String getDetails(final String details, final String entityName, final Object suppliedValue) {
        return String.format(ERROR_MESSAGE, details, entityName, suppliedValue);
    }

    private static String getDetails(final String details) {
        return String.format(ERROR_MESSAGE_ONLY_DETAILS, details);
    }
}
