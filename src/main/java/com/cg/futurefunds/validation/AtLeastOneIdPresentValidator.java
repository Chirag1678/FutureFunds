package com.cg.futurefunds.validation;

import com.cg.futurefunds.dto.NotificationDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneIdPresentValidator implements ConstraintValidator<AtLeastOne, NotificationDTO> {
    @Override
    public boolean isValid(NotificationDTO dto, ConstraintValidatorContext context) {
        return dto.getInvestmentId() != null || dto.getGoalId() != null;
    }
}

