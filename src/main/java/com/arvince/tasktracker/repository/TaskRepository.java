package com.arvince.tasktracker.repository;

import com.arvince.tasktracker.entity.Task;
import com.arvince.tasktracker.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

	Page<Task> findByStatus(TaskStatus status, Pageable pageable);
}
