package fi.vincit.mutrproject.component.feature.todo;

import com.jayway.restassured.response.Response;
import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.Application;
import fi.vincit.mutrproject.component.configuration.ComponentTestConfig;
import fi.vincit.mutrproject.component.configuration.RestAssuredHelper;
import fi.vincit.mutrproject.component.configuration.RestTestConfiguration;
import fi.vincit.mutrproject.config.SecurityConfig;
import fi.vincit.mutrproject.feature.todo.TodoService;
import fi.vincit.mutrproject.feature.todo.command.TodoItemCommand;
import fi.vincit.mutrproject.feature.todo.command.TodoListCommand;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import org.apache.http.HttpStatus;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.annotation.Resource;

import static fi.vincit.multiusertest.rule.expection.Expectations.valueOf;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * Example how to use existing users
 */
@RunWithUsers(
        producers = {"user:admin", "role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {"role:ROLE_ADMIN", "role:ROLE_USER", "user:user1",
                RunWithUsers.PRODUCER, RunWithUsers.ANONYMOUS}
)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@MultiUserTestConfig(
        defaultException = AccessDeniedException.class)
@ContextConfiguration(classes = {Application.class, SecurityConfig.class, ComponentTestConfig.class})
@RunWith(MultiUserTestRunner.class)
public class ComponentRestAssuredIT {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @Resource(name = "restTestConfiguration")
    @MultiUserConfigClass
    private RestTestConfiguration multiUserConfig;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();
    
    @Autowired
    private RestAssuredHelper restApi;


    @Before
    public void initUsers() {
        restApi.setUp();
        todoService.clearList();
        userService.createUser("admin", "admin", Role.ROLE_ADMIN);
        userService.createUser("user1", "user1", Role.ROLE_USER);
    }

    @After
    public void tearUp() {
        restApi.clear();
    }

    @Test
    public void getTodoLists() throws Throwable {
        restApi.whenAuthenticated()
                .body(new TodoListCommand("Test List 1", false)).post("/api/todo/list")
                .then().assertThat().statusCode(HttpStatus.SC_OK);
        restApi.whenAuthenticated()
                .body(new TodoListCommand("Test List 2", true)).post("/api/todo/list")
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        multiUserConfig.logInAs(LoginRole.CONSUMER);

        Response response = restApi.whenAuthenticated().get("/api/todo/lists");

        authorizationRule.expect(valueOf(response::then)
                .toAssert(t -> t.statusCode(HttpStatus.SC_OK)
                                .body("", hasSize(2))
                                .body("[0].name", equalTo("Test List 1"))
                                .body("[1].name", equalTo("Test List 2")),
                        ifAnyOf("role:ROLE_ADMIN", TestUsers.CREATOR))
                .toAssert(t -> t.statusCode(HttpStatus.SC_OK)
                                .body("", hasSize(1))
                                .body("[0].name", equalTo("Test List 2")),
                        ifAnyOf("role:ROLE_USER", "user:user1", TestUsers.ANONYMOUS)));
    }

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = restApi.whenAuthenticated()
                .body(new TodoListCommand("Test List", false)).post("/api/todo/list")
                .body().as(Long.class);

        multiUserConfig.logInAs(LoginRole.CONSUMER);

        Response response = restApi.whenAuthenticated().get("/api/todo/list/" + id);

