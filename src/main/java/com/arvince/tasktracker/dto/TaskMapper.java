package com.arvince.tasktracker.dto;

import com.arvince.tasktracker.entity.Task;

public final class TaskMapper {

	private TaskMapper() {
	}

	public static TaskResponse toResponse(Task task) {
		return new TaskResponse(
				task.getId(),
				task.getTitle(),
				task.getDescription(),
				task.getStatus(),
				task.getDueDate(),
				task.getCreatedAt(),
				task.getUpdatedAt()
		);
	}

	public static Task toEntity(TaskRequest request) {
		Task task = new Task();
		applyFullUpdate(task, request);
		return task;
	}

	/** Applies a full PUT-style replacement of the fields onto an existing task. */
	public static void applyFullUpdate(Task task, TaskRequest request) {
		task.setTitle(request.title());
		task.setDescription(request.description());
		task.setStatus(request.status() != null ? request.status() : task.getStatus());
		task.setDueDate(request.dueDate());
	}

	/** Applies only the non-null fields of a PATCH request onto an existing task. */
	public static void applyPatch(Task task, TaskPatchRequest request) {
		if (request.title() != null) {
			task.setTitle(request.title());
		}
		if (request.description() != null) {
			task.setDescription(request.description());
		}
		if (request.status() != null) {
			task.setStatus(request.status());
		}
		if (request.dueDate() != null) {
			task.setDueDate(request.dueDate());
		}
	}
}
