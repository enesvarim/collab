package com.collab.collab.dto;

import com.collab.collab.entity.Project;
import com.collab.collab.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String subject;
    private LocalDate deadline;
    private UserDto creator;
    private List<UserDto> members;
    private List<UserDto> admins;

    public static ProjectResponse fromProject(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .subject(project.getSubject())
                .deadline(project.getDeadline())
                .creator(UserDto.fromUser(project.getCreator()))
                .members(project.getMembers().stream()
                        .map(UserDto::fromUser)
                        .collect(Collectors.toList()))
                .admins(project.getAdmins().stream()
                        .map(UserDto::fromUser)
                        .collect(Collectors.toList()))
                .build();
    }

    @Data
    @Builder
    public static class UserDto {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;

        public static UserDto fromUser(User user) {
            return UserDto.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .build();
        }
    }
}
