package com.pierre.tasks.model;

public class Task {
    private final long id;

    private final String title;

    private boolean accomplished = false;

    public Task(long id, String title){
        this.id = id;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isAccomplished() {
        return accomplished;
    }

    public void setAccomplished(boolean accomplished){
        this.accomplished = accomplished;
    }
}
