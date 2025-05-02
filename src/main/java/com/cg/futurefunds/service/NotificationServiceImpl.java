package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.InvestmentResponseDTO;
import com.cg.futurefunds.dto.NotificationDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.dto.UserResponseDTO;
import com.cg.futurefunds.exceptions.FutureFundsException;
import com.cg.futurefunds.model.Goal;
import com.cg.futurefunds.model.InvestmentPlan;
import com.cg.futurefunds.model.Notification;
import com.cg.futurefunds.repository.GoalRepository;
import com.cg.futurefunds.repository.InvestmentPlanRepository;
import com.cg.futurefunds.repository.NotificationRepository;
import com.cg.futurefunds.utility.MailSenderUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService{
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private InvestmentPlanRepository investmentPlanRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private MailSenderUtility mailSenderUtility;

    @Override
    public ResponseDTO createNotification(NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        if(notificationDTO.getInvestmentId() != null) {
            InvestmentPlan investmentPlan = investmentPlanRepository.findById(notificationDTO.getInvestmentId())
                    .orElseThrow(() -> new FutureFundsException("Investment with Id" + notificationDTO.getInvestmentId() + "not found"));
            notification.setInvestmentPlan(investmentPlan);
            notification.setUser(investmentPlan.getUser());
        }

        if(notificationDTO.getGoalId() != null) {
            Goal goal = goalRepository.findById(notificationDTO.getGoalId())
                    .orElseThrow(() -> new FutureFundsException("Goal with Id" + notificationDTO.getGoalId() + "not found"));
            notification.setGoal(goal);
            notification.setUser(goal.getUser());
        }
        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notification.setType(notificationDTO.getType());
        notification.setScheduledAt(LocalDateTime.parse(notificationDTO.getScheduledAt()));

        notificationRepository.save(notification);

        return new ResponseDTO("Notification created successfully", 201, null);
    }

    @Override
    public ResponseDTO updateNotification(Long notificationId, NotificationDTO notificationDTO) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new FutureFundsException("Notfication with id "+notificationId+" not found"));

                if(notificationDTO.getInvestmentId()!=null){
                    InvestmentPlan investmentPlan = investmentPlanRepository.findById(notificationDTO.getInvestmentId())
                            .orElseThrow(() -> new FutureFundsException("Investment with id "+notificationDTO.getInvestmentId()+" not found"));

                notification.setInvestmentPlan(investmentPlan);
                notification.setUser(investmentPlan.getUser());
                }
                if(notificationDTO.getGoalId()!=null){
                    Goal goal = goalRepository.findById(notificationDTO.getGoalId())
                            .orElseThrow(() -> new FutureFundsException("Goal with id "+notificationDTO.getGoalId()+" not found"));
                    notification.setGoal(goal);
                    notification.setUser(goal.getUser());
                }
                notification.setTitle(notificationDTO.getTitle());
                notification.setMessage(notificationDTO.getMessage());
                notification.setType(notificationDTO.getType());
                notification.setScheduledAt(LocalDateTime.parse(notificationDTO.getScheduledAt()));
                notificationRepository.save(notification);

                return  new ResponseDTO("Notification updated successfully",200,notification);
    }

    @Override
    public ResponseDTO deleteNotification(Long notificationId) {
            Notification notification=notificationRepository.findById(notificationId)
                    .orElseThrow(()->new FutureFundsException("No notification found with given id "));
            notificationRepository.delete(notification);
            return new ResponseDTO("Notification Deleted successfully", 201, null);

    }

    @Override
    public ResponseDTO sendNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new FutureFundsException("Notification with id "+notificationId+" not found"));

        String to = notification.getUser().getEmail();
        String subject = notification.getTitle();
        String body = notification.getMessage();

        if(notification.getInvestmentPlan()!=null) {
            InvestmentResponseDTO investmentResponseDTO = convertToResponse(notification.getInvestmentPlan());
            body += "\n\nInvestment Plan Details:\n" + investmentResponseDTO;
        }

        try {
            mailSenderUtility.sendEmail(to, subject, body);
            deleteNotification(notificationId);
            return new ResponseDTO("Notification sent successfully", 200, null);
        } catch (Exception e) {
            throw new FutureFundsException("Failed to send email: " + e.getMessage());
        }
    }

    public InvestmentResponseDTO convertToResponse(InvestmentPlan investmentPlan) {
        InvestmentResponseDTO investmentResponseDTO = new InvestmentResponseDTO();
        investmentResponseDTO.setName(investmentPlan.getName());
        investmentResponseDTO.setType(investmentPlan.getType());
        investmentResponseDTO.setMonthlyAmount(investmentPlan.getMonthly_amount());
        investmentResponseDTO.setExpectedReturn(investmentPlan.getExpected_return());
        investmentResponseDTO.setDurationMonths(investmentPlan.getDuration_months());
        investmentResponseDTO.setTargetAmount(investmentPlan.getTarget_amount());
        investmentResponseDTO.setCurrentValue(investmentPlan.getCurrent_value());
        investmentResponseDTO.setStartDate(investmentPlan.getStartDate());

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setName(investmentPlan.getUser().getName());
        userResponseDTO.setEmail(investmentPlan.getUser().getEmail());
        userResponseDTO.setVerified(investmentPlan.getUser().isVerified());
        investmentResponseDTO.setUser(userResponseDTO);

        return investmentResponseDTO;
    }
}
