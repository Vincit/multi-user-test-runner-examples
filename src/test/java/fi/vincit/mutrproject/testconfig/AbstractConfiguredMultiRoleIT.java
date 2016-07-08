package fi.vincit.mutrproject.testconfig;


import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.runner.junit.framework.SpringMultiUserTestClassRunner;
import fi.vincit.mutrproject.Application;
import fi.vincit.mutrproject.config.SecurityConfig;
import fi.vincit.mutrproject.configuration.TestMultiUserConfig;
import fi.vincit.mutrproject.feature.user.UserService;
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
 * Basic general configuration class for example tests. Uses a basic
 * {@link TestMultiUserConfig} to configure how test class role string are
 * mapped to system roles.
 */
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@MultiUserTestConfig(
        runner = SpringMultiUserTestClassRunner.class,
        defaultException = AccessDeniedException.class)
@ContextConfiguration(classes = {Application.class, SecurityConfig.class})
@RunWith(MultiUserTestRunner.class)
public abstract class AbstractConfiguredMultiRoleIT {

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseUtil databaseUtil;

    @Autowired
    @MultiUserConfigClass
    public TestMultiUserConfig config;

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @After
    public void clear() {
        userService.logout();
        databaseUtil.clearDb();
    }

    public TestMultiUserConfig config() {
        return config;
    }

    public AuthorizationRule authorization() {
        return authorizationRule;
    }

}
