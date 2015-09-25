package fi.vincit.mutrproject.service;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.AbstractConfiguredIT;
import fi.vincit.mutrproject.domain.Role;
import fi.vincit.mutrproject.domain.TodoItem;

@TestUsers(
        creators = {"user:admin", "user:user1"},
        users = {"role:ROLE_SUPER_ADMIN", "user:user2", "role:ROLE_ANONYMOUS", TestUsers.CREATOR}
)
public class TodoServiceWithUsersIT extends AbstractConfiguredIT {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @Before
    public void initUsers() {
        userService.createUser("admin", Role.ROLE_ADMIN);
        userService.createUser("user1", Role.ROLE_USER);
        userService.createUser("user2", Role.ROLE_USER);
    }

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.USER);
        authorization().expect(toFail(ifAnyOf("user:user2", "role:ROLE_ANONYMOUS")));
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
        authorization().expect(notToFail(ifAnyOf("role:ROLE_SUPER_ADMIN", TestUsers.CREATOR)));
        todoService.addItemToList(listId, "Write tests");
    }

    @Test
    public void setTaskAsDone() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);

        logInAs(LoginRole.USER);
        authorization().expect(notToFail(ifAnyOf("role:ROLE_SUPER_ADMIN", TestUsers.CREATOR)));
        long itemId = todoService.addItemToList(listId, "Write tests");
        TodoItem item = todoService.getTodoItem(listId, itemId);
        item.setDone(true);
        todoService.setItemStatus(listId, item);
    }

}
