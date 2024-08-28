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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.modelreader;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.ModelConstants.BOOLEAN_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.ModelConstants.ENUM_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.ModelConstants.INTEGER_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.ModelConstants.LONG_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.ModelConstants.OPEN_ALARM_URN;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.ModelConstants.STRING_TYPE;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.ModelConstants.TIMESTAMP_TYPE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.TypedModelAccess;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;

/**
 * Responsible for parsing OpenAlarm Model and building attribute list base on the types.
 *
 */
@ApplicationScoped
public class OpenAlarmParser {

    final List<String> integerTypeAttributes = new ArrayList<String>();
    final List<String> longTypeAttributes = new ArrayList<String>();
    final List<String> dateTypeAttributes = new ArrayList<String>();
    final List<String> booleanTypeAttributes = new ArrayList<String>();
    final List<String> stringTypeAttributes = new ArrayList<String>();
    final List<String> enumTypeAttributes = new ArrayList<String>();
    final List<String> alarmAttributes = new ArrayList<String>();
    final Map<String, String> alarmAttributesWithTypes = new HashMap<String, String>();
    boolean isAttributesLoaded = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAlarmParser.class);

    @Inject
    private ModelServiceHelper modelServiceHelper;

    /**
     * Method builds the attribute list based on the types, also builds the list of attribute from the model. <br>
     * <p>
     * Example model is:
     *
     * <?xml version="1.0" encoding="UTF-8" standalone="no"?> <ns2:primaryTypeAttribute immutable="false" key="false" mandatory="false"
     * ns1:lifeCycle="CURRENT" ns1:name="objectOfReference" sensitive="false"> <ns1:desc>objectOfReference</ns1:desc> <ns1:type
     * xsi:type="ns1:stringType"/> </ns2:primaryTypeAttribute> <ns2:primaryTypeAttribute immutable="false" key="false" mandatory="false"
     * ns1:lifeCycle="CURRENT" ns1:name="fdn" sensitive="false"> <ns1:desc>fdn</ns1:desc> <ns1:type xsi:type="ns1:stringType"/>
     * </ns2:primaryTypeAttribute> <ns2:primaryTypeAttribute immutable="false" key="false" mandatory="false" ns1:lifeCycle="CURRENT"
     * ns1:name="eventTime" sensitive="false"> <ns1:desc>eventTime</ns1:desc> <ns1:type xsi:type="ns1:timestampType"/> </ns2:primaryTypeAttribute>
     */
    public void extractAttributesFromModel() {
        try {
            final TypedModelAccess tma = modelServiceHelper.getTypeModelAccess();
            final PrimaryTypeSpecification eModelSpecification = tma.getEModelSpecification(OPEN_ALARM_URN, PrimaryTypeSpecification.class);
            final Collection<PrimaryTypeAttributeSpecification> attributeSpecifications = eModelSpecification.getAttributeSpecifications();
            for (final PrimaryTypeAttributeSpecification attributeSpecification : attributeSpecifications) {
                final String attributeName = attributeSpecification.getName();
                final DataType attributeDataType = attributeSpecification.getDataTypeSpecification().getDataType();

                alarmAttributes.add(attributeName);
                alarmAttributesWithTypes.put(attributeName, attributeDataType.toString());

                buildAttributeListBasedOnType(attributeName, attributeDataType.toString());
            }

            isAttributesLoaded = true;
            LOGGER.info("Attributes successfully loaded from OpenAlarm model : {} ", alarmAttributes);
        } catch (final Exception exception) {
            LOGGER.error("Error occured while parsing OpenAlarm Model :: {} ", exception);
        }
    }

    private void buildAttributeListBasedOnType(final String attributeName, final String attributeDataType) {
        switch (attributeDataType) {
            case ENUM_TYPE:
                enumTypeAttributes.add(attributeName);
                break;
            case TIMESTAMP_TYPE:
                dateTypeAttributes.add(attributeName);
                break;
            case BOOLEAN_TYPE:
                booleanTypeAttributes.add(attributeName);
                break;
            case LONG_TYPE:
                longTypeAttributes.add(attributeName);
                break;
            case STRING_TYPE:
                stringTypeAttributes.add(attributeName);
                break;
            case INTEGER_TYPE:
                integerTypeAttributes.add(attributeName);
                break;
            default:
                break;
        }
    }

    public boolean isAttributesLoaded() {
        return isAttributesLoaded;
    }

    public List<String> getIntegerTypeAttributes() {
        return integerTypeAttributes;
    }

    public List<String> getLongTypeAttributes() {
        return longTypeAttributes;
    }

    public List<String> getDateTypeAttributes() {
        return dateTypeAttributes;
    }

    public List<String> getBooleanTypeAttributes() {
        return booleanTypeAttributes;
    }

    public List<String> getStringTypeAttributes() {
        return stringTypeAttributes;
    }

    public List<String> getEnumTypeAttributes() {
        return enumTypeAttributes;
    }

    public List<String> getAllalarmAttributes() {
        return alarmAttributes;
    }
}
