package fi.vincit.mutrproject.service;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.configuration.AbstractConfiguredIT;

@TestUsers(
        creators = {"role:ROLE_SUPER_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", "role:ROLE_USER"},
        users = {"role:ROLE_SUPER_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", "role:ROLE_ANONYMOUS", TestUsers.CREATOR}
)
public class TodoServiceIT extends AbstractConfiguredIT {

    @Autowired
    private TodoService todoService;

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.USER);
        authorization().expect(toFail(ifAnyOf("role:ROLE_USER", "role:ROLE_ANONYMOUS")));
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
        authorization().expect(notToFail(ifAnyOf("role:ROLE_ADMIN", "role:ROLE_SUPER_ADMIN", TestUsers.CREATOR)));
        todoService.addItemToList(listId, "Write tests");
    }

}
