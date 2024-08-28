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

package com.ericsson.oss.itpf.sdk.security.accesscontrol.classic;

import javax.ejb.Stateless;
import java.util.Map;
import java.util.Set;

import com.ericsson.oss.services.security.accesscontrol.AccessControlService;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityAction;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecurityResource;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.ESecuritySubject;

/**
 * Access Control Service remote interface mock implementation.
 *
 */
@Stateless
public class AccessControlServiceMockImpl implements AccessControlService {

    /**
     * This method is used to know if the String has TBAC restrictions active.
     * This depends if that user hasn't the TG ALL assigned.
     * The method, returns true if the user hasn't the TG ALL assigned. false otherwise
     *
     * @param subject
     *            the name of the user
     * @return true if the ESecuritySubject has the TBAC restriction active.
     */
    @Override
    public boolean isTBACRestrictedForSubject(final String subject) {
        if (subject != null) {
            return true;
        }
        return false;
    }

    /**
     * Returns the list of role names for the user
     * If user is null, the user name is retrieved from the JEE context.
     *
     * @param user
     *            the name of user
     * @return the list of role names
     */
    @Override
    public Set<String> getUserRoles(final String s) {
        return null;
    }

    /**
     * Returns the list of capabilities for the user
     *
     * @param resources
     *            set of resource
     * @param user
     *            the name of user
     * @return the list of capabilities
     */
    @Override
    public Map<ESecurityResource, Set<ESecurityAction>> getCapabilitiesForSubject(final ESecuritySubject eSecuritySubject,
                                                                                  final Set<ESecurityResource> set) {
        return null;
    }

    /**
     * Internal Method :returns the list of capabilities for the user
     *
     * @param resources
     *            set of resource
     * @param user
     *            the name of user
     * @return the list of capabilities
     */
    @Override
    public Map<ESecurityResource, Set<ESecurityAction>> getCapabilitiesForSubjectWithAuthorize(final ESecuritySubject eSecuritySubject,
                                                                                               final Set<ESecurityResource> set) {
        return null;
    }
}
