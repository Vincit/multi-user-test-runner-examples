package fi.vincit.mutrproject.service;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.configuration.AbstractConfiguredRoleAliasIT;

/**
 * Example test using role aliasing. See {@link AbstractConfiguredRoleAliasIT} for an example
 * how to implement role aliasing.
 */
@TestUsers(
        creators = {"role:SYSTEM_ADMIN", "role:ADMIN", "role:USER", "role:USER"},
        users = {"role:SYSTEM_ADMIN", "role:ADMIN", "role:USER", "role:UNREGISTERED", TestUsers.CREATOR}
)
public class TodoServiceRoleAliasIT extends AbstractConfiguredRoleAliasIT {

    @Autowired
    private TodoService todoService;

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.USER);
        authorization().expect(toFail(ifAnyOf("role:USER", "role:UNREGISTERED")));
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
        authorization().expect(notToFail(ifAnyOf("role:ADMIN", "role:SYSTEM_ADMIN", TestUsers.CREATOR)));
        todoService.addItemToList(listId, "Write tests");
    }

}
