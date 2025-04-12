package com.collab.collab.repository;

import com.collab.collab.entity.Project;
import com.collab.collab.entity.ProjectInvitation;
import com.collab.collab.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectInvitationRepository extends JpaRepository<ProjectInvitation, Long> {
    List<ProjectInvitation> findByInvitedUserAndStatus(User user, ProjectInvitation.InvitationStatus status);
    Optional<ProjectInvitation> findByProjectAndInvitedUser(Project project, User user);
    List<ProjectInvitation> findByProject(Project project);
}
