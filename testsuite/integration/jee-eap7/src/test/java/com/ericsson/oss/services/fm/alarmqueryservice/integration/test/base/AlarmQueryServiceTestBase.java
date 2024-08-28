package com.ericsson.oss.services.fm.alarmqueryservice.integration.test.base;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.SecurityPrivilegeServiceMock;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.AccessControlServiceMockImpl;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.EAccessControlBypassAllImpl;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.TargetGroupRegistry;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.EnumConstants;
import com.ericsson.oss.services.fm.alarmqueryservice.impl.constants.QueryConstants;
import com.ericsson.oss.services.fm.alarmqueryservice.integration.test.AlarmQueryServiceIT;
import com.ericsson.oss.services.fm.alarmqueryservice.integration.test.util.Artifact;
import com.ericsson.oss.services.fm.alarmqueryservice.integration.test.util.ServiceProxyProvider;
import com.ericsson.oss.services.fm.alarmqueryservice.integration.test.util.ServiceProxyProviderBean;

public class AlarmQueryServiceTestBase {

    public static WebArchive createTestArchive() {
        final WebArchive archive = ShrinkWrap.create(WebArchive.class, "AlarmQueryServiceTest.war");
        final JavaArchive testJar = ShrinkWrap.create(JavaArchive.class, "AlarmQueryServiceTest.jar")
                .add(new StringAsset("sdk_service_identifier=AlarmQueryServiceTest\nsdk_service_version=1.0.1"),
                        "ServiceFrameworkConfiguration.properties");

        archive.addClass(AlarmQueryServiceTestBase.class);
        archive.addAsLibraries(Artifact.resolveFiles(Artifact.COM_ERICSSON_OSS_SERVICES_ALARM_PERSISTENCE_MODEl_JAR));
        archive.addAsLibraries(Artifact.resolveFiles(Artifact.ALARM_QUERY_SERVICE_API));
        archive.addAsLibraries(Artifact.resolveFiles(Artifact.ACCESS_CONTROL_SERVICE_API));
        archive.addAsLibraries(Artifact.resolveFiles(Artifact.FM_COMMON_JAR));

        archive.addAsLibrary(testJar);
        archive.addClass(QueryConstants.class);
        archive.addClass(EnumConstants.class);
        archive.addClasses(ServiceProxyProvider.class, ServiceProxyProviderBean.class);
        archive.addClass(DummyDataCreator.class);
        archive.addClass(AlarmQueryServiceIT.class);
        archive.addClass(TargetGroupRegistry.class);
        archive.addClass(TestConstants.class);
        archive.addClass(AccessControlServiceMockImpl.class);
        archive.addClass(EAccessControlBypassAllImpl.class);
        archive.addClass(SecurityPrivilegeServiceMock.class);

        archive.addAsResource("META-INF/beans.xml", "META-INF/beans.xml");

        archive.setManifest(new StringAsset("Dependencies: com.ericsson.oss.itpf.datalayer.dps.api export\n"));

        return archive;
    }

}
