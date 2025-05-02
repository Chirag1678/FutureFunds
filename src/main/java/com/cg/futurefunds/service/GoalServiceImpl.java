package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.GoalDTO;
import com.cg.futurefunds.dto.NotificationDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.exceptions.FutureFundsException;
import com.cg.futurefunds.model.Goal;
import com.cg.futurefunds.model.NotificationType;
import com.cg.futurefunds.model.User;
import com.cg.futurefunds.repository.GoalRepository;
import com.cg.futurefunds.repository.InvestmentPlanRepository;
import com.cg.futurefunds.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoalServiceImpl implements GoalService {
    @Autowired
    private InvestmentPlanRepository investmentPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public ResponseDTO addGoal(GoalDTO goalDTO) {
        Goal goal = new Goal();
        goal.setName(goalDTO.getName());
        goal.setTarget_value(goalDTO.getTargetValue());
        goal.setProgress(goalDTO.getProgress());
        goal.setTarget_date(goalDTO.getTargetDate());

        goal.setUser(userRepository.findById(goalDTO.getInvestmentId())
                .orElseThrow(() -> new FutureFundsException("User not found")));

        Goal savedGoal = goalRepository.save(goal);

        ResponseDTO notificationResponse = createNotification(goal.getId(), "Goal Created Successfully", "New goal created successfully", NotificationType.GOAL_CREATED);
        if(notificationResponse.getStatusCode()==200) {
            return new ResponseDTO("Goal added successfully", 201, savedGoal);
        } else {
            throw new FutureFundsException("Goal creation failed");
        }
    }

    @Override
    public ResponseDTO updateGoal(Long goalId, GoalDTO goalDTO) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new FutureFundsException("Goal with Id " + goalId + " not found"));

        goal.setName(goalDTO.getName());
        goal.setProgress(goalDTO.getProgress());
        goal.setTarget_value(goalDTO.getTargetValue());
        goal.setTarget_date(goalDTO.getTargetDate());

        Goal updatedGoal = goalRepository.save(goal);

        return new ResponseDTO("Goal updated successfully", 200, updatedGoal);
    }
    @Override
    public ResponseDTO deleteGoal(Long goalId) {
        try {
            Goal goal = goalRepository.findById(goalId)
                    .orElseThrow(() -> new FutureFundsException("Goal with id: " + goalId + " not found."));
            goalRepository.delete(goal);
            return new ResponseDTO("Goal deleted successfully", 200, null);
        } catch (Exception e) {
            throw new FutureFundsException("Error deleting goal: " + e.getMessage());
        }
    }

    @Override
    public ResponseDTO getGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new FutureFundsException("Goal with id: " + goalId + " not found."));
        return new ResponseDTO("Goal retrieved successfully", 200, goal);
    }
    @Override
    public ResponseDTO getAllGoals(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new FutureFundsException("User with id: " + userId + " not found."));
            List<Goal> goals = goalRepository.findByUser(user);
            return new ResponseDTO("Goals retrieved successfully", 200, goals);
        } catch (Exception e) {
            throw new FutureFundsException("Error retrieving goals: " + e.getMessage());
        }
    }

    public ResponseDTO createNotification(@Valid Long goalId, @Valid String title, @Valid String message, @Valid NotificationType type) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setGoalId(goalId);
        notificationDTO.setTitle(title);
        notificationDTO.setMessage(message);
        notificationDTO.setType(type);
        String scheduledAt = LocalDateTime.now().toString(); // e.g. "2025-05-02T15:30:00"
        notificationDTO.setScheduledAt(scheduledAt);
        try {
            notificationService.createNotification(notificationDTO);
            return new ResponseDTO("Notification created successfully", 200, null);
        } catch (Exception e) {
            throw new FutureFundsException("Notification creation failed");
        }
    }
}

   