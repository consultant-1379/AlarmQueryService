/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import com.ericsson.oss.services.fm.alarmqueryservice.integration.test.util.Artifact;

public class Deployments {

    public static EnterpriseArchive createEnterpriseArchiveDeployment(final String artifactName) {
        final EnterpriseArchive ear = ShrinkWrap
                .createFromZipFile(EnterpriseArchive.class, Artifact.resolveArtifactWithoutDependencies(artifactName));

        return ear;
    }

}
