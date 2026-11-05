package com.pierre.tasks.service;

import com.pierre.tasks.exception.TaskNotFoundException;
import com.pierre.tasks.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    List<Task> tasks = new ArrayList<>();
    private Long currentId = 1L;

    public synchronized Task createTask(String title){
        Task task = new Task(currentId++, title.trim());
        tasks.add(task);
        return task;
    }

    public synchronized List<Task> getTasks(){
        return List.copyOf(tasks);
    }

    public synchronized Task getTaskById(long id){
        for(Task task : tasks){
            if(task.getId() == id){
                return task;
            }
        }
        throw new TaskNotFoundException(id);
    }

    public synchronized Task updateTaskStatus(long id, boolean accomplished){
        for(Task task : tasks){
            if(task.getId() == id){
                task.setAccomplished(accomplished);
                return task;
            }
        }

        throw new TaskNotFoundException(id);
    }

    public synchronized void deleteTask(long id){
        Task taskToDelete = null;
        for(Task task : tasks){
            if(task.getId() == id){
                taskToDelete = task;
                break;
            }
        }

        if(taskToDelete == null){
            throw new TaskNotFoundException(id);
        }

        tasks.remove(taskToDelete);
    }
}
