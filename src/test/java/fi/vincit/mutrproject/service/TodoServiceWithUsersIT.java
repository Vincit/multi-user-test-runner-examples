package fi.vincit.mutrproject.service;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.configuration.AbstractConfiguredIT;
import fi.vincit.mutrproject.service.todo.TodoService;
import fi.vincit.mutrproject.service.todo.dto.TodoItemDto;
import fi.vincit.mutrproject.service.user.UserService;
import fi.vincit.mutrproject.service.user.model.Role;

/**
 * Example how to use existing users
 */
@TestUsers(
        creators = {"user:admin", "user:user1"},
        users = {"role:ROLE_SYSTEM_ADMIN", "user:user2", TestUsers.CREATOR}
)
public class TodoServiceWithUsersIT extends AbstractConfiguredIT {

    @After
    public void clear() {
        todoService.clearList();
        userService.clearUsers();
    }

    @Autowired
    private UserService userService;
    @Autowired
    private TodoService todoService;

    @Before
    public void initUsers() {
        userService.createUser("admin", "admin", Role.ROLE_ADMIN);
        userService.createUser("user1", "user1", Role.ROLE_USER);
        userService.createUser("user2", "user2", Role.ROLE_USER);
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
        authorization().expect(notToFail(ifAnyOf("role:ROLE_SYSTEM_ADMIN", TestUsers.CREATOR)));
        todoService.addItemToList(listId, "Write tests");
    }

    @Test
    public void setTaskAsDone() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);

        logInAs(LoginRole.USER);
        authorization().expect(notToFail(ifAnyOf("role:ROLE_SYSTEM_ADMIN", TestUsers.CREATOR)));
        long itemId = todoService.addItemToList(listId, "Write tests");
        TodoItemDto item = todoService.getTodoItem(listId, itemId);
        todoService.setItemStatus(listId, item.getId(), true);
    }

}
