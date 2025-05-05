package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.*;
import com.cg.futurefunds.exceptions.FutureFundsException;
import com.cg.futurefunds.model.*;
import com.cg.futurefunds.repository.GoalRepository;
import com.cg.futurefunds.repository.InvestmentPlanRepository;
import com.cg.futurefunds.repository.NotificationRepository;
import com.cg.futurefunds.repository.UserRepository;
import com.cg.futurefunds.utility.MailSenderUtility;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService{
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private InvestmentPlanRepository investmentPlanRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailSenderUtility mailSenderUtility;

    @Override
    public ResponseDTO createNotification(NotificationDTO notificationDTO) {
        Notification notification = new Notification();

        if(notificationDTO.getInvestmentId() != null) {
            InvestmentPlan investmentPlan = investmentPlanRepository.findById(notificationDTO.getInvestmentId())
                    .orElseThrow(() -> new FutureFundsException("Investment with Id" + notificationDTO.getInvestmentId() + "not found"));

            notification.setInvestment(investmentPlan.getId());
            notification.setUser(investmentPlan.getUser().getId());
        } else if(notificationDTO.getGoalId() != null) {
            Goal goal = goalRepository.findById(notificationDTO.getGoalId())
                    .orElseThrow(() -> new FutureFundsException("Goal with Id" + notificationDTO.getGoalId() + "not found"));

            notification.setGoal(goal.getId());
            notification.setUser(goal.getUser().getId());
        } else if(notificationDTO.getUserId() != null) {
            notification.setUser(notificationDTO.getUserId());
        }
        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notification.setType(notificationDTO.getType());
        notification.setScheduledAt(LocalDateTime.parse(notificationDTO.getScheduledAt()));

        notificationRepository.save(notification);

        return new ResponseDTO("Notification created successfully", HttpStatus.CREATED.value(), null);
    }

    @Override
    public ResponseDTO updateNotification(Long notificationId, NotificationDTO notificationDTO) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new FutureFundsException("Notification with id "+notificationId+" not found"));

                if(notificationDTO.getInvestmentId()!=null){
                    InvestmentPlan investmentPlan = investmentPlanRepository.findById(notificationDTO.getInvestmentId())
                            .orElseThrow(() -> new FutureFundsException("Investment with id "+notificationDTO.getInvestmentId()+" not found"));

                    notification.setInvestment(investmentPlan.getId());
                    notification.setUser(investmentPlan.getUser().getId());
                } else if(notificationDTO.getGoalId()!=null){
                    Goal goal = goalRepository.findById(notificationDTO.getGoalId())
                            .orElseThrow(() -> new FutureFundsException("Goal with id "+notificationDTO.getGoalId()+" not found"));

                    notification.setGoal(goal.getId());
                    notification.setUser(goal.getUser().getId());
                } else if(notificationDTO.getUserId()!=null){
                    notification.setUser(notificationDTO.getUserId());
                }
                notification.setTitle(notificationDTO.getTitle());
                notification.setMessage(notificationDTO.getMessage());
                notification.setType(notificationDTO.getType());
                notification.setScheduledAt(LocalDateTime.parse(notificationDTO.getScheduledAt()));
                notificationRepository.save(notification);

                return  new ResponseDTO("Notification updated successfully",HttpStatus.OK.value(), notification);
    }

    @Override
    public ResponseDTO deleteNotification(Long notificationId) {
            Notification notification=notificationRepository.findById(notificationId)
                    .orElseThrow(()->new FutureFundsException("No notification found with given id "));

            notificationRepository.delete(notification);
            return new ResponseDTO("Notification Deleted successfully", HttpStatus.OK.value(), null);
    }

    @Override
    public ResponseDTO sendNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new FutureFundsException("Notification with id "+notificationId+" not found"));

        String to = userRepository.findById(notification.getUser())
                .orElseThrow(() -> new FutureFundsException("User with id: " + notification.getUser() + " does not exists.")).getEmail();
        String subject = notification.getTitle();
        String body = notification.getMessage();

        if(notification.getInvestment()!=null) {
            if(notification.getType()!= NotificationType.INVESTMENT_DELETED) {
                InvestmentResponseDTO investmentResponseDTO = convertToResponse(investmentPlanRepository.findById(notification.getInvestment())
                        .orElseThrow(() -> new FutureFundsException("Investment Plan with id: " + notification.getInvestment() + " not exits.")));
                body += "\n\nInvestment Plan Details:\n<p>" + buildInvestmentHtmlTable(investmentResponseDTO) + "</p>" ;
            }
            try {
                mailSenderUtility.sendHtmlEmail(to, subject, body);
                deleteNotification(notificationId);

                return new ResponseDTO("Notification sent successfully", HttpStatus.OK.value(), null);
            } catch (Exception e) {
                throw new FutureFundsException("Failed to send email: " + e.getMessage());
            }
        } else if(notification.getGoal()!=null) {
            if (notification.getType() != NotificationType.GOAL_DELETED) {
                Goal goal = goalRepository.findById(notification.getGoal())
                        .orElseThrow(() -> new FutureFundsException("Goal with id: " + notification.getGoal() + " not exits."));
                body += "\n\nGoal Details:\nGoal Name: " + goal.getName() + "\n" + "Goal Progress: " + goal.getProgress() + "%";
            }
            try {
                mailSenderUtility.sendEmail(to, subject, body);
                deleteNotification(notificationId);

                return new ResponseDTO("Notification sent successfully", HttpStatus.OK.value(), null);
            } catch (Exception e) {
                throw new FutureFundsException("Failed to send email: " + e.getMessage());
            }
        } else if(notification.getUser()!=null) {
            try {
                mailSenderUtility.sendEmail(to, subject, body);
                deleteNotification(notificationId);

                return new ResponseDTO("Notification sent successfully", HttpStatus.OK.value(), null);
            } catch (Exception e) {
                throw new FutureFundsException("Failed to send email: " + e.getMessage());
            }
        } else {
            throw new FutureFundsException("Notification does not have Investment Plan or Goal associated with it");
        }
    }

    @Override
    public ResponseDTO getAllNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new FutureFundsException("User with id: " + userId + " does not exist."));

        List<Notification> notifications = notificationRepository.findByUser(userId);

        if (notifications.isEmpty()) {
            return new ResponseDTO("No notifications found", HttpStatus.OK.value(), null);
        } else {
            List<NotificationResponseDTO> responseList = notifications.stream()
                    .map(this::convertToNotificationResponse)
                    .toList();

            return new ResponseDTO("Notifications found", HttpStatus.OK.value(), responseList);
        }
    }


    private String buildInvestmentHtmlTable(InvestmentResponseDTO dto) {
        return "<h3>Investment Plan Details</h3>" +
                "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse; font-family: Arial;'>" +
                "<tr><th>Field</th><th>Value</th></tr>" +
                "<tr><td>Plan Name</td><td>" + dto.getName() + "</td></tr>" +
                "<tr><td>Amount Invested</td><td>₹" + dto.getMonthlyAmount() + "</td></tr>" +
                "<tr><td>Start Date</td><td>" + dto.getStartDate() + "</td></tr>" +
                "<tr><td>Expected Returns</td><td>" + dto.getExpectedReturn() + "%</td></tr>" +
                "<tr><td>Duration </td><td>" + dto.getDurationMonths() + "months" + "</td></tr>" +
                "<tr><td>Maturity Amount </td><td>" + dto.getTargetAmount() + "</td></tr>" +
                "<tr><td>Current Value </td><td>" + dto.getCurrentValue() + "</td></tr>" +
                "<tr><td>Start Date</td><td>" + dto.getStartDate() + "</td></tr>" +
                "<tr><td>User Name</td><td>" + dto.getUser().getName() + "</td></tr>" +
                "<tr><td>User Email</td><td>" + dto.getUser().getEmail() + "</td></tr>" +
                "</table>";
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

    public NotificationResponseDTO convertToNotificationResponse(Notification notification) {
        NotificationResponseDTO notificationResponseDTO = new NotificationResponseDTO();
        notificationResponseDTO.setTitle(notification.getTitle());
        notificationResponseDTO.setMessage(notification.getMessage());
        notificationResponseDTO.setScheduledAt(notification.getScheduledAt().toString());
        notificationResponseDTO.setType(notification.getType().toString());

        return notificationResponseDTO;
    }

    public ResponseDTO sendPdfNotification(String to, String filePath) {
        try {
            mailSenderUtility.sendPdf(to, filePath);
        } catch (MessagingException e) {
            throw new FutureFundsException("Failed to send email: " + e.getMessage());
        }

        return new ResponseDTO("Email sent successfully", HttpStatus.OK.value(), null);
    }
}
