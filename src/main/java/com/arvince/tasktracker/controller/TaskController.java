package com.arvince.tasktracker.controller;

import com.arvince.tasktracker.dto.TaskPatchRequest;
import com.arvince.tasktracker.dto.TaskRequest;
import com.arvince.tasktracker.dto.TaskResponse;
import com.arvince.tasktracker.entity.TaskStatus;
import com.arvince.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;

	@PostMapping
	public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
		TaskResponse created = taskService.createTask(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@GetMapping
	public ResponseEntity<List<TaskResponse>> getTasks(
			@RequestParam(required = false) TaskStatus status,
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@RequestParam(required = false) String sort) {

		Pageable pageable = buildPageable(page, size, sort);
		Page<TaskResponse> result = taskService.getTasks(status, pageable);
		return ResponseEntity.ok(result.getContent());
	}

	@GetMapping("/{id}")
	public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
		return ResponseEntity.ok(taskService.getTask(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
		return ResponseEntity.ok(taskService.updateTask(id, request));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<TaskResponse> patchTask(@PathVariable Long id, @Valid @RequestBody TaskPatchRequest request) {
		return ResponseEntity.ok(taskService.patchTask(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
		taskService.deleteTask(id);
		return ResponseEntity.noContent().build();
	}

	/** Builds a Pageable from optional query params; defaults to all tasks (page 0, size = Integer.MAX_VALUE) sorted by id when none are given. */
	private Pageable buildPageable(Integer page, Integer size, String sort) {
		int resolvedPage = page != null ? page : 0;
		int resolvedSize = size != null ? size : Integer.MAX_VALUE;
		Sort resolvedSort = Sort.by(Sort.Direction.ASC, "id");

		if (sort != null && !sort.isBlank()) {
			String[] parts = sort.split(",", 2);
			String property = parts[0].trim();
			Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())
					? Sort.Direction.DESC
					: Sort.Direction.ASC;
			resolvedSort = Sort.by(direction, property);
		}

		return PageRequest.of(resolvedPage, resolvedSize, resolvedSort);
	}
}
