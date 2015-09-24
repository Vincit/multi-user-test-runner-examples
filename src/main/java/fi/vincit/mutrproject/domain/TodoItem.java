package fi.vincit.mutrproject.domain;

public class TodoItem {

    private long id;
    private String task;
    private boolean done;

    public TodoItem(long id, String task, boolean done) {
        this.id = id;
        this.task = task;
        this.done = done;
    }

    public long getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
