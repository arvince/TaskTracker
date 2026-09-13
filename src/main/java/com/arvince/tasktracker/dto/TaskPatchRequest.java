package com.arvince.tasktracker.dto;

import com.arvince.tasktracker.entity.TaskStatus;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request payload for a partial update (PATCH) of a task. Every field is
 * optional; only non-null fields are applied to the existing task.
 */
public record TaskPatchRequest(

		@Size(min = 1, max = 100, message = "title must be between 1 and 100 characters")
		String title,

		@Size(max = 500, message = "description must be at most 500 characters")
		String description,

		TaskStatus status,

		LocalDate dueDate
) {
}
