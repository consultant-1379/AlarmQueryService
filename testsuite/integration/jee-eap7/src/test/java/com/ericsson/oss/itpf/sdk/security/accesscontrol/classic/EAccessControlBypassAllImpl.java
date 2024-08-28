package com.ericsson.oss.itpf.sdk.security.accesscontrol.classic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.security.accesscontrol.EAccessControl;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.EPredefinedRole;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityAction;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityResource;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecuritySubject;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;

/* Implementation of EAccessControl interface for testing.
 */
public class EAccessControlBypassAllImpl extends EAccessControlImpl implements EAccessControl {
    private static final Logger logger = LoggerFactory.getLogger(EAccessControlBypassAllImpl.class);

    @Override
    public ESecuritySubject getAuthUserSubject() throws SecurityViolationException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: getAuthUserSubject called.");
        logger.warn("************************************************************");
        final String toruser = SecurityPrivilegeServiceMock.FM_USER;

        logger.info("AccessControlBypassAllImpl: getAuthUserSubject: toruser is <{}>", toruser);
        return new ESecuritySubject(toruser);
    }

    @Override
    public boolean isAuthorized(final ESecuritySubject secSubject, final ESecurityResource secResource, final ESecurityAction secAction,
                                final EPredefinedRole[] roles) throws SecurityViolationException, IllegalArgumentException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: isAuthorized 1 called");
        logger.warn("************************************************************");
        return true;
    }

    @Override
    public boolean isAuthorized(final ESecuritySubject secSubject, final ESecurityResource secResource, final ESecurityAction secAction)
            throws SecurityViolationException, IllegalArgumentException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: isAuthorized 2 called");
        logger.warn("************************************************************");
        return true;
    }

    @Override
    public boolean isAuthorized(final ESecurityResource secResource, final ESecurityAction secAction, final EPredefinedRole[] roles)
            throws SecurityViolationException, IllegalArgumentException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: isAuthorized 3 called");
        logger.warn("************************************************************");
        return true;
    }

    @Override
    public boolean isAuthorized(final ESecurityResource secResource, final ESecurityAction secAction) throws SecurityViolationException,
            IllegalArgumentException {
        logger.warn("************************************************************");
        logger.warn("AccessControlBypassAllImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("AccessControlBypassAllImpl: isAuthorized 4 called");
        logger.warn("************************************************************");
        return true;
    }

}