        authorizationRule.expect(valueOf(response::then)
                .toAssert(t -> t.statusCode(HttpStatus.SC_OK)
                                .assertThat().body("name", equalTo("Test List")),
                        ifAnyOf("role:ROLE_ADMIN", TestUsers.CREATOR))
                .toAssert(t -> t.statusCode(HttpStatus.SC_FORBIDDEN),
                        ifAnyOf("role:ROLE_USER", "user:user1"))
                .toAssert(t -> t.statusCode(HttpStatus.SC_UNAUTHORIZED),
                        ifAnyOf(TestUsers.ANONYMOUS)));
    }

    @Test
    public void addItemToPrivateList() throws Throwable {
        long listId = restApi.whenAuthenticated()
                .body(new TodoListCommand("Test List", false)).post("/api/todo/list")
                .body().as(Long.class);

        multiUserConfig.logInAs(LoginRole.CONSUMER);

        Response response = restApi.whenAuthenticated()
                .body(new TodoItemCommand(listId, "Test List")).post("/api/todo/list/item");

        authorizationRule.expect(valueOf(response::then)
                .toAssert(t -> t.statusCode(HttpStatus.SC_OK),
                        ifAnyOf("role:ROLE_ADMIN", TestUsers.CREATOR))
                .toAssert(t -> t.statusCode(HttpStatus.SC_FORBIDDEN),
                        ifAnyOf("role:ROLE_USER", "user:user1"))
                .toAssert(t -> t.statusCode(HttpStatus.SC_UNAUTHORIZED),
                        ifAnyOf(TestUsers.ANONYMOUS)));
    }

    @Test
    public void addItemToPublicList() throws Throwable {
        long listId = restApi.whenAuthenticated()
                .body(new TodoListCommand("Test List", false)).post("/api/todo/list")
                .body().as(Long.class);

        multiUserConfig.logInAs(LoginRole.CONSUMER);

        Response response = restApi.whenAuthenticated()
                .body(new TodoItemCommand(listId, "Test List")).post("/api/todo/list/item");

        authorizationRule.expect(valueOf(response::then)
                .toAssert(t -> t.statusCode(HttpStatus.SC_OK),
                        ifAnyOf("role:ROLE_ADMIN", TestUsers.CREATOR))
                .toAssert(t -> t.statusCode(HttpStatus.SC_FORBIDDEN),
                        ifAnyOf("role:ROLE_USER", "user:user1"))
                .toAssert(t -> t.statusCode(HttpStatus.SC_UNAUTHORIZED),
                        ifAnyOf(TestUsers.ANONYMOUS)));
    }

    @Test
    public void setPrivateItemAsDone() throws Throwable {
        long listId = restApi.whenAuthenticated()
                .body(new TodoListCommand("Test List", false)).post("/api/todo/list")
                .body().as(Long.class);

        long itemId = restApi.whenAuthenticated()
                .body(new TodoItemCommand(listId, "Test List")).post("/api/todo/list/item")
                .body().as(Long.class);

        multiUserConfig.logInAs(LoginRole.CONSUMER);

        Response response = restApi.whenAuthenticated().post(String.format("/api/todo/list/%s/%s/done", listId, itemId));

        authorizationRule.expect(valueOf(response::then)
                .toAssert(t -> t.statusCode(HttpStatus.SC_OK),
                        ifAnyOf("role:ROLE_ADMIN", TestUsers.CREATOR))
                .toAssert(t -> t.statusCode(HttpStatus.SC_FORBIDDEN),
                        ifAnyOf("role:ROLE_USER", "user:user1"))
                .toAssert(t -> t.statusCode(HttpStatus.SC_UNAUTHORIZED),
                        ifAnyOf(TestUsers.ANONYMOUS)));
    }

    @Test
    public void setPublicItemAsDone() throws Throwable {
        long listId = restApi.whenAuthenticated()
                .body(new TodoListCommand("Test List", true)).post("/api/todo/list")
                .body().as(Long.class);

        long itemId = restApi.whenAuthenticated()
                .body(new TodoItemCommand(listId, "Test List")).post("/api/todo/list/item")
                .body().as(Long.class);

        multiUserConfig.logInAs(LoginRole.CONSUMER);

        Response response = restApi.whenAuthenticated().post(String.format("/api/todo/list/%s/%s/done", listId, itemId));

        authorizationRule.expect(valueOf(response::then)
                .toAssert(t -> t.statusCode(HttpStatus.SC_OK),
                        ifAnyOf("role:ROLE_ADMIN", TestUsers.CREATOR))
                .toAssert(t -> t.statusCode(HttpStatus.SC_FORBIDDEN),
                        ifAnyOf("role:ROLE_USER", "user:user1"))
                .toAssert(t -> t.statusCode(HttpStatus.SC_UNAUTHORIZED),
                        ifAnyOf(TestUsers.ANONYMOUS)));

    }

}
