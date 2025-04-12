package com.collab.collab.service;

import com.collab.collab.dto.TaskCreateRequest;
import com.collab.collab.dto.TaskResponse;
import com.collab.collab.entity.Project;
import com.collab.collab.entity.Task;
import com.collab.collab.entity.User;
import com.collab.collab.repository.ProjectRepository;
import com.collab.collab.repository.TaskRepository;
import com.collab.collab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public TaskResponse createTask(TaskCreateRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));
        
        // Görev oluşturan kişinin yönetici yetkisine sahip olup olmadığını kontrol et
        if (!project.getAdmins().contains(creator) && !project.getCreator().getId().equals(creator.getId())) {
            throw new RuntimeException("Bu projede görev oluşturma yetkiniz yok. Sadece yöneticiler görev oluşturabilir.");
        }
        
        User assignedUser = userRepository.findById(request.getAssignedToId())
                .orElseThrow(() -> new RuntimeException("Atanacak kullanıcı bulunamadı"));
        
        // Görevin atanacağı kullanıcının projede üye olup olmadığını kontrol et
        if (!project.getMembers().contains(assignedUser)) {
            throw new RuntimeException("Görev atanacak kullanıcı projede üye değil");
        }
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setProject(project);
        task.setAssignedTo(assignedUser);
        task.setCreatedBy(creator);
        task.setDeadline(request.getDeadline());
        task.setCreatedAt(LocalDateTime.now());
        task.setStatus(Task.TaskStatus.PENDING);
        
        return TaskResponse.fromTask(taskRepository.save(task));
    }
    
    public List<TaskResponse> getProjectTasks(Long projectId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));
        
        // Kullanıcının projeye erişim yetkisi olup olmadığını kontrol et
        if (!project.getMembers().contains(user)) {
            throw new RuntimeException("Bu projeye erişim yetkiniz yok");
        }
        
        List<Task> tasks = taskRepository.findByProject(project);
        
        return tasks.stream()
                .map(TaskResponse::fromTask)
                .collect(Collectors.toList());
    }
    
    public List<TaskResponse> getUserTasks(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        List<Task> tasks = taskRepository.findByAssignedTo(user);
        
        return tasks.stream()
                .map(TaskResponse::fromTask)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, Task.TaskStatus status, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Görev bulunamadı"));
        
        // Görev durumunu güncelleyebilecek kişileri kontrol et (görev sahibi veya yönetici)
        Project project = task.getProject();
        if (!task.getAssignedTo().getId().equals(user.getId()) && 
            !project.getAdmins().contains(user) && 
            !project.getCreator().getId().equals(user.getId())) {
            throw new RuntimeException("Bu görevi güncelleme yetkiniz yok");
        }
        
        task.updateStatus(status);
        
        return TaskResponse.fromTask(taskRepository.save(task));
    }
}
