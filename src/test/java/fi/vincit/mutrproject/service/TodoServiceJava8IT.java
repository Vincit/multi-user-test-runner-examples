package fi.vincit.mutrproject.service;

import static fi.vincit.multiusertest.rule.expection.Expectations.call;
import static fi.vincit.multiusertest.rule.expection.Expectations.valueOf;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.configuration.AbstractConfiguredIT;
import fi.vincit.mutrproject.domain.TodoList;

/**
 * Examples how to use advanced assertions with Java 8
 */
@TestUsers(
        creators = {"role:ROLE_SUPER_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER"},
        users = {"role:ROLE_SUPER_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", "role:ROLE_ANONYMOUS", TestUsers.CREATOR}
)
public class TodoServiceJava8IT extends AbstractConfiguredIT {

    @Autowired
    private TodoService todoService;

    @Before
    public void init() {
        todoService.clearList();
    }

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

    @Test
    public void getListsCount() throws Throwable {
        todoService.createTodoList("Test list 1", false);
        todoService.createTodoList("Test list 2", true);
        todoService.createTodoList("Test list 3", false);

        logInAs(LoginRole.USER);

        authorization().expect(valueOf(() -> todoService.getTodoLists().size())
                        .toEqual(1, ifAnyOf("role:ROLE_USER", "role:ANONYMOUS"))
                        .toEqual(3, ifAnyOf(TestUsers.CREATOR, "role:ROLE_ADMIN", "role:ROLE_SUPER_ADMIN"))
        );
    }

    @Test
    public void getLists() throws Throwable {
        todoService.createTodoList("Test list 1", false);
        todoService.createTodoList("Test list 2", true);
        todoService.createTodoList("Test list 3", false);

        logInAs(LoginRole.USER);

        authorization().expect(valueOf(() ->
                        todoService.getTodoLists().stream().map(TodoList::getName).collect(toList()))
                        .toAssert(value -> assertThat(value, is(Arrays.asList("Test list 2"))),
                                ifAnyOf("role:ROLE_USER", "role:ANONYMOUS")
                        )
                        .toAssert(value -> assertThat(value, is(Arrays.asList("Test list 1",
                                        "Test list 2",
                                        "Test list 3"))),
                                ifAnyOf(TestUsers.CREATOR, "role:ROLE_ADMIN", "role:ROLE_SUPER_ADMIN")
                        )
        );
    }

}
