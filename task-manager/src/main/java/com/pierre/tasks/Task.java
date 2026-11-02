package com.pierre.tasks;

public class Task {
    private final long id;

    private final String title;

    private boolean completed = false;

    Task(long id, String title){
        this.id = id;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(){
        completed = true;
    }
}
