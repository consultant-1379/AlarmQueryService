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

public abstract class AlarmQueryServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Create a AQS Exception with the given message and underlying cause.
     *
     * @param message
     *            the message for this exception.
     * @param cause
     *            the underlying exception which caused this problem.
     */
    public AlarmQueryServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a AQS Exception with the given message.
     *
     * @param message
     *            the message for this exception.
     */
    public AlarmQueryServiceException(final String message) {
        super(message);
    }

}
