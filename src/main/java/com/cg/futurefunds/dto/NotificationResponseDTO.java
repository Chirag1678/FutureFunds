package com.cg.futurefunds.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NotificationResponseDTO {
    private String title;
    private String message;
    private String scheduledAt;
    private String type;
}
