/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.impl.comparator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface that holds the Map<String, Integer> with the values of Severities and pseudoSeverities with respective priorities.
 *
 *
 */
public interface PseudoSeverities {

    Map<String, Integer> PSEUDO_SEVERITIES_MAP = Collections.unmodifiableMap(new HashMap<String, Integer>() {
        private static final long serialVersionUID = 1L;
        {
            put("CRITICAL", 99);
            put("MAJOR", 98);
            put("MINOR", 97);
            put("WARNING", 96);
            put("INDETERMINATE", 95);
            put("CLEARED", 94);
            put("UNDEFINED", 93);

            put("CRITICAL_CRITICAL", 89);
            put("MAJOR_CRITICAL", 88);
            put("MINOR_CRITICAL", 87);
            put("WARNING_CRITICAL", 86);
            put("INDETERMINATE_CRITICAL", 85);
            put("CLEARED_CRITICAL", 84);
            put("UNDEFINED_CRITICAL", 83);
            put("CRITICAL_CLEARED", 82);

            put("CRITICAL_MAJOR", 79);
            put("MAJOR_MAJOR", 78);
            put("MINOR_MAJOR", 77);
            put("WARNING_MAJOR", 76);
            put("INDETERMINATE_MAJOR", 75);
            put("CLEARED_MAJOR", 74);
            put("UNDEFINED_MAJOR", 73);
            put("MAJOR_CLEARED", 72);

            put("CRITICAL_MINOR", 69);
            put("MAJOR_MINOR", 68);
            put("MINOR_MINOR", 67);
            put("WARNING_MINOR", 66);
            put("INDETERMINATE_MINOR", 65);
            put("CLEARED_MINOR", 64);
            put("UNDEFINED_MINOR", 63);
            put("MINOR_CLEARED", 62);

            put("CRITICAL_WARNING", 59);
            put("MAJOR_WARNING", 58);
            put("MINOR_WARNING", 57);
            put("WARNING_WARNING", 56);
            put("INDETERMINATE_WARNING", 55);
            put("CLEARED_WARNING", 54);
            put("UNDEFINED_WARNING", 53);
            put("WARNING_CLEARED", 52);

            put("CRITICAL_INDETERMINATE", 49);
            put("MAJOR_INDETERMINATE", 48);
            put("MINOR_INDETERMINATE", 47);
            put("WARNING_INDETERMINATE", 46);
            put("INDETERMINATE_INDETERMINATE", 45);
            put("CLEARED_INDETERMINATE", 44);
            put("UNDEFINED_INDETERMINATE", 43);
            put("INDETERMINATE_CLEARED", 42);

            put("CRITICAL_UNDEFINED", 39);
            put("MAJOR_UNDEFINED", 38);
            put("MINOR_UNDEFINED", 37);
            put("WARNING_UNDEFINED", 36);
            put("INDETERMINATE_UNDEFINED", 35);
            put("CLEARED_UNDEFINED", 34);
            put("UNDEFINED_UNDEFINED", 33);
            put("UNDEFINED_CLEARED", 32);
            put("CLEARED_CLEARED", 31);
        }
    });

}
