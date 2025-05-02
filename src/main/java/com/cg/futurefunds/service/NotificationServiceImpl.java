package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.NotificationDTO;
import com.cg.futurefunds.dto.ResponseDTO;
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

        notificationRepository.save(notification);

        return new ResponseDTO("Notification created successfully", 201, notification);
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
                if(notificationDTO.getTitle()!=null) {
                    notification.setTitle(notificationDTO.getTitle());
                }
                if(notificationDTO.getMessage()!=null){
                    notification.setMessage(notificationDTO.getMessage());
                }
                if(notificationDTO.getType()!=null){
                    notification.setType(notificationDTO.getType());
                }
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
    public ResponseDTO scheduleNotification(Long notificationId) {
        return null;
    }
}
