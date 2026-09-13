package com.arvince.tasktracker.dto;

import com.arvince.tasktracker.entity.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response payload returned to API clients. Kept separate from the JPA
 * entity so the persistence model is never exposed directly.
 */
public record TaskResponse(
		Long id,
		String title,
		String description,
		TaskStatus status,
		LocalDate dueDate,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
