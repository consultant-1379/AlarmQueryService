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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Class maintains ENM values of Severity and Filter values. <br>
 * Filter constants are used in GUI .
 *
 */
public final class SeveritiesMapper {

    public static final Map<String, String> SEVERITY_MAP;
    static {
        SEVERITY_MAP = new HashMap<String, String>();
        SEVERITY_MAP.put(EnumConstants.CRITICAL, FilterConstants.CRITICAL);
        SEVERITY_MAP.put(EnumConstants.MAJOR, FilterConstants.MAJOR);
        SEVERITY_MAP.put(EnumConstants.MINOR, FilterConstants.MINOR);
        SEVERITY_MAP.put(EnumConstants.INDETERMINATE, FilterConstants.INDETERMINATE);
        SEVERITY_MAP.put(EnumConstants.WARNING, FilterConstants.WARNING);
        SEVERITY_MAP.put(EnumConstants.CLEARED, FilterConstants.CLEARED);
    }

    private SeveritiesMapper() {
    }

}