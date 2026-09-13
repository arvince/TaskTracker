package com.arvince.tasktracker.service;

import com.arvince.tasktracker.dto.TaskPatchRequest;
import com.arvince.tasktracker.dto.TaskRequest;
import com.arvince.tasktracker.dto.TaskResponse;
import com.arvince.tasktracker.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

	TaskResponse createTask(TaskRequest request);

	TaskResponse getTask(Long id);

	Page<TaskResponse> getTasks(TaskStatus status, Pageable pageable);

	TaskResponse updateTask(Long id, TaskRequest request);

	TaskResponse patchTask(Long id, TaskPatchRequest request);

	void deleteTask(Long id);
}
