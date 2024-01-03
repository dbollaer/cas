package org.apereo.cas.authentication.surrogate;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.config.CasCookieAutoConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationAutoConfiguration;
import org.apereo.cas.config.CasCoreAutoConfiguration;
import org.apereo.cas.config.CasCoreLogoutAutoConfiguration;
import org.apereo.cas.config.CasCoreMultifactorAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreNotificationsAutoConfiguration;
import org.apereo.cas.config.CasCoreRestAutoConfiguration;
import org.apereo.cas.config.CasCoreServicesAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketsAutoConfiguration;
import org.apereo.cas.config.CasCoreUtilAutoConfiguration;
import org.apereo.cas.config.CasCoreWebAutoConfiguration;
import org.apereo.cas.config.CasMultifactorAuthenticationWebflowAutoConfiguration;
import org.apereo.cas.config.CasPersonDirectoryTestConfiguration;
import org.apereo.cas.config.CasThemesConfiguration;
import org.apereo.cas.config.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.config.CasWebflowAutoConfiguration;
import org.apereo.cas.config.SurrogateAuthenticationAuditConfiguration;
import org.apereo.cas.config.SurrogateAuthenticationConfiguration;
import org.apereo.cas.config.SurrogateAuthenticationMetadataConfiguration;
import org.apereo.cas.config.SurrogateAuthenticationRestConfiguration;
import org.apereo.cas.config.SurrogateComponentSerializationConfiguration;
import org.apereo.cas.services.ServicesManager;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Import;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link BaseSurrogateAuthenticationServiceTests}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
public abstract class BaseSurrogateAuthenticationServiceTests {
    public static final String BANDERSON = "banderson";

    public static final String ADMIN = "casadmin";

    @Mock
    protected ServicesManager servicesManager;

    public abstract SurrogateAuthenticationService getService();

    @Test
    void verifyUserAllowedToProxy() throws Throwable {
        assertFalse(getService().getImpersonationAccounts(getTestUser()).isEmpty());
    }

    @Test
    void verifyUserNotAllowedToProxy() throws Throwable {
        assertTrue(getService().getImpersonationAccounts("unknown-user").isEmpty());
    }

    @Test
    void verifyProxying() throws Throwable {
        val service = Optional.of(CoreAuthenticationTestUtils.getService());
        val surrogateService = getService();
        assertTrue(surrogateService.canImpersonate(BANDERSON, CoreAuthenticationTestUtils.getPrincipal(getTestUser()), service));
        assertTrue(surrogateService.canImpersonate(BANDERSON, CoreAuthenticationTestUtils.getPrincipal(BANDERSON), service));
        assertFalse(surrogateService.canImpersonate("XXXX", CoreAuthenticationTestUtils.getPrincipal(getTestUser()), service));
        assertFalse(surrogateService.canImpersonate(getTestUser(), CoreAuthenticationTestUtils.getPrincipal(BANDERSON), service));
    }

    @Test
    void verifyWildcard() throws Throwable {
        val service = Optional.of(CoreAuthenticationTestUtils.getService());
        val admin = CoreAuthenticationTestUtils.getPrincipal(getAdminUser());
        assertTrue(getService().canImpersonate(BANDERSON, admin, service));
        assertTrue(getService().isWildcardedAccount(BANDERSON, admin));
    }

    public String getAdminUser() {
        return ADMIN;
    }

    public String getTestUser() {
        return "casuser";
    }

    @ImportAutoConfiguration({
        RefreshAutoConfiguration.class,
        MailSenderAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        AopAutoConfiguration.class
    })
    @SpringBootConfiguration
    @Import({
        SurrogateAuthenticationConfiguration.class,
        SurrogateComponentSerializationConfiguration.class,
        SurrogateAuthenticationAuditConfiguration.class,
        SurrogateAuthenticationMetadataConfiguration.class,
        SurrogateAuthenticationRestConfiguration.class,
        CasCoreRestAutoConfiguration.class,
        CasCoreAuthenticationAutoConfiguration.class,
        CasWebApplicationServiceFactoryConfiguration.class,
        CasCoreUtilAutoConfiguration.class,
        CasPersonDirectoryTestConfiguration.class,
        CasCoreWebAutoConfiguration.class,
        CasCoreServicesAuthenticationConfiguration.class,
        CasCoreServicesConfiguration.class,
        CasWebflowAutoConfiguration.class,
        CasCoreUtilAutoConfiguration.class,
        CasCoreMultifactorAuthenticationConfiguration.class,
        CasMultifactorAuthenticationWebflowAutoConfiguration.class,
        CasCoreAutoConfiguration.class,
        CasCoreLogoutAutoConfiguration.class,
        CasCookieAutoConfiguration.class,
        CasThemesConfiguration.class,
        CasCoreNotificationsAutoConfiguration.class,
        CasCoreTicketsAutoConfiguration.class
    })
    public static class SharedTestConfiguration {
    }
}
