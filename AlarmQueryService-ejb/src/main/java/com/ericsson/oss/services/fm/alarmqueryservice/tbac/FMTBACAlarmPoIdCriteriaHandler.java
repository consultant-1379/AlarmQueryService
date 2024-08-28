/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmqueryservice.tbac;

import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.ADDITIONAL_INFORMATION;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.FDN;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.NETWORK_ELEMENT_DELIMITER;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.OBJECT_OF_REFERENCE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.PROBABLE_CAUSE;
import static com.ericsson.oss.services.fm.alarmqueryservice.api.common.Constants.SPECIFIC_PROBLEM;
import static com.ericsson.oss.services.fm.common.tbac.FMTBACConstants.FMTBAC_ERROR;
import static com.ericsson.oss.services.fm.common.tbac.FMTBACConstants.INSUFFICIENT_ACCESS_RIGHTS_ERROR_MSG;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmAttributeResponse;
import com.ericsson.oss.services.fm.alarmqueryservice.api.common.AlarmRecord;
import com.ericsson.oss.services.fm.common.tbac.FMTBACAccessControl;
import com.ericsson.oss.services.fm.common.tbac.FMTBACHandler;
import com.ericsson.oss.services.fm.common.tbac.FMTBACParamHandler;

/**
 * TBAC handler.
 */
@FMTBACHandler(handlerId = "FMTBACAlarmPoIdCriteriaHandler")
public class FMTBACAlarmPoIdCriteriaHandler implements FMTBACParamHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FMTBACAlarmPoIdCriteriaHandler.class);

    @Override
    public Object postProcess(final FMTBACAccessControl accessControl, final Object response) {
        LOGGER.debug("PostProcess with and alarmPoIdCriteria {} ", response);
        final AlarmAttributeResponse alarmAttributeResponse = (AlarmAttributeResponse) response;
        final Iterator<AlarmRecord> iterator = alarmAttributeResponse.getAlarmRecords().iterator();
        final List<AlarmRecord> tbacAlarmRecordList = new ArrayList<>();
        final Set<String> tbacFdnSet = new HashSet<>();
        while (iterator.hasNext()) {
            final AlarmRecord alarmRecord = iterator.next();
            final String fdn = alarmRecord.getFdn();
            if (fdn != null) {
                final boolean isTbacFdn = tbacFdnSet.contains(fdn);
                if (isTbacFdn || (!accessControl.isAuthorizedFromFdn(fdn))) {
                    tbacFdnSet.add(fdn);
                    iterator.remove();
                    tbacAlarmRecordList.add(changeInTbacAlarmRecord(alarmRecord));
                    LOGGER.debug("AlarmRecord with poId: {} and fdn: {} is not autorized", alarmRecord.getEventPoIdAsString(), fdn);
                }
            }
        }
        alarmAttributeResponse.getAlarmRecords().addAll(tbacAlarmRecordList);
        return alarmAttributeResponse;
    }

    @Override
    public boolean preProcess(final FMTBACAccessControl accessControl, final Object paramList) {
        // No filtering with pre processing ...
        return true;
    }

    private AlarmRecord changeInTbacAlarmRecord(final AlarmRecord alarmRecord) {
        try {
            final Field field = AlarmRecord.class.getDeclaredField("attributeMap");
            field.setAccessible(true);
            Map<String, Object> alarmAttributeMap = alarmAttributeMap = (Map<String, Object>) field.get(alarmRecord);
            alarmAttributeMap.put(FDN, NETWORK_ELEMENT_DELIMITER + FMTBAC_ERROR);
            alarmAttributeMap.put(OBJECT_OF_REFERENCE, FMTBAC_ERROR);
            alarmAttributeMap.put(SPECIFIC_PROBLEM, FMTBAC_ERROR);
            alarmAttributeMap.put(PROBABLE_CAUSE, FMTBAC_ERROR);
            alarmAttributeMap.put(ADDITIONAL_INFORMATION, FMTBAC_ERROR + ":" + INSUFFICIENT_ACCESS_RIGHTS_ERROR_MSG);
        } catch (final NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            LOGGER.debug("Tbac Error with the changing of Alarm Record ", e);
        }
        return alarmRecord;
    }

}
