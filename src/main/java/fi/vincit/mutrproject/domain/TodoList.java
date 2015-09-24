package fi.vincit.mutrproject.domain;

import java.util.ArrayList;
import java.util.List;

public class TodoList {

    private long id;
    private String name;
    private boolean publicList;
    private User owner;
    private List<TodoItem> items;

    public TodoList(long id, String name, boolean publicList, User owner) {
        this.id = id;
        this.name = name;
        this.publicList = publicList;
        this.owner = owner;
        this.items = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isPublicList() {
        return publicList;
    }

    public User getOwner() {
        return owner;
    }

    public List<TodoItem> getItems() {
        return items;
    }
}
