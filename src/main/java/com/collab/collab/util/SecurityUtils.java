package com.collab.collab.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    /**
     * Oturum açmış kullanıcının email adresini güvenlik kontekstinden alır.
     * @return Kullanıcı email adresi veya null (oturum açılmamışsa)
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername(); // Spring Security'de email, username olarak saklanır
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        
        return null;
    }
}
