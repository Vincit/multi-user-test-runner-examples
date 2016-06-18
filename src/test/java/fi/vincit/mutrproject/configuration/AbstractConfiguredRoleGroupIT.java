package fi.vincit.mutrproject.configuration;


import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.runner.junit.framework.SpringMultiUserTestClassRunner;
import fi.vincit.mutrproject.Application;
import fi.vincit.mutrproject.config.SecurityConfig;
import fi.vincit.mutrproject.util.DatabaseUtil;
import org.junit.After;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * Example on how to configure users with multiple roles. Uses a custom role
 * {@link RoleGroup} to configure which roles to configure for the user. RoleGroup
 * can be any enum or object that just defines all the combinations that need to
 * be tested.
 */
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@MultiUserTestConfig(
        runner = SpringMultiUserTestClassRunner.class,
        defaultException = AccessDeniedException.class)
@ContextConfiguration(classes = {Application.class, SecurityConfig.class})
@RunWith(MultiUserTestRunner.class)
public abstract class AbstractConfiguredRoleGroupIT {

    @Autowired
    private DatabaseUtil databaseUtil;

    @Autowired
    @MultiUserConfigClass
    public TestMultiRoleConfig config;

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    public TestMultiRoleConfig config() {
        return config;
    }

    public AuthorizationRule authorization() {
        return authorizationRule;
    }


    @After
    public void clear() {
        databaseUtil.clearDb();
    }

}
