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

package com.ericsson.oss.services.fm.alarmqueryservice.api.common;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.COLON_DELIMITER;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EMPTY_STRING;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.EQUAL_DELIMITER;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.HASH_DELIMITER;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.UNDEFINED;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An helper class, which supports AlarmRecord in lazy loading of attributes and in conversion of attribute into respective types.
 **/
public class AlarmAttributeConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmAttributeConverter.class);
    private static final String ESCAPE_SEQUENCE_FOR_HASH = "¡¿§";

    private AlarmAttributeConverter() {

    }

    /**
     * These methods were introduced to avoid NullPointerException when Casting a null object.
     **/
    static String getStringAttributeValue(final String attribute, final Object attributeValue) {
        if (attributeValue != null) {
            return (String) attributeValue;
        } else {
            return null;
        }
    }

    static Long getLongAttributeValue(final String attribute, final Object attributeValue) {
        if (attributeValue != null) {
            return ((Number) attributeValue).longValue();
        } else {
            return 0L;
        }
    }

    static Integer getIntegerAttributeValue(final String attribute, final Object attributeValue) {
        if (attributeValue != null) {
            return ((Number) attributeValue).intValue();
        } else {
            return 0;
        }
    }

    static Date getDateAttributeValue(final String attribute, final Object attributeValue) {
        if (attributeValue != null) {
            if (attributeValue instanceof String) {
                try {
                    final Calendar cal = DatatypeConverter.parseDateTime((String) attributeValue);
                    cal.setTimeZone(TimeZone.getTimeZone("UTC"));
                    return cal.getTime();
                } catch (final Exception ex) {
                    LOGGER.error("ParseException caught while converting from string to date: cause ", ex);
                    return null;
                }
            } else if (attributeValue instanceof Date) {
                return (Date) attributeValue;
            } else {
                LOGGER.warn("unrecognized attribute {} value format, neither String or Date, it is {}!", attribute, attributeValue.getClass());
                return null;
            }
        } else {
            return null;
        }
    }

    static Object getEnumAttributeValue(final String attribute, final Object attributeValue) {
        if (attributeValue != null) {
            return attributeValue;
        } else {
            return UNDEFINED;
        }
    }

    static Boolean getBooleanAttributeValue(final String attribute, final Object attributeValue) {
        if (attributeValue != null) {
            return (Boolean) attributeValue;
        } else {
            return null;
        }
    }

    /**
     * Method returns the NE Name of a node.
     *
     * @param fdn
     *         - fdn of a node or null.
     * @return example : fdn = "NetworkElement=LTE01ERBS0001"; <br>
     * this method returns LTE01ERBS0001;
     */

    static String getNeName(final Object fdn) {
        String neName = EMPTY_STRING;
        if (fdn != null) {
            final String fdnAsString = fdn.toString();
            if (fdnAsString.contains(EQUAL_DELIMITER)) {
                neName = fdnAsString.split(EQUAL_DELIMITER)[1];
            } else {
                neName = fdnAsString;
            }
        }
        return neName;
    }

    public static Map<String, String> convertAdditionalInfoToMap(final String additionalInfo) {
        LOGGER.trace("Converting Additional Attribute Info of an alarm {} to Map.", additionalInfo);
        final Map<String, String> additionalAttributeMap = new HashMap<String, String>();
        if (additionalInfo != null && additionalInfo.length() > 0) {
            final String[] attributes = additionalInfo.split(HASH_DELIMITER);
            for (final String string : attributes) {
                // Splits string into key and value .This holds good even in case of value containing ":"
                final String[] keyValue = string.split(COLON_DELIMITER, 2);
                if (keyValue.length == 1) {
                    additionalAttributeMap.put(keyValue[0], null);
                } else {
                    final String value = replaceEscapeSequenceWithHashValue(keyValue[1]);
                    additionalAttributeMap.put(keyValue[0], value);
                }
            }
        }
        return additionalAttributeMap;
    }

    private static String replaceEscapeSequenceWithHashValue(final String value) {
        String result = value;
        if (result.contains(ESCAPE_SEQUENCE_FOR_HASH)) {
            result = result.replace(ESCAPE_SEQUENCE_FOR_HASH, HASH_DELIMITER);
        }
        return result;
    }
}
