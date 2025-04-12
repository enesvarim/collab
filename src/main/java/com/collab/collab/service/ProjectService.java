package com.collab.collab.service;

import com.collab.collab.dto.ProjectCreateRequest;
import com.collab.collab.dto.ProjectResponse;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectInvitationRepository invitationRepository;

    @Transactional
    public ProjectResponse createProject(ProjectCreateRequest request, String creatorEmail) {
        // Projeyi oluşturacak kullanıcıyı bul
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        // Yeni proje oluştur
        Project project = new Project();
        project.setName(request.getName());
        project.setSubject(request.getSubject());
        project.setDeadline(request.getDeadline());
        project.setCreator(creator);
        
        // Projeye kurucuyu üye ve yönetici olarak ekle
        project.addMember(creator);
        project.addAdmin(creator); // Projeyi oluşturan kişi otomatik olarak yönetici olur
        
        // Projeyi kaydet
        Project savedProject = projectRepository.save(project);
        
        // Eğer davet edilecek üyeler varsa, davet gönder
        if (request.getMemberEmails() != null && !request.getMemberEmails().isEmpty()) {
            for (String email : request.getMemberEmails()) {
                Optional<User> userOpt = userRepository.findByEmail(email);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    
                    // Kendisini davet etmesini engelle
                    if (user.getId().equals(creator.getId())) {
                        continue;
                    }
                    
                    ProjectInvitation invitation = new ProjectInvitation();
                    invitation.setProject(savedProject);
                    invitation.setInvitedUser(user);
                    invitation.setStatus(ProjectInvitation.InvitationStatus.PENDING);
                    invitation.setCreatedAt(LocalDateTime.now());
                    
                    invitationRepository.save(invitation);
                }
            }
        }
        
        return ProjectResponse.fromProject(savedProject);
    }
    
    @Transactional
    public ProjectResponse addAdmin(Long projectId, Long userId, String adminEmail) {
        User currentAdmin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));
        
        // Yönetici yetkisi verecek kişinin yönetici olup olmadığını kontrol et
        if (!project.getAdmins().contains(currentAdmin) && !project.getCreator().getId().equals(currentAdmin.getId())) {
            throw new RuntimeException("Bu projede yönetici atama yetkiniz yok");
        }
        
        User newAdmin = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Atanacak kullanıcı bulunamadı"));
        
        // Kullanıcının projede üye olup olmadığını kontrol et
        if (!project.getMembers().contains(newAdmin)) {
            throw new RuntimeException("Bu kullanıcı projede üye değil");
        }
        
        // Kullanıcıyı yönetici olarak ekle
        project.addAdmin(newAdmin);
        
        return ProjectResponse.fromProject(projectRepository.save(project));
    }
    
    @Transactional
    public ProjectResponse removeAdmin(Long projectId, Long userId, String adminEmail) {
        // Sadece proje kurucusu bir yöneticiyi yetkisini kaldırabilir
        User currentUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));
        
        // İşlemi yapan kişinin proje kurucusu olup olmadığını kontrol et
        if (!project.getCreator().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Sadece proje kurucusu yönetici yetkisini kaldırabilir");
        }
        
        // Kurucunun kendi yönetici yetkisini kaldırmasını engelle
        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("Proje kurucusu kendi yönetici yetkisini kaldıramaz");
        }
        
        User adminToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        // Kullanıcının yönetici olup olmadığını kontrol et
        if (!project.getAdmins().contains(adminToRemove)) {
            throw new RuntimeException("Bu kullanıcı zaten yönetici değil");
        }
        
        // Kullanıcının yönetici yetkisini kaldır
        project.removeAdmin(adminToRemove);
        
        return ProjectResponse.fromProject(projectRepository.save(project));
    }
    
    public List<ProjectResponse> getUserProjects(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        // Kullanıcının oluşturduğu veya üye olduğu projeleri getir
        List<Project> projects = projectRepository.findByMembersContaining(user);
        
        return projects.stream()
                .map(ProjectResponse::fromProject)
                .collect(Collectors.toList());
    }
    
    public ProjectResponse getProjectById(Long projectId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));
        
        // Kullanıcının projeye erişim yetkisi olup olmadığını kontrol et
        if (!project.getMembers().contains(user) && !project.getCreator().getId().equals(user.getId())) {
            throw new RuntimeException("Bu projeye erişim yetkiniz yok");
        }
        
        return ProjectResponse.fromProject(project);
    }
}
