package com.ericsson.oss.itpf.sdk.security.accesscontrol.classic;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityAction;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityPrivilege;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityResource;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityRole;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecuritySubject;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityTarget;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityTargetGroup;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.spi.AccessControlSPI;

@Stateless
public class SecurityPrivilegeServiceMock implements AccessControlSPI {

    private static final Logger logger = LoggerFactory.getLogger(SecurityPrivilegeServiceMock.class);

    @Inject
    TargetGroupRegistry targetGroupRegistry;

    public static final String FM_USER = "FM-USER";
    public static final ESecurityTargetGroup SEC_TARGET_GROUP_1 = new ESecurityTargetGroup("tg1");
    public static final ESecurityTargetGroup SEC_TARGET_GROUP_2 = new ESecurityTargetGroup("tg2");

    @Override
    public Set<ESecurityPrivilege> getPrivilegesForSubject(final ESecuritySubject eSecuritySubject) {
        final Set<ESecurityPrivilege> subjectPrivileges = new HashSet<>();
        if (FM_USER.toLowerCase().equals(eSecuritySubject.getSubjectId())) {
            subjectPrivileges.add(new ESecurityPrivilege(SEC_TARGET_GROUP_1, new ESecurityRole("")));
        }
        logger.warn("************************************************************");
        logger.warn("AccessControlSPIImplMock IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlSPIImplMock: getPrivilegesForSubject {} {}", eSecuritySubject.getSubjectId(), subjectPrivileges.size());
        logger.warn("************************************************************");
        return subjectPrivileges;
    }

    @Override
    public Set<ESecurityTargetGroup> getGroupsForTarget(final ESecurityTarget eSecurityTarget) {
        final Set<ESecurityTargetGroup> targetForGroups = targetGroupRegistry.getTargetGroupsForTarget(eSecurityTarget.getName());
        logger.warn("************************************************************");
        logger.warn("AccessControlSPIImplMock IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlSPIImplMock: getGroupsForTarget {} {}", eSecurityTarget.getName(), targetForGroups.size());
        logger.warn("************************************************************");
        return targetForGroups;
    }

    @Override
    public Set<ESecurityTarget> getTargetsForSubject(final ESecuritySubject eSecuritySubject) {
        logger.warn("************************************************************");
        logger.warn("AccessControlSPIImplMock IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlSPIImplMock: getTargetsForSubject called.");
        logger.warn("************************************************************");
        return null;
    }

    @Override
    public Map<ESecurityResource, Set<ESecurityAction>> getActionsForResources(final ESecuritySubject eSecuritySubject,
                                                                               final Set<ESecurityResource> set) {
        logger.warn("************************************************************");
        logger.warn("AccessControlSPIImplMock IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlSPIImplMock: getActionsForResources called.");
        logger.warn("************************************************************");
        return null;
    }
}

