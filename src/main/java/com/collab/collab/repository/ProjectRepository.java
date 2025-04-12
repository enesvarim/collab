package com.collab.collab.repository;

import com.collab.collab.entity.Project;
import com.collab.collab.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCreator(User creator);
    List<Project> findByMembersContaining(User member);
}
