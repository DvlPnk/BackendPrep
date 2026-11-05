package com.pierre.tasks.controller;

import com.pierre.tasks.dto.CreateTaskRequest;
import com.pierre.tasks.dto.UpdateTaskRequest;
import com.pierre.tasks.exception.TaskNotFoundException;
import com.pierre.tasks.model.Task;
import com.pierre.tasks.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@Tag(name = "Tasks", description = "Create and Manage tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service){
        this.service = service;
    }

    @Operation(summary = "List tasks")
    @GetMapping("/available")
    public List<Task> getTasks(){
        return service.getTasks();
    }

    @Operation(summary = "Find a task by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task found",
                    content = @Content(
                            schema = @Schema(implementation = Task.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID must be a numeric value",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task does not exist",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable("id") long id){
        return service.getTaskById(id);
    }

    @Operation(summary = "Create new task from title")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Task created",
                    content = @Content(
                            schema = @Schema(implementation = Task.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Task title must be not blank",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody CreateTaskRequest request){
        Task task = service.createTask(request.title());
        return ResponseEntity.created(URI.create("/tasks/" + task.getId())).body(task);
    }

    @Operation(summary = "Update task accomplishment")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task accomplishment updated",
                    content = @Content(
                            schema = @Schema(implementation = Task.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid ID or request body",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task does not exist",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable("id") long id,
            @Valid @RequestBody UpdateTaskRequest request){
        Task task = service.updateTaskStatus(id, request.accomplished());
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Delete task by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Task deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID must be a numeric value",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task does not exist",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable("id") long id
    ){
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
