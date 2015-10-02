package fi.vincit.mutrproject.service;

import java.util.List;
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
import fi.vincit.mutrproject.repository.TodoItemRepository;
import fi.vincit.mutrproject.repository.TodoListRepository;

@Service
public class TodoService {

    @Autowired
    private UserService userService;

    @Autowired
    private TodoItemRepository todoItemRepository;
    @Autowired
    private TodoListRepository todoListRepository;


    public void clearList() {
        todoItemRepository.deleteAll();
        todoListRepository.deleteAll();
    }

    @PreAuthorize("isAuthenticated()")
    public long createTodoList(String listName, boolean publicList) {
        TodoList list = todoListRepository.save(new TodoList(
                listName,
                publicList,
                userService.getLoggedInUser().get()
        ));
        return list.getId();
    }

    public List<TodoList> getTodoLists() {
        final Optional<User> currentUser = userService.getLoggedInUser();
        List<TodoList> todoLists = todoListRepository.findAll();
        if (currentUser.isPresent()) {
            return todoLists.stream().filter(
                    list -> list.isPublicList()
                            || isOwner(list, currentUser.get())
                            || isAdmin(currentUser.get())
            ).collect(Collectors.toList());
        } else {
            return todoLists.stream().filter(TodoList::isPublicList).collect(Collectors.toList());
        }
    }

    public TodoList getTodoList(long id) {
        TodoList list = todoListRepository.findOne(id);
        Optional<User> user = userService.getLoggedInUser();
        return authorizeRead(list, user);
    }

    @PreAuthorize("isAuthenticated()")
    public TodoItem getTodoItem(long listId, long id) {
        TodoList list = todoListRepository.findOne(listId);
        Optional<User> user = userService.getLoggedInUser();
        authorizeRead(list, user);

        return todoItemRepository.findOne(id);
    }

    @PreAuthorize("isAuthenticated()")
    public void setItemStatus(long listId, TodoItem item) {
        setItemStatus(listId, item.getId(), item.isDone());
    }

    @PreAuthorize("isAuthenticated()")
    public void setItemStatus(long listId, long itemId, boolean done) {
        TodoItem existingItem = getTodoItem(listId, itemId);
        authorizeEdit(getTodoList(listId), userService.getLoggedInUser());
        existingItem.setDone(done);
        todoItemRepository.save(existingItem);
    }

    @PreAuthorize("isAuthenticated()")
    public long addItemToList(long listId, String task) {
        TodoList list = getTodoList(listId);

        Optional<User> user = userService.getLoggedInUser();
        authorizeEdit(list, user);

        TodoItem item = todoItemRepository.save(new TodoItem(listId, task, false));
        return item.getId();
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
            } else if (isOwner(list, loggedInUser)) {
                return;
            }
        }
        throw new AccessDeniedException("");
    }

    private boolean isAdmin(User loggedInUser) {
        return loggedInUser.getAuthorities().contains(Role.ROLE_ADMIN)
                || loggedInUser.getAuthorities().contains(Role.ROLE_SYSTEM_ADMIN);
    }
    private boolean isOwner(TodoList list, User currentUser) {
        return list.getOwner().getUsername().equals(currentUser.getUsername());
    }

}
