package com.collab.collab.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationActionRequest {
    
    @NotNull(message = "Davet yanıtı gereklidir")
    private boolean accept;
}
