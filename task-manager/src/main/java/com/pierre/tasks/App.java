package com.pierre.tasks;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App {
    public static void main(String[] args){
        System.out.println("Task manager iniciado");

        TaskService taskService = new TaskService();
        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.println("1. Create task\n" +
                    "2. List tasks\n" +
                    "3. Complete task\n" +
                    "4. Delete task\n" +
                    "0. Exit");
            System.out.print("Enter your choice: ");
            int option;

            try {
                option = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number");
                continue;
            }

            boolean breakLoop = false;
            switch (option){
                case(0):
                    breakLoop = true;
                    break;

                case (1):
                    System.out.print("Enter your new title tasks: ");
                    String title = scanner.nextLine();
                    try {
                        taskService.createTask(title);
                    }catch (IllegalArgumentException e){
                        System.out.println(e.getMessage());
                    }
                    break;

                case(2):
                    printTaskList(taskService);
                    break;

                case(3):
                    System.out.println("Enter task ID:");

                    try {
                        long id = Long.parseLong(scanner.nextLine().trim());
                        taskService.completeById(id);
                        System.out.println("Task with ID " + id + " completed");
                    } catch (NumberFormatException e) {
                        System.out.println("Enter a valid numeric ID");
                    } catch (NoSuchElementException e) {
                        System.out.println(e.getMessage());
                    }

                    break;

                case(4):
                    System.out.println("Enter task ID:");

                    try {
                        long id = Long.parseLong(scanner.nextLine().trim());
                        taskService.deleteById(id);
                        System.out.println("Task with ID " + id + " deleted");
                    } catch (NumberFormatException e) {
                        System.out.println("Enter a valid numeric ID");
                    } catch (NoSuchElementException e) {
                        System.out.println(e.getMessage());
                    }

                    break;

                default:
                    System.out.println("Invalid option");
                    break;
            }

            if(breakLoop){
                break;
            }
        }
    }

    public static void printTaskList(TaskService taskService){
        List<Task> tasks = taskService.getTasks();
        if(tasks.isEmpty()){
            System.out.println("No tasks yet");
            return;
        }
        System.out.println("\nTasks:");
        for(var task : tasks){
            System.out.println(task.getId() + " | " + task.getTitle() + " | " + (!task.isCompleted() ? "Pending" : "Completed"));
        }
        System.out.println();
    }
}
