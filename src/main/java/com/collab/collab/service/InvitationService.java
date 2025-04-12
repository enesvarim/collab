package com.collab.collab.service;

import com.collab.collab.dto.InvitationActionRequest;
import com.collab.collab.dto.InvitationResponse;
import com.collab.collab.entity.Project;
import com.collab.collab.entity.ProjectInvitation;
import com.collab.collab.entity.User;
import com.collab.collab.repository.ProjectInvitationRepository;
import com.collab.collab.repository.ProjectRepository;
import com.collab.collab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvitationService {

    @Autowired
    private ProjectInvitationRepository invitationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    public List<InvitationResponse> getUserPendingInvitations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        List<ProjectInvitation> invitations = invitationRepository.findByInvitedUserAndStatus(
                user, ProjectInvitation.InvitationStatus.PENDING);
        
        return invitations.stream()
                .map(InvitationResponse::fromInvitation)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public InvitationResponse respondToInvitation(Long invitationId, InvitationActionRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        ProjectInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Davet bulunamadı"));
        
        // Davetin doğru kullanıcıya gönderildiğini kontrol et
        if (!invitation.getInvitedUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu davete yanıt verme yetkiniz yok");
        }
        
        // Davet zaten yanıtlanmış mı kontrol et
        if (invitation.getStatus() != ProjectInvitation.InvitationStatus.PENDING) {
            throw new RuntimeException("Bu davet zaten yanıtlanmış");
        }
        
        // Daveti güncelle
        invitation.setRespondedAt(LocalDateTime.now());
        
        if (request.isAccept()) {
            invitation.setStatus(ProjectInvitation.InvitationStatus.ACCEPTED);
            
            // Kullanıcıyı projeye üye olarak ekle
            Project project = invitation.getProject();
            project.addMember(user);
            projectRepository.save(project);
        } else {
            invitation.setStatus(ProjectInvitation.InvitationStatus.REJECTED);
        }
        
        return InvitationResponse.fromInvitation(invitationRepository.save(invitation));
    }
}
