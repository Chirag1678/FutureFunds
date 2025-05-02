package com.cg.futurefunds.dto;

import com.cg.futurefunds.validation.AtLeastOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AtLeastOne
@Data
@RequiredArgsConstructor
public class NotificationDTO {
    private Long investmentId;
    private Long goalId;

    @NotBlank(message = "Title cannot be empty")
    @Size(min = 4, max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Message cannot be empty")
    @Size(min = 4, max = 1000, message = "Message must not exceed 1000 characters")
    private String message;

    @NotBlank(message = "Type cannot be empty")
    @Size(min = 4, max = 100, message = "Type must not exceed 100 characters")
    private String type;
}
