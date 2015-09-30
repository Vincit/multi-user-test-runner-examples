package fi.vincit.mutrproject.configuration;


import static com.jayway.restassured.RestAssured.given;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.runner.junit.framework.SpringMultiUserTestClassRunner;
import fi.vincit.multiusertest.test.AbstractUserRoleIT;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.Application;
import fi.vincit.mutrproject.config.SecurityConfig;
import fi.vincit.mutrproject.domain.Role;
import fi.vincit.mutrproject.domain.User;
import fi.vincit.mutrproject.service.UserService;

/**
 * Example of basic configuration for Spring projects.
 */
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@MultiUserTestConfig(
        runner = SpringMultiUserTestClassRunner.class,
        defaultException = AccessDeniedException.class)
@SpringApplicationConfiguration(classes = {Application.class, SecurityConfig.class})
@WebAppConfiguration
@IntegrationTest("server.port:0")
@RunWith(MultiUserTestRunner.class)
public abstract class AbstractConfiguredRestAssuredIT extends AbstractUserRoleIT<User, Role> {

    @Value("${local.server.port}")
    private int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @After
    public void clear() {
        userService.clearUsers();
    }

    @Autowired
    private UserService userService;

    private String username;
    private String password;

    @Override
    protected void loginWithUser(User user) {
        username = user.getUsername();
        password = user.getUsername();
    }

    protected RequestSpecification whenAuthenticated() {
        return given().auth().preemptive().basic(username, password).header("Content-Type", "application/json");
    }

    @Override
    protected User createUser(String username, String firstName, String lastName, Role userRole, LoginRole loginRole) {
        String password = username;
        return userService.createUser(username, password, userRole);
    }

    @Override
    protected Role stringToRole(String role) {
        return Role.valueOf(role);
    }

    @Override
    protected User getUserByUsername(String username) {
        return userService.loadUserByUsername(username);
    }
}
