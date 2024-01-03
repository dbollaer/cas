package org.apereo.cas.aup;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.config.CasAcceptableUsagePolicyJdbcConfiguration;
import org.apereo.cas.config.CasAcceptableUsagePolicyWebflowConfiguration;
import org.apereo.cas.config.CasAuthenticationEventExecutionPlanTestConfiguration;
import org.apereo.cas.config.CasCookieAutoConfiguration;
import org.apereo.cas.config.CasCoreAuditAutoConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationAutoConfiguration;
import org.apereo.cas.config.CasCoreAutoConfiguration;
import org.apereo.cas.config.CasCoreLogoutAutoConfiguration;
import org.apereo.cas.config.CasCoreMultifactorAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreNotificationsAutoConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketsAutoConfiguration;
import org.apereo.cas.config.CasCoreUtilAutoConfiguration;
import org.apereo.cas.config.CasCoreWebAutoConfiguration;
import org.apereo.cas.config.CasMultifactorAuthenticationWebflowAutoConfiguration;
import org.apereo.cas.config.CasPersonDirectoryTestConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.config.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.config.CasWebflowAutoConfiguration;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.util.MockRequestContext;
import org.apereo.cas.web.support.WebUtils;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.transaction.support.TransactionOperations;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * This is {@link BaseJdbcAcceptableUsagePolicyRepositoryTests}.
 *
 * @author Martin Böhmer
 * @since 5.3.8
 */
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    WebMvcAutoConfiguration.class,
    CasCoreWebAutoConfiguration.class,
    CasCookieAutoConfiguration.class,
    CasRegisteredServicesTestConfiguration.class,
    CasWebApplicationServiceFactoryConfiguration.class,
    CasAuthenticationEventExecutionPlanTestConfiguration.class,
    CasCoreTicketsAutoConfiguration.class,
    CasCoreAuthenticationAutoConfiguration.class,
    CasAcceptableUsagePolicyJdbcConfiguration.class,
    CasAcceptableUsagePolicyWebflowConfiguration.class,
    CasPersonDirectoryTestConfiguration.class,
    CasCoreUtilAutoConfiguration.class,
    CasWebflowAutoConfiguration.class,
    CasCoreMultifactorAuthenticationConfiguration.class,
    CasMultifactorAuthenticationWebflowAutoConfiguration.class,
    CasCoreAutoConfiguration.class,
    CasCoreLogoutAutoConfiguration.class,
    CasCoreNotificationsAutoConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasCoreAuditAutoConfiguration.class
})
public abstract class BaseJdbcAcceptableUsagePolicyRepositoryTests extends BaseAcceptableUsagePolicyRepositoryTests {
    @Autowired
    @Qualifier("acceptableUsagePolicyDataSource")
    protected DataSource acceptableUsagePolicyDataSource;

    @Autowired
    @Qualifier(AcceptableUsagePolicyRepository.BEAN_NAME)
    @Getter
    protected AcceptableUsagePolicyRepository acceptableUsagePolicyRepository;

    @Autowired
    @Qualifier(TicketRegistrySupport.BEAN_NAME)
    protected TicketRegistrySupport ticketRegistrySupport;

    @Autowired
    @Qualifier("jdbcAcceptableUsagePolicyTransactionTemplate")
    protected TransactionOperations jdbcAcceptableUsagePolicyTransactionTemplate;

    protected String determinePrincipalId(final String actualPrincipalId,
                                          final Map<String, List<Object>> profileAttributes) throws Exception {
        val aupProperties = casProperties.getAcceptableUsagePolicy();
        val jdbcAupRepository = new JdbcAcceptableUsagePolicyRepository(
            ticketRegistrySupport,
            aupProperties, acceptableUsagePolicyDataSource,
            jdbcAcceptableUsagePolicyTransactionTemplate);

        val context = MockRequestContext.create();
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(actualPrincipalId);
        val principal = CoreAuthenticationTestUtils.getPrincipal(c.getId(), profileAttributes);
        val auth = CoreAuthenticationTestUtils.getAuthentication(principal);
        WebUtils.putAuthentication(auth, context);
        return jdbcAupRepository.determinePrincipalId(principal);
    }

    @Override
    public boolean hasLiveUpdates() {
        return false;
    }
}
