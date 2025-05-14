package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.NotificationDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.exceptions.FutureFundsException;
import com.cg.futurefunds.model.Goal;
import com.cg.futurefunds.model.InvestmentPlan;
import com.cg.futurefunds.model.NotificationType;
import com.cg.futurefunds.repository.GoalRepository;
import com.cg.futurefunds.repository.InvestmentPlanRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private InvestmentPlanRepository investmentPlanRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public ResponseDTO payment(Long investmentId) {
        InvestmentPlan investmentPlan = investmentPlanRepository.findById(investmentId)
                .orElseThrow(() -> new FutureFundsException("Investment Plan with id: " + investmentId + " not found."));

        Goal goal = investmentPlan.getGoal();

        double paymentDone = (investmentPlan.getMonthly_amount() * (investmentPlan.getExpected_return() / 1200)) + investmentPlan.getMonthly_amount();
        BigDecimal updatedValue = BigDecimal.valueOf(investmentPlan.getCurrent_value() + paymentDone)
                .setScale(2, RoundingMode.HALF_UP);
        investmentPlan.setCurrent_value(updatedValue.doubleValue());

        investmentPlan.setMonths_passed(investmentPlan.getMonths_passed() + 1);
        investmentPlan.setNextPaymentDate(investmentPlan.getNextPaymentDate().plusMonths(1));

        if(goal != null) {
            double progress = (investmentPlan.getCurrent_value() / goal.getTarget_value()) * 100;

            BigDecimal progressRounded = BigDecimal.valueOf(progress).setScale(2, RoundingMode.HALF_UP);
            goal.setProgress(progressRounded.doubleValue());
            goalRepository.save(goal);
        }

        String title = "Payment Confirmation";
        String monthPaid = investmentPlan.getNextPaymentDate().minusMonths(1).getMonth().toString();
        String message = "Your payment for " + investmentPlan.getName() + " (" + investmentPlan.getType() + ") for the month of " + monthPaid + " is successful.";
        ResponseDTO notificationResponse = createNotification(investmentId, title, message, NotificationType.INVESTMENT_PAYMENT_SUCCESSFUL);

        if(notificationResponse.getStatusCode() == HttpStatus.OK.value()) {
            investmentPlanRepository.save(investmentPlan);
            return new ResponseDTO("Payment Processed successfully", HttpStatus.OK.value(), null);
        } else {
            throw new FutureFundsException("Unable to process payment");
        }
    }

    @Override
    public ResponseDTO dueDateNotification() {
        LocalDate today = LocalDate.now();

        List<InvestmentPlan> investmentPlans = investmentPlanRepository.findAll();

        for (InvestmentPlan investmentPlan : investmentPlans) {
            if (investmentPlan.getNextPaymentDate().isEqual(today.plusDays(3))) {
                String title = "Due Date Reminder";
                String message = "Your payment for " + investmentPlan.getName() + " (" + investmentPlan.getType() + ") is due in 3 days.";
                ResponseDTO notificationResponse = createNotification(investmentPlan.getId(), title, message, NotificationType.INVESTMENT_DUE_DATE_REMINDER);

                if (notificationResponse.getStatusCode() != HttpStatus.OK.value()) {
                    throw new FutureFundsException("Unable to send due date notification");
                }
            }
        }

        return new ResponseDTO("Due date notifications sent successfully", HttpStatus.OK.value(), null);
    }

    @Override
    public ResponseDTO paymentConfirmationNotification() {
        LocalDate today = LocalDate.now();

        List<InvestmentPlan> investmentPlans = investmentPlanRepository.findAll();

        for (InvestmentPlan investmentPlan : investmentPlans) {
            if (investmentPlan.getNextPaymentDate().isEqual(today)) {
                return payment(investmentPlan.getId());
            }
        }

        return new ResponseDTO("Payment confirmation notifications sent successfully", HttpStatus.OK.value(), null);
    }

    @Override
    public ResponseDTO investmentMaturedNotification() {
        LocalDate today = LocalDate.now();

        List<InvestmentPlan> investmentPlans = investmentPlanRepository.findAll();

        for (InvestmentPlan investmentPlan : investmentPlans) {
            if (investmentPlan.getEndDate().isEqual(today)) {
                String title = "Investment Maturity";
                String message = "Your investment plan for " + investmentPlan.getName() + " (" + investmentPlan.getType() + ") has matured. You can now withdraw your funds.";
                ResponseDTO notificationResponse = createNotification(investmentPlan.getId(), title, message, NotificationType.INVESTMENT_MATURED);

                if (notificationResponse.getStatusCode() != HttpStatus.OK.value()) {
                    throw new FutureFundsException("Unable to send matured investment notification");
                }
            }
        }

        return new ResponseDTO("Investment maturity notifications sent successfully", HttpStatus.OK.value(), null);
    }

    @Override
    public ResponseDTO goalAchievementNotification() {
        List<Goal> goals = goalRepository.findAll();
        for (Goal goal : goals) {
            String message = null;
            String title = "Goal Achievement";
            NotificationType type = null;
            if (goal.getProgress() == 100 && (goal.getMilestone().equals("null") || goal.getMilestone().equals("25%") || goal.getMilestone().equals("50%") || goal.getMilestone().equals("75%"))) {
                message = "Your progress on goal " + goal.getName() + " has reached 100%. You can now withdraw your funds.";
                type = NotificationType.GOAL_MILESTONE_100_PERCENT;
            } else if (goal.getProgress() >= 75 && (goal.getMilestone().equals("null") || goal.getMilestone().equals("25%") || goal.getMilestone().equals("50%"))) {
                message = "Your progress on goal " + goal.getName() + " has reached 75%. Keep going!";
                goal.setMilestone("75%");
                type = NotificationType.GOAL_MILESTONE_75_PERCENT;
            } else if (goal.getProgress() >= 50 && (goal.getMilestone().equals("null") || goal.getMilestone().equals("25%"))) {
                message = "Your progress on goal " + goal.getName() + " has reached 50%. You're halfway there!";
                goal.setMilestone("50%");
                type = NotificationType.GOAL_MILESTONE_50_PERCENT;
            } else if (goal.getProgress() >= 25 && goal.getMilestone().equals("null")) {
                message = "Your progress on goal " + goal.getName() + " has reached 25%. Great start!";
                goal.setMilestone("25%");
                type = NotificationType.GOAL_MILESTONE_25_PERCENT;
            }
            goalRepository.save(goal);

            if (message != null) {
                ResponseDTO notificationResponse = createNotificationGoal(goal.getId(), title, message, type);

                if (notificationResponse.getStatusCode() != HttpStatus.OK.value()) {
                    throw new FutureFundsException("Unable to send goal achievement notification for goal ID: " + goal.getId());
                }
            }
        }

        return new ResponseDTO("Goal achievement notifications sent successfully", HttpStatus.OK.value(), null);
    }


    public ResponseDTO createNotification(@Valid Long investmentId, @Valid String title, @Valid String message, @Valid NotificationType type) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setInvestmentId(investmentId);
        notificationDTO.setTitle(title);
        notificationDTO.setMessage(message);
        notificationDTO.setType(type);
        String scheduledAt = LocalDateTime.now().toString();
        notificationDTO.setScheduledAt(scheduledAt);
        try {
            notificationService.createNotification(notificationDTO);
            return new ResponseDTO("Notification created successfully", HttpStatus.OK.value(), null);
        } catch (Exception e) {
            throw new FutureFundsException("Notification creation failed");
        }
    }

    public ResponseDTO createNotificationGoal(@Valid Long goalId, @Valid String title, @Valid String message, @Valid NotificationType type) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setGoalId(goalId);
        notificationDTO.setTitle(title);
        notificationDTO.setMessage(message);
        notificationDTO.setType(type);
        String scheduledAt = LocalDateTime.now().toString();
        notificationDTO.setScheduledAt(scheduledAt);
        try {
            notificationService.createNotification(notificationDTO);
            return new ResponseDTO("Notification created successfully", HttpStatus.OK.value(), null);
        } catch (Exception e) {
            throw new FutureFundsException("Notification creation failed");
        }
    }


}
