package com.collab.collab.dto;

import com.collab.collab.entity.ProjectInvitation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvitationResponse {
    private Long id;
    private Long projectId;
    private String projectName;
    private String projectSubject;
    private String inviterName;
    private String status;
    private LocalDateTime createdAt;

    public static InvitationResponse fromInvitation(ProjectInvitation invitation) {
        return InvitationResponse.builder()
                .id(invitation.getId())
                .projectId(invitation.getProject().getId())
                .projectName(invitation.getProject().getName())
                .projectSubject(invitation.getProject().getSubject())
                .inviterName(invitation.getProject().getCreator().getFirstName() + " " + invitation.getProject().getCreator().getLastName())
                .status(invitation.getStatus().toString())
                .createdAt(invitation.getCreatedAt())
                .build();
    }
}
