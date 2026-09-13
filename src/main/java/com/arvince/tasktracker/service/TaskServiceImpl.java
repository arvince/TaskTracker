package com.arvince.tasktracker.service;

import com.arvince.tasktracker.dto.TaskMapper;
import com.arvince.tasktracker.dto.TaskPatchRequest;
import com.arvince.tasktracker.dto.TaskRequest;
import com.arvince.tasktracker.dto.TaskResponse;
import com.arvince.tasktracker.entity.Task;
import com.arvince.tasktracker.entity.TaskStatus;
import com.arvince.tasktracker.exception.TaskNotFoundException;
import com.arvince.tasktracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

	private final TaskRepository taskRepository;

	@Override
	public TaskResponse createTask(TaskRequest request) {
		Task task = TaskMapper.toEntity(request);
		Task saved = taskRepository.save(task);
		return TaskMapper.toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public TaskResponse getTask(Long id) {
		Task task = findTaskOrThrow(id);
		return TaskMapper.toResponse(task);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<TaskResponse> getTasks(TaskStatus status, Pageable pageable) {
		Page<Task> page = status != null
				? taskRepository.findByStatus(status, pageable)
				: taskRepository.findAll(pageable);
		return page.map(TaskMapper::toResponse);
	}

	@Override
	public TaskResponse updateTask(Long id, TaskRequest request) {
		Task task = findTaskOrThrow(id);
		TaskMapper.applyFullUpdate(task, request);
		// Flush so @PreUpdate runs now and the response reflects the real updatedAt,
		// rather than the stale in-memory value (the hook only fires at flush time).
		Task saved = taskRepository.saveAndFlush(task);
		return TaskMapper.toResponse(saved);
	}

	@Override
	public TaskResponse patchTask(Long id, TaskPatchRequest request) {
		Task task = findTaskOrThrow(id);
		TaskMapper.applyPatch(task, request);
		Task saved = taskRepository.saveAndFlush(task);
		return TaskMapper.toResponse(saved);
	}

	@Override
	public void deleteTask(Long id) {
		if (!taskRepository.existsById(id)) {
			throw new TaskNotFoundException(id);
		}
		taskRepository.deleteById(id);
	}

	private Task findTaskOrThrow(Long id) {
		return taskRepository.findById(id)
				.orElseThrow(() -> new TaskNotFoundException(id));
	}
}
