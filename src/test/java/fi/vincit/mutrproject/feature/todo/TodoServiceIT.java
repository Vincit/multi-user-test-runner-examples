package fi.vincit.mutrproject.feature.todo;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.configuration.AbstractConfiguredIT;

/**
 * Basic examples on how to use multi-user-test-runner.
 */
@TestUsers(
        creators = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", "role:ROLE_USER"},
        users = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", TestUsers.CREATOR, TestUsers.ANONYMOUS}
)
public class TodoServiceIT extends AbstractConfiguredIT {

    @Autowired
    private TodoService todoService;

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.USER);
        authorization().expect(toFail(ifAnyOf("role:ROLE_USER", TestUsers.ANONYMOUS)));
        todoService.getTodoList(id);
    }

    @Test
    public void getPublicTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", true);
        logInAs(LoginRole.USER);
        todoService.getTodoList(id);
    }

    @Test
    @TestUsers(users = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", TestUsers.CREATOR})
    public void addTodoItem() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.USER);
        authorization().expect(notToFail(ifAnyOf("role:ROLE_ADMIN", "role:ROLE_SYSTEM_ADMIN", TestUsers.CREATOR)));
        todoService.addItemToList(listId, "Write tests");
    }

    /**
     * See {@link TodoServiceJava8IT} for nicer version
     * @throws Throwable
     */
    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    @TestUsers(users = TestUsers.ANONYMOUS)
    public void addTodoItemAnonymous() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.USER);
        todoService.addItemToList(listId, "Write tests");
    }

}
