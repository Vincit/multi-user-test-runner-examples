package fi.vincit.mutrproject.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import fi.vincit.mutrproject.domain.Role;
import fi.vincit.mutrproject.domain.TodoItem;
import fi.vincit.mutrproject.domain.TodoList;
import fi.vincit.mutrproject.domain.User;

@Service
public class TodoService {

    @Autowired
    private UserService userService;

    private static Map<Long, TodoList> todoLists = new HashMap<Long, TodoList>();
    private static long currentId = 1;
    private static long currentItemId = 1;

    public void clearList() {
        todoLists.clear();
    }

    @PreAuthorize("isAuthenticated()")
    public long createTodoList(String listName, boolean publicList) {
        long id = currentId;
        todoLists.put(id, new TodoList(id, listName, publicList, userService.getLoggedInUser()));
        currentId++;
        return id;
    }

    @PreAuthorize("isAuthenticated()")
    public TodoList getTodoList(long id) {
        TodoList list = todoLists.get(id);
        User user = userService.getLoggedInUser();
        return authorizeRead(list, user);
    }

    @PreAuthorize("isAuthenticated()")
    public long addItemToList(long listId, String task) {
        TodoList list = getTodoList(listId);

        User user = userService.getLoggedInUser();
        authorizeEdit(list, user);

        long id = currentItemId;
        list.getItems().add(new TodoItem(id, task, false));
        currentItemId++;
        return id;
    }

    private TodoList authorizeRead(TodoList list, User user) {
        if (list.isPublicList()) {
            return list;
        }
        authorizeEdit(list, user);
        return list;
    }

    private void authorizeEdit(TodoList list, User user) {
        if (user.getAuthorities().contains(Role.ROLE_ADMIN) || user.getAuthorities().contains(Role.ROLE_SUPER_ADMIN)) {
            return;
        } else if (user.getUsername().equals(list.getOwner().getUsername())) {
            return;
        }
        throw new AccessDeniedException("");
    }

}
