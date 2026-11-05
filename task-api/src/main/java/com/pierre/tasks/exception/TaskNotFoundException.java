package com.pierre.tasks.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(long id){
        super("Task with ID " + id + " does not exist");
    }
}
