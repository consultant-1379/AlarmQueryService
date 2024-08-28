package com.ericsson.oss.services.fm.alarmqueryservice.integration.test.util;

import javax.ejb.Local;

import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.TargetGroupRegistry;
import com.ericsson.oss.services.fm.alarmqueryservice.api.AlarmQueryService;

@Local
public interface ServiceProxyProvider {

    // Proxy (wrapper) required for resolving EService References

    DataPersistenceService getDataPersistenceService();

    com.ericsson.oss.services.alarm.query.service.api.AlarmQueryService getOldAlarmQueryService();

    AlarmQueryService getAlarmQueryService();

    TargetGroupRegistry getTargetGroupRegistry();
}
