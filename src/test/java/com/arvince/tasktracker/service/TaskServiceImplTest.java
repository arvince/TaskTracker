package com.arvince.tasktracker.service;

import com.arvince.tasktracker.dto.TaskPatchRequest;
import com.arvince.tasktracker.dto.TaskRequest;
import com.arvince.tasktracker.dto.TaskResponse;
import com.arvince.tasktracker.entity.Task;
import com.arvince.tasktracker.entity.TaskStatus;
import com.arvince.tasktracker.exception.TaskNotFoundException;
import com.arvince.tasktracker.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

	@Mock
	private TaskRepository taskRepository;

	@InjectMocks
	private TaskServiceImpl taskService;

	private Task existingTask;

	@BeforeEach
	void setUp() {
		existingTask = new Task();
		existingTask.setId(1L);
		existingTask.setTitle("Original title");
		existingTask.setDescription("Original description");
		existingTask.setStatus(TaskStatus.TODO);
		existingTask.setDueDate(LocalDate.of(2026, 1, 1));
		existingTask.setCreatedAt(LocalDateTime.now());
		existingTask.setUpdatedAt(LocalDateTime.now());
	}

	@Test
	void createTask_savesAndReturnsResponse() {
		TaskRequest request = new TaskRequest("Write tests", "Cover the service layer", TaskStatus.TODO, null);
		when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
			Task toSave = invocation.getArgument(0);
			toSave.setId(42L);
			return toSave;
		});

		TaskResponse response = taskService.createTask(request);

		assertThat(response.id()).isEqualTo(42L);
		assertThat(response.title()).isEqualTo("Write tests");
		assertThat(response.status()).isEqualTo(TaskStatus.TODO);
	}

	@Test
	void getTask_returnsMappedResponse_whenFound() {
		when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

		TaskResponse response = taskService.getTask(1L);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.title()).isEqualTo("Original title");
	}

	@Test
	void getTask_throwsNotFound_whenMissing() {
		when(taskRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> taskService.getTask(99L))
				.isInstanceOf(TaskNotFoundException.class)
				.hasMessageContaining("99");
	}

	@Test
	void updateTask_replacesFields() {
		when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
		when(taskRepository.saveAndFlush(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
		TaskRequest request = new TaskRequest("Updated title", "Updated description", TaskStatus.IN_PROGRESS, LocalDate.of(2026, 2, 1));

		TaskResponse response = taskService.updateTask(1L, request);

		assertThat(response.title()).isEqualTo("Updated title");
		assertThat(response.description()).isEqualTo("Updated description");
		assertThat(response.status()).isEqualTo(TaskStatus.IN_PROGRESS);
		assertThat(response.dueDate()).isEqualTo(LocalDate.of(2026, 2, 1));
	}

	@Test
	void patchTask_appliesOnlyProvidedFields() {
		when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
		when(taskRepository.saveAndFlush(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
		TaskPatchRequest patch = new TaskPatchRequest(null, null, TaskStatus.DONE, null);

		TaskResponse response = taskService.patchTask(1L, patch);

		assertThat(response.status()).isEqualTo(TaskStatus.DONE);
		assertThat(response.title()).isEqualTo("Original title");
		assertThat(response.description()).isEqualTo("Original description");
	}

	@Test
	void deleteTask_deletes_whenExists() {
		when(taskRepository.existsById(1L)).thenReturn(true);

		taskService.deleteTask(1L);

		verify(taskRepository, times(1)).deleteById(1L);
	}

	@Test
	void deleteTask_throwsNotFound_whenMissing() {
		when(taskRepository.existsById(99L)).thenReturn(false);

		assertThatThrownBy(() -> taskService.deleteTask(99L))
				.isInstanceOf(TaskNotFoundException.class);
		verify(taskRepository, never()).deleteById(any());
	}
}
