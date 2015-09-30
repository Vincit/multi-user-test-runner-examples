package fi.vincit.mutrproject.domain;

public class TodoCommand {
    private String name;

    public TodoCommand() {
    }

    public TodoCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
