package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.GoalDTO;
import com.cg.futurefunds.dto.ResponseDTO;

public interface GoalService {
    ResponseDTO addGoal(GoalDTO goalDTO);

    ResponseDTO updateGoal(Long goalId, GoalDTO goalDTO);

    ResponseDTO deleteGoal(Long goalId);

    ResponseDTO getGoal(Long goalId);

    ResponseDTO getAllGoals(Long userId);
}
