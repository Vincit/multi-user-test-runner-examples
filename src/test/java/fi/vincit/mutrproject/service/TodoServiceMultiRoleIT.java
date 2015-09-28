package fi.vincit.mutrproject.service;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.configuration.AbstractConfiguredMultiRoleIT;

/**
 * Example how to use multiple roles per user using intermediate role.
 * See {@link AbstractConfiguredMultiRoleIT} for an example how to implement multi role support.
 */
@TestUsers(
        creators = {"role:ADMINISTRATOR", "role:REGULAR_USER"},
        users = {"role:ADMINISTRATOR", "role:REGULAR_USER", TestUsers.CREATOR}
)
public class TodoServiceMultiRoleIT extends AbstractConfiguredMultiRoleIT {

    @Autowired
    private TodoService todoService;

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.USER);
        authorization().expect(toFail(ifAnyOf("role:REGULAR_USER")));
        todoService.getTodoList(id);
    }

    @Test
    public void getPublicTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", true);
        logInAs(LoginRole.USER);
        todoService.getTodoList(id);
    }

    @Test
    public void addTodoItem() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.USER);
        authorization().expect(notToFail(ifAnyOf("role:ADMINISTRATOR", TestUsers.CREATOR)));
        todoService.addItemToList(listId, "Write tests");
    }

}
