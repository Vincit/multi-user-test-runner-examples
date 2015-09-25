package fi.vincit.mutrproject.service;

import static fi.vincit.multiusertest.rule.expection.Expectations.call;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.AbstractConfiguredIT;

@TestUsers(
        creators = {"role:ROLE_SUPER_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", "role:ROLE_USER"},
        users = {"role:ROLE_SUPER_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", "role:ROLE_ANONYMOUS", TestUsers.CREATOR}
)
public class TodoServiceJava8IT extends AbstractConfiguredIT {

    @Autowired
    private TodoService todoService;

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.USER);
        authorization().expect(
                call(() -> todoService.getTodoList(id))
                        .toFail(ifAnyOf("role:ROLE_USER", "role:ROLE_ANONYMOUS"))
        );
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

        authorization().expect(
                call(() -> todoService.addItemToList(listId, "Write tests"))
                        .notToFail(ifAnyOf("role:ROLE_ADMIN", "role:ROLE_SUPER_ADMIN", TestUsers.CREATOR))
        );
    }

}
