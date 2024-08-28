package com.ericsson.oss.services.fm.alarmqueryservice.integration.test.util;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.TargetGroupRegistry;
import com.ericsson.oss.services.fm.alarmqueryservice.api.AlarmQueryService;

@ApplicationScoped
public class ServiceProxyProviderBean implements ServiceProxyProvider {

    @EServiceRef
    protected DataPersistenceService dataPersistenceService;

    @Override
    public DataPersistenceService getDataPersistenceService() {
        return dataPersistenceService;
    }

    @EServiceRef
    AlarmQueryService alarmQueryService;

    @Override
    public AlarmQueryService getAlarmQueryService() {
        return alarmQueryService;
    }

    @EServiceRef
    com.ericsson.oss.services.alarm.query.service.api.AlarmQueryService alarmQueryOldService;

    @Override
    public com.ericsson.oss.services.alarm.query.service.api.AlarmQueryService getOldAlarmQueryService() {
        return alarmQueryOldService;
    }

    @Inject
    private TargetGroupRegistry targetPIPSPIMock;

    @Override
    public TargetGroupRegistry getTargetGroupRegistry() {
        return targetPIPSPIMock;
    }
}
