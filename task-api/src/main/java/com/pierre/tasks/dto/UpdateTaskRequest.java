package com.pierre.tasks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskRequest(
        @Schema(
                description = "Whether the task is accomplished",
                example = "true"
        )
        @NotNull(message = "Accomplished must be provided")
        Boolean accomplished
) {
}
