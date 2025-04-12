package com.collab.collab.repository;

import com.collab.collab.entity.Project;
import com.collab.collab.entity.Task;
import com.collab.collab.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject(Project project);
    List<Task> findByAssignedTo(User user);
    List<Task> findByProjectAndAssignedTo(Project project, User user);
}
