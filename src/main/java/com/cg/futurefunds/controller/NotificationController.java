package com.cg.futurefunds.controller;

import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.service.NotificationService;
import com.cg.futurefunds.utility.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtUtility jwtUtility;

    @GetMapping("/user")
    public ResponseEntity<ResponseDTO> getNotifications(HttpServletRequest request) {
        String token = getToken(request);
        Long userId = jwtUtility.extractUserId(token);
        ResponseDTO responseDTO = notificationService.getAllNotifications(userId);

        return new ResponseEntity<>(responseDTO, org.springframework.http.HttpStatusCode.valueOf(responseDTO.getStatusCode()));
    }

    public String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
