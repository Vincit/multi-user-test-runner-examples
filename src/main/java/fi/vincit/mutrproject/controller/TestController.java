package fi.vincit.mutrproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.vincit.mutrproject.domain.TodoCommand;
import fi.vincit.mutrproject.domain.TodoItemCommand;
import fi.vincit.mutrproject.domain.TodoList;
import fi.vincit.mutrproject.service.TodoService;

@RestController
public class TestController {

    @Autowired
    private TodoService todoService;

    @RequestMapping(value = "/api/todo/lists", method = RequestMethod.GET)
    public List<TodoList> getLists() {
        return todoService.getTodoLists();
    }

    @RequestMapping(value = "/api/todo/list/{listId}", method = RequestMethod.GET)
    public TodoList getList(@PathVariable("listId") long id) {
        return todoService.getTodoList(id);
    }

    @RequestMapping(value = "/api/todo/list/item", method = RequestMethod.POST)
    public long createItemTask(@RequestBody TodoItemCommand todoItemCommand) {
        return todoService.addItemToList(todoItemCommand.getListId(), todoItemCommand.getName());
    }

    @RequestMapping(value = "/api/todo/list/public", method = RequestMethod.POST, consumes = "application/json")
    public long createPublicList(@RequestBody TodoCommand todoCommand) {
        return todoService.createTodoList(todoCommand.getName(), true);
    }

    @RequestMapping(value = "/api/todo/list/private", method = RequestMethod.POST, consumes = "application/json")
    public long createPrivateList(@RequestBody TodoCommand todoCommand) {
        return todoService.createTodoList(todoCommand.getName(), false);
    }

    @RequestMapping(value = "/api/todo/list/{listId}/{itemId}/done", method = RequestMethod.POST, consumes = "application/json")
    public void saveItem(@PathVariable("listId") long listId, @PathVariable("itemId") long itemId) {
        todoService.setItemStatus(listId, itemId, true);
    }

}
