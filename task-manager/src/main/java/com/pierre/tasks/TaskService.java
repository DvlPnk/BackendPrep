package com.pierre.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class TaskService {
    private long currentId;

    private final List<Task> tasks;

    public TaskService(){
        currentId = 1;
        tasks = new ArrayList<Task>();
    }

    public Task createTask(String title){
        if(title == null || title.isBlank()){
            throw new IllegalArgumentException("Title must not be blank");
        }
        Task newTask = new Task(currentId++, title.trim());
        tasks.add(newTask);
        return newTask;
    }

    public List<Task> getTasks(){
        return List.copyOf(tasks);
    }

    public void completeById(long id){
        for(Task task : tasks){
            if(task.getId() == id){
                task.setCompleted();
                return;
            }
        }

        throw new NoSuchElementException("Task with id " + id + " does not exist");
    }

    public void deleteById(long id){
        Task taskToRemove = null;
        for(Task task : tasks){
            if(task.getId() == id){
                taskToRemove = task;
                break;
            }
        }
        if(taskToRemove == null){
            throw new NoSuchElementException("Task with id " + id + " does not exist");
        }

        tasks.remove(taskToRemove);
    }
}
