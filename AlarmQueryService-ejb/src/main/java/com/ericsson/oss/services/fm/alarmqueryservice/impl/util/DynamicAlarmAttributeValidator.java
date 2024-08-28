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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.util;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmqueryservice.api.criteria.AlarmAttributeCriteria;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.cache.FmxAdditionalAttributesDataHandler;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader.OpenAlarmParser;
import com.ericsson.oss.services.fm.models.FmxAddtionalAttributesRecord;

/**
 * Class validates if the sort attribute and filter criteria is fmx additional attribute or not.
 */
public class DynamicAlarmAttributeValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicAlarmAttributeValidator.class);

    @Inject
    private FmxAdditionalAttributesDataHandler fmxAdditionalAttributesDataHandler;

    @Inject
    private OpenAlarmParser openAlarmParser;

    /**
     * Method takes {@link AlarmAttributeCriteria} criteria and checks whether it is additional attribute criteria or not.
     *
     * @param alarmAttributeCriteria
     *            - alarm attribute filter criteria list
     * @return true/false
     * @throws Exception
     */
    public boolean isFmxAdditionalAttributeCriteria(final List<AlarmAttributeCriteria> alarmAttributeCriteria) throws Exception {
        boolean isFmxAdditionalAttribute = false;
        // Validate for Search criteria
        if (null != alarmAttributeCriteria && !alarmAttributeCriteria.isEmpty()) {
            for (final AlarmAttributeCriteria alarmSearchAttribute : alarmAttributeCriteria) {
                final String attribute = alarmSearchAttribute.getAttributeName();
                final List<String> attributeList = new ArrayList<String>();
                attributeList.add(attribute);

                if (!filterFmxAdditionalAttributes(attributeList).isEmpty()) {
                    isFmxAdditionalAttribute = true;
                    break;
                }
            }
        }
        return isFmxAdditionalAttribute;
    }

    /**
     * Method takes list of attribute names and filter the FMX attributes present in the list.
     *
     * @param attributeNames
     *            List of attribute names.
     * @return list of FMX additional attributes if present any, otherwise empty list.
     * @throws Exception
     */
    public List<String> filterFmxAdditionalAttributes(final List<String> attributeNames) throws Exception {

        final List<String> dynamicAttributeNames = new ArrayList<String>();

        for (final String additionalAttribute : attributeNames) {
            if (!openAlarmParser.getAllalarmAttributes().contains(additionalAttribute)) {
                final FmxAddtionalAttributesRecord enabledAddtionalAttributesRecord = fmxAdditionalAttributesDataHandler.getEnabledAttributesRecord();
                final FmxAddtionalAttributesRecord disabledAddtionalAttributesRecord = fmxAdditionalAttributesDataHandler
                        .getDisabledAttributesRecord();
                final List<String> enabledAttributes = getAdditionalAtrributes(enabledAddtionalAttributesRecord);
                final List<String> disabledAttributes = getAdditionalAtrributes(disabledAddtionalAttributesRecord);

                LOGGER.debug("Enabled additional attributes {} and disabled additional attributes {} ", enabledAttributes, disabledAttributes);

                if (enabledAttributes.contains(additionalAttribute)) {
                    dynamicAttributeNames.add(additionalAttribute);
                } else if (disabledAttributes.contains(additionalAttribute)) {
                    throw new Exception("Invalid sort/search attribute");
                }
            }
        }
        return dynamicAttributeNames;
    }

    private List<String> getAdditionalAtrributes(final FmxAddtionalAttributesRecord fmxAddtionalAttributesRecord) {
        final List<String> additionalAttributes = new ArrayList<String>();
        if (fmxAddtionalAttributesRecord != null && fmxAddtionalAttributesRecord.getAdditionalAttibuteList() != null) {
            additionalAttributes.addAll(fmxAddtionalAttributesRecord.getAdditionalAttibuteList());
        }
        return additionalAttributes;
    }
}