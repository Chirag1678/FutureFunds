package com.cg.futurefunds.dto;

import com.cg.futurefunds.model.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDTO {
    private Long investmentId;
    private Long goalId;
    private Long userId;

    @NotBlank(message = "Title cannot be empty")
    @Size(min = 4, max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Message cannot be empty")
    @Size(min = 4, message = "Message must be at least 4 characters")
    private String message;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    private String scheduledAt;
}
