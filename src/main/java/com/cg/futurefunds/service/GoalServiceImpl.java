package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.GoalDTO;
import com.cg.futurefunds.dto.NotificationDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.exceptions.FutureFundsException;
import com.cg.futurefunds.model.Goal;
import com.cg.futurefunds.model.InvestmentPlan;
import com.cg.futurefunds.model.NotificationType;
import com.cg.futurefunds.model.User;
import com.cg.futurefunds.repository.GoalRepository;
import com.cg.futurefunds.repository.InvestmentPlanRepository;
import com.cg.futurefunds.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        InvestmentPlan investmentPlan = investmentPlanRepository.findById(goalDTO.getInvestmentId())
                        .orElseThrow(() -> new FutureFundsException("Investment Plan with id: " + goalDTO.getInvestmentId() + " not found."));

        User user = userRepository.findById(investmentPlan.getUser().getId())
                        .orElseThrow(() -> new FutureFundsException("User with id: " + investmentPlan.getUser().getId() + " not found."));

        Goal goal = new Goal();
        goal.setName(goalDTO.getName());
        goal.setTarget_value(investmentPlan.getTarget_amount());
        double progress = (investmentPlan.getCurrent_value() / investmentPlan.getTarget_amount()) * 100;
        double formattedProgress = BigDecimal.valueOf(progress).setScale(2, RoundingMode.HALF_UP).doubleValue();
        goal.setProgress(formattedProgress);
        goal.setTarget_date(investmentPlan.getEndDate());
        goal.setInvestment(investmentPlan.getId());
        goal.setMilestone("null");
        goal.setUser(user);
        investmentPlan.setGoal(goal);

        goalRepository.save(goal);
        investmentPlanRepository.save(investmentPlan);

        ResponseDTO notificationResponse = createNotification(goal.getId(), "Goal Created Successfully", "New goal created successfully", NotificationType.GOAL_CREATED);
        if(notificationResponse.getStatusCode() == HttpStatus.OK.value()) {
            return new ResponseDTO("Goal added successfully", HttpStatus.CREATED.value(), goal);
        } else {
            throw new FutureFundsException("Goal creation failed");
        }
    }

    @Override
    public ResponseDTO updateGoal(Long goalId, GoalDTO goalDTO) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new FutureFundsException("Goal with Id " + goalId + " not found"));

        goal.setName(goalDTO.getName());

        goalRepository.save(goal);

        ResponseDTO notificationResponse = createNotification(goal.getId(), "Goal Updated Successfully", "Goal updated successfully", NotificationType.GOAL_UPDATED);
        if(notificationResponse.getStatusCode() == HttpStatus.OK.value()) {
            return new ResponseDTO("Goal updated successfully", HttpStatus.OK.value(), goal);
        } else {
            throw new FutureFundsException("Goal update failed");
        }
    }
    @Override
    public ResponseDTO deleteGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new FutureFundsException("Goal with id: " + goalId + " not found."));

        InvestmentPlan investmentPlan = goal.getInvestment() != null ? investmentPlanRepository.findById(goal.getInvestment())
                .orElseThrow(() -> new FutureFundsException("Investment Plan with id: " + goal.getInvestment() + " not found.")) : null;

        String goalName = goal.getName();

        ResponseDTO notificationResponse = createNotification(
                goalId,
                "Goal Deleted",
                "Your goal " + goalName + "' has been deleted successfully.",
                NotificationType.GOAL_DELETED
        );

        if (notificationResponse.getStatusCode() == HttpStatus.OK.value()) {
            investmentPlan.setGoal(null);
            investmentPlanRepository.save(investmentPlan);
            goalRepository.delete(goal);
            return new ResponseDTO("Goal deleted successfully", HttpStatus.OK.value(), null);
        } else {
            throw new FutureFundsException("Goal deletion succeeded, but notification failed");
        }
    }

    @Override
    public ResponseDTO getGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new FutureFundsException("Goal with id: " + goalId + " not found."));

        return new ResponseDTO("Goal retrieved successfully", HttpStatus.OK.value(), goal);
    }

    @Override
    public ResponseDTO getAllGoals(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new FutureFundsException("User with id: " + userId + " not found."));

        List<Goal> goals = goalRepository.findByUser(user);

        if(goals.isEmpty()) {
            return new ResponseDTO("No goals found", HttpStatus.OK.value(), null);
        } else {
            return new ResponseDTO("Goals retrieved successfully", HttpStatus.OK.value(), goals);
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
            return new ResponseDTO("Notification created successfully", HttpStatus.OK.value(), null);
        } catch (Exception e) {
            throw new FutureFundsException("Notification creation failed");
        }
    }
}

   