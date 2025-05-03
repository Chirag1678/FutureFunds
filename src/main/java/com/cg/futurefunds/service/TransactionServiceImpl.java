package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.NotificationDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.exceptions.FutureFundsException;
import com.cg.futurefunds.model.InvestmentPlan;
import com.cg.futurefunds.model.NotificationType;
import com.cg.futurefunds.repository.InvestmentPlanRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private InvestmentPlanRepository investmentPlanRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public ResponseDTO payment(Long investmentId) {
        InvestmentPlan investmentPlan = investmentPlanRepository.findById(investmentId)
                .orElseThrow(() -> new FutureFundsException("Investment Plan with id: " + investmentId + " not found."));

        double paymentDone = (investmentPlan.getMonthly_amount() * (investmentPlan.getExpected_return() / 1200)) + investmentPlan.getMonthly_amount();
        investmentPlan.setCurrent_value(investmentPlan.getCurrent_value() + paymentDone);

        investmentPlan.setMonths_passed(investmentPlan.getMonths_passed() + 1);
        investmentPlan.setNextPaymentDate(investmentPlan.getNextPaymentDate().plusMonths(1));

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
}
