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

public class InputFormatConstraintViolationException extends AlarmQueryServiceException {

    private static final long serialVersionUID = 1L;
    private String details;

    /**
     * Create a new exception for the input wit improper format.
     *
     * @param reason
     *            the specifics of what data was not found in the model.
     * @param cause
     *            the underlying exception which caused this problem.
     */
    public InputFormatConstraintViolationException(final String reson, final Throwable cause) {
        super(reson, cause);
    }

    /**
     * Create a new exception for the input wit improper format.
     *
     * @param reason
     *            the specifics of what data was not found in the model.
     */
    public InputFormatConstraintViolationException(final String reason) {
        super(reason);
    }

    public String getReason() {
        return details;
    }

    public void setReason(final String reason) {
        this.details = reason;
    }

}
