package com.collab.collab.controller;

import com.collab.collab.dto.InvitationActionRequest;
import com.collab.collab.dto.InvitationResponse;
import com.collab.collab.service.InvitationService;
import com.collab.collab.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;
    
    @GetMapping
    public ResponseEntity<List<InvitationResponse>> getPendingInvitations() {
        String email = SecurityUtils.getCurrentUserEmail();
        List<InvitationResponse> invitations = invitationService.getUserPendingInvitations(email);
        return ResponseEntity.ok(invitations);
    }
    
    @PostMapping("/{id}/respond")
    public ResponseEntity<InvitationResponse> respondToInvitation(
            @PathVariable Long id,
            @Valid @RequestBody InvitationActionRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        InvitationResponse response = invitationService.respondToInvitation(id, request, email);
        return ResponseEntity.ok(response);
    }
}
