package com.pierre.tasks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateTaskRequest(
        @Schema(
                description = "Title of the task to create",
                example = "Study Spring Boot"
        )
        @NotBlank(message = "Title must not be blank")
        String title) {
}
