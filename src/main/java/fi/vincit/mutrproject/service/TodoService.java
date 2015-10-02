package fi.vincit.mutrproject.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private static Map<Long, TodoList> todoLists = new LinkedHashMap<>();
    private static long currentId = 1;
    private static long currentItemId = 1;

    public void clearList() {
        todoLists.clear();
    }

    @PreAuthorize("isAuthenticated()")
    public long createTodoList(String listName, boolean publicList) {
        long id = currentId;
        todoLists.put(id, new TodoList(id, listName, publicList, userService.getLoggedInUser().get()));
        currentId++;
        return id;
    }

    public List<TodoList> getTodoLists() {
        final Optional<User> currentUser = userService.getLoggedInUser();
        if (currentUser.isPresent()) {
            return todoLists.values().stream().filter(
                    list -> list.isPublicList()
                            || list.getOwner().getUsername().equals(currentUser.get().getUsername())
                            || currentUser.get().getAuthorities().contains(Role.ROLE_ADMIN)
                            || currentUser.get().getAuthorities().contains(Role.ROLE_SYSTEM_ADMIN)
            ).collect(Collectors.toList());
        } else {
            return todoLists.values().stream().filter(TodoList::isPublicList).collect(Collectors.toList());
        }
    }

    public TodoList getTodoList(long id) {
        TodoList list = todoLists.get(id);
        Optional<User> user = userService.getLoggedInUser();
        return authorizeRead(list, user);
    }

    @PreAuthorize("isAuthenticated()")
    public TodoItem getTodoItem(long listId, long id) {
        TodoList list = todoLists.get(listId);
        Optional<User> user = userService.getLoggedInUser();
        authorizeRead(list, user);

        return list.getItems().stream()
                .filter(i -> i.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @PreAuthorize("isAuthenticated()")
    public void setItemStatus(long listId, TodoItem item) {
        TodoItem existingItem = getTodoItem(listId, item.getId());
        authorizeEdit(getTodoList(listId), userService.getLoggedInUser());
        existingItem.setDone(item.isDone());
    }

    @PreAuthorize("isAuthenticated()")
    public void setItemStatus(long listId, long itemId, boolean done) {
        TodoItem existingItem = getTodoItem(listId, itemId);
        authorizeEdit(getTodoList(listId), userService.getLoggedInUser());
        existingItem.setDone(done);
    }

    @PreAuthorize("isAuthenticated()")
    public long addItemToList(long listId, String task) {
        TodoList list = getTodoList(listId);

        Optional<User> user = userService.getLoggedInUser();
        authorizeEdit(list, user);

        long id = currentItemId;
        list.getItems().add(new TodoItem(id, task, false));
        currentItemId++;
        return id;
    }

    private TodoList authorizeRead(TodoList list, Optional<User> user) {
        if (list.isPublicList()) {
            return list;
        }
        authorizeEdit(list, user);
        return list;
    }

    private void authorizeEdit(TodoList list, Optional<User> user) {
        if (user.isPresent()) {
            User loggedInUser = user.get();
            if (isAdmin(loggedInUser)) {
                return;
            } else if (loggedInUser.getUsername().equals(list.getOwner().getUsername())) {
                return;
            }
        }
        throw new AccessDeniedException("");
    }

    private boolean isAdmin(User loggedInUser) {
        return loggedInUser.getAuthorities().contains(Role.ROLE_ADMIN)
                || loggedInUser.getAuthorities().contains(Role.ROLE_SYSTEM_ADMIN);
    }

}
