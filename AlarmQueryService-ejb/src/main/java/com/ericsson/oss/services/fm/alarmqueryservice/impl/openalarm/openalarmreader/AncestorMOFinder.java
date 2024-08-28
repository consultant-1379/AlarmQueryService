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

package com.ericsson.oss.services.fm.alarmqueryservice.impl.openalarm.openalarmreader;

import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.COMMA_DELIMITER;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.EMPTY_STRING;
import static com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants.EQUAL_DELIMITER;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.util.DPSProxy;

/**
 * Responsible for retrieving the poId of the ManagedObject<br>
 * ManagedObject will have fdn as objectOfReference of an alarm (input).
 *
 *
 */
public class AncestorMOFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AncestorMOFinder.class);

    @Inject
    private DPSProxy dpsProxy;

    /**
     * Retrieves the poId of ManagedObject with fully distinguish(fdn of MO) name as objectOfReference. <br>
     *
     * Eg : If objectOfReference is MeContext=LTE01ERBS06,ManagedElement=1,cell=4
     *
     * this method returns poId of MO having fdn as MeContect=LTE01ERBS06,ManagedElement=1,cell=4 if it found in DB.
     *
     * If the object of reference is received has wrong format(with out key=value format separated by ,), Ex:
     * <b>MeContect=LTE01ERBS06,ManagedElement=1,cell=4,Cabinet</b> , The object of reference substring till proper key=value pair is considered. In
     * this case <b>MeContect=LTE01ERBS06,ManagedElement=1,cell=4</b> is considered.
     *
     * @param objectOfReference
     *            -- objectOfReference of alarm.
     * @return -- PoId of MO
     */
    public String find(final String objectOfReference) {
        String nodeId = EMPTY_STRING;
        ManagedObject managedObject = null;
        String properOor = objectOfReference;
        final int eqIndex = objectOfReference.lastIndexOf(EQUAL_DELIMITER);
        int lastIndexOfComa = 0;
        if (eqIndex > 0) {
            lastIndexOfComa = objectOfReference.indexOf(COMMA_DELIMITER, eqIndex);
            if (lastIndexOfComa > 0) {
                properOor = objectOfReference.substring(0, lastIndexOfComa);
            }
        }

        if (objectOfReference != null) {
            managedObject = getManagedObject(properOor);
            if (managedObject != null) {
                final Long poId = managedObject.getPoId();
                nodeId = poId.toString();
            } else {
                nodeId = findImmediateAncestor(properOor);
            }
        }
        return nodeId;
    }

    /**
     * Retrieves the poId of ManagedObject with fully distinguish(fdn of MO) name as objectOfReference.<br>
     * This method returns poId of MO having fdn as objectOfReference if it found in DB,<br>
     * if not method will upgrade by one level in hierarchy and checks. <br>
     * Process repeats until valid mo found.
     * <p>
     * Eg : If objectOfReference is MeContect=LTE01ERBS06,ManagedElement=1,cell=4
     *
     * This method returns poId of MO having fdn as MeContect=LTE01ERBS06,ManagedElement=1,cell=4 if it found in DB.<br>
     * if not we will check for MeContect=LTE01ERBS06,ManagedElement=1 if found returns PoId.
     *
     * @param objectOfReference
     *            -- objectOfReference of alarm.
     * @return -- PoId of MO
     */
    private String findImmediateAncestor(final String objectOfReference) {
        String finalResult = objectOfReference;
        String nodeId = EMPTY_STRING;
        ManagedObject managedObject = null;
        while (finalResult != null && (managedObject == null)) {
            final int lastIndex = finalResult.lastIndexOf(COMMA_DELIMITER);
            if (lastIndex > 0) {
                finalResult = finalResult.substring(0, lastIndex);
                managedObject = getManagedObject(finalResult);
            } else {
                finalResult = null;
            }
        }
        if (managedObject != null) {
            final Long poId = managedObject.getPoId();
            nodeId = poId.toString();
        }
        return nodeId;
    }

    /**
     * As DPS does not accept invalid value of MO, need to check if correct type of attribute is passed while checking findMoByFdn() call. <br>
     * Ex:- 1. Invalid Attribute ("ENM") , Valid attribute ("ENM=123") <br>
     * 2. Invalid Attribute ("Ultracell") , Valid attribute ("Ultracell=1") <br>
     *
     * @param oor
     * @return
     */
    private ManagedObject getManagedObject(final String oor) {
        ManagedObject managedObject = null;
        try {
            if (oor.contains(EQUAL_DELIMITER)) {
                managedObject = dpsProxy.getLiveBucket().findMoByFdn(oor);
            }
        } catch (final Exception ex) {
            LOGGER.error("Failed to get Managed object instance for OOR: {} due to {}", oor, ex.getMessage());
        }
        return managedObject;
    }
}
