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

package com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base;

public final class TestConstants {
    public static final String ME_CONTEXT = "MeContext";
    public static final String ERBS_NODE_MODEL = "ERBS_NODE_MODEL";

    public static final String ME_CONTEXT_PREFIX = "MeContext=";
    public static final String NETWORK_ELEMENT_PREFIX = "NetworkElement=";
    public static final String FM_ALARM_SUPERVISION = ",FmAlarmSupervision=1";
    public static final String FM_FUNCION_PREFIX = ",FmFunction=1";

    public static final String OSS_TOP = "OSS_TOP";
    public static final String OSS_NE_CM_DEF = "OSS_NE_CM_DEF";
    public static final String OSS_NE_DEF = "OSS_NE_DEF";
    public static final String OSS_NE_FM_DEF = "OSS_NE_FM_DEF";
    public static final String CPP_MED = "CPP_MED";
    public static final String MEDIATION = "MEDIATION";

    public static final String OSS_TOP_VERSION = "3.0.0";
    public static final String OSS_NE_DEF_VERSION = "2.0.0";
    public static final String DEFAULT_VERSION = "1.0.0";

    public static final String ERBS_NODE_TYPE = "ERBS";
    public static final String CPP_PLATFORM_TYPE = "CPP";

    public static final String TEST_NE_PREFIX = "AQS_ARQUILLIAN00";
    public static final String TEST_FDN = NETWORK_ELEMENT_PREFIX + TEST_NE_PREFIX + 25;
    public static final String TEST_FDN1 = NETWORK_ELEMENT_PREFIX + TEST_NE_PREFIX + 26;

    public static final String TEST_OOR = "MeContext=AQS_ARQUILLIAN0025,ManagedElement=1,ENodeBFunction=1";
    public static final String TEST_OOR1 = "MeContext=AQS_ARQUILLIAN0026,ManagedElement=1,ENodeBFunction=1";

    public static final String AQS_EAR = "AQS";
    public static final String AQS_TEST_WAR = "AQS_TEST";

    private TestConstants() {
    }
}
