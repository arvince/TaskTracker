package com.arvince.tasktracker.dto;

import com.arvince.tasktracker.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request payload for creating or fully replacing (PUT) a task.
 */
public record TaskRequest(

		@NotBlank(message = "title is required")
		@Size(max = 100, message = "title must be at most 100 characters")
		String title,

		@Size(max = 500, message = "description must be at most 500 characters")
		String description,

		TaskStatus status,

		LocalDate dueDate
) {
}
