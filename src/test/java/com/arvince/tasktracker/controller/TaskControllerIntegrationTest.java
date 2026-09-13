package com.arvince.tasktracker.controller;

import com.arvince.tasktracker.dto.TaskRequest;
import com.arvince.tasktracker.dto.TaskResponse;
import com.arvince.tasktracker.entity.TaskStatus;
import com.arvince.tasktracker.exception.TaskNotFoundException;
import com.arvince.tasktracker.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private TaskService taskService;

	@Test
	void createTask_returns201_whenValid() throws Exception {
		TaskRequest request = new TaskRequest("Buy groceries", null, null, null);
		TaskResponse response = new TaskResponse(1L, "Buy groceries", null, TaskStatus.TODO, null,
				LocalDateTime.now(), LocalDateTime.now());
		when(taskService.createTask(any())).thenReturn(response);

		mockMvc.perform(post("/api/tasks")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.title").value("Buy groceries"))
				.andExpect(jsonPath("$.status").value("TODO"));
	}

	@Test
	void createTask_returns400_whenTitleBlank() throws Exception {
		TaskRequest request = new TaskRequest(" ", null, null, null);

		mockMvc.perform(post("/api/tasks")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.fieldErrors.title").exists());
	}

	@Test
	void getTask_returns404_whenMissing() throws Exception {
		when(taskService.getTask(99L)).thenThrow(new TaskNotFoundException(99L));

		mockMvc.perform(get("/api/tasks/99"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Task not found with id: 99"));
	}

	@Test
	void getTasks_returnsList() throws Exception {
		TaskResponse response = new TaskResponse(1L, "Task one", null, TaskStatus.TODO, LocalDate.now(),
				LocalDateTime.now(), LocalDateTime.now());
		Page<TaskResponse> page = new PageImpl<>(List.of(response));
		when(taskService.getTasks(any(), any())).thenReturn(page);

		mockMvc.perform(get("/api/tasks"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].title").value("Task one"));
	}

	@Test
	void deleteTask_returns204() throws Exception {
		mockMvc.perform(delete("/api/tasks/1"))
				.andExpect(status().isNoContent());
	}
}
