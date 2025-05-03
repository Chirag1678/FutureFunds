package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.*;
import com.cg.futurefunds.exceptions.FutureFundsException;
import com.cg.futurefunds.model.Goal;
import com.cg.futurefunds.model.InvestmentPlan;
import com.cg.futurefunds.model.NotificationType;
import com.cg.futurefunds.model.User;
import com.cg.futurefunds.repository.InvestmentPlanRepository;
import com.cg.futurefunds.repository.UserRepository;
import com.cg.futurefunds.utility.PdfGeneratorUtility;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvestmentServiceImpl implements InvestmentService {
    @Autowired
    private InvestmentPlanRepository investmentPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private GoalService goalService;

    @Autowired
    private PdfGeneratorUtility pdfGeneratorUtility;

    @Override
    public ResponseDTO addInvestment(InvestmentPlanDTO investmentPlanDTO) {
        User user = userRepository.findByEmail(investmentPlanDTO.getUserEmail())
                .orElseThrow(() -> new FutureFundsException("User with email: " + investmentPlanDTO.getUserEmail() + " not exists."));

        double targetAmount = getTargetAmount(investmentPlanDTO.getMonthlyAmount(), investmentPlanDTO.getExpectedReturn(), investmentPlanDTO.getDurationMonths());
        int completedMonths = getCompletedMonths(LocalDate.now());
        double currentValue = calculateCurrentValue(investmentPlanDTO.getMonthlyAmount(), investmentPlanDTO.getExpectedReturn(), completedMonths);

        InvestmentPlan investmentPlan = getInvestmentPlan(investmentPlanDTO, targetAmount, currentValue, user);

        investmentPlanRepository.save(investmentPlan);

        if(investmentPlanDTO.getGoal() != null) {
            GoalDTO goalDTO = new GoalDTO();
            goalDTO.setInvestmentId(investmentPlan.getId());
            goalDTO.setName(investmentPlanDTO.getGoal());
            goalDTO.setProgress(0);
            goalDTO.setTargetValue(targetAmount);
            goalDTO.setTargetDate(investmentPlan.getStartDate().plusMonths(investmentPlanDTO.getDurationMonths()));

            ResponseDTO responseDTO = goalService.addGoal(goalDTO);

            if(responseDTO.getStatusCode() != HttpStatus.CREATED.value()) {
                throw new FutureFundsException("Failed to add goal");
            }
        }

        InvestmentResponseDTO investmentResponseDTO = convertToResponse(investmentPlan);
        investmentResponseDTO.setGoal(investmentPlanDTO.getGoal());

        ResponseDTO notificationResponse = createNotification(investmentPlan.getId(), "Investment Plan Created", "Your investment plan has been created successfully.", NotificationType.INVESTMENT_CREATED);
        if(notificationResponse.getStatusCode() == HttpStatus.OK.value()) {
            return new ResponseDTO("Investment plan added successfully", HttpStatus.OK.value(), investmentResponseDTO);
        } else {
            throw new FutureFundsException("Investment plan creation failed");
        }
    }


    @Override
    public ResponseDTO updateInvestment(Long investmentId, InvestmentPlanDTO investmentPlanDTO) {
        InvestmentPlan investmentPlan = investmentPlanRepository.findById(investmentId)
                .orElseThrow(() ->  new FutureFundsException("Investment with Id" + investmentId + "not found"));

        User user = userRepository.findByEmail(investmentPlanDTO.getUserEmail())
                .orElseThrow(() -> new FutureFundsException("User with Email"+investmentPlanDTO.getUserEmail()+"not found"));

        double targetAmount = getTargetAmount(investmentPlanDTO.getMonthlyAmount(),investmentPlanDTO.getExpectedReturn(),investmentPlanDTO.getDurationMonths());
        int completedMonths = getCompletedMonths(investmentPlan.getStartDate());
        double currentValue = calculateCurrentValue(investmentPlanDTO.getMonthlyAmount(),investmentPlanDTO.getExpectedReturn(),completedMonths);
        investmentPlan.setName(investmentPlanDTO.getName());
        investmentPlan.setType(investmentPlanDTO.getType());
        investmentPlan.setMonthly_amount(investmentPlanDTO.getMonthlyAmount());
        investmentPlan.setExpected_return(investmentPlanDTO.getExpectedReturn());
        investmentPlan.setDuration_months(investmentPlanDTO.getDurationMonths());
        investmentPlan.setTarget_amount(targetAmount);
        investmentPlan.setCurrent_value(currentValue);
        investmentPlan.setUser(user);

        investmentPlanRepository.save(investmentPlan);

        if(investmentPlanDTO.getGoal() != null) {
            GoalDTO goalDTO = new GoalDTO();
            goalDTO.setInvestmentId(investmentPlan.getId());
            goalDTO.setName(investmentPlanDTO.getGoal());
            goalDTO.setProgress(investmentPlan.getCurrent_value() / investmentPlan.getTarget_amount() * 100.0);
            goalDTO.setTargetValue(targetAmount);
            goalDTO.setTargetDate(investmentPlan.getStartDate().plusMonths(investmentPlanDTO.getDurationMonths()));

            Long goalId = investmentPlan.getGoal().getId();
            ResponseDTO responseDTO = goalService.updateGoal(goalId, goalDTO);

            if(responseDTO.getStatusCode() != HttpStatus.OK.value()) {
                throw new FutureFundsException("Failed to update goal");
            }
        }

        InvestmentResponseDTO responseDTO = convertToResponse(investmentPlan);
        responseDTO.setGoal(investmentPlanDTO.getGoal());

        ResponseDTO notificationResponse = createNotification(investmentPlan.getId(), "Investment Plan Updated", "Your investment plan has been updated successfully.", NotificationType.INVESTMENT_UPDATED);
        if(notificationResponse.getStatusCode() == HttpStatus.OK.value()) {
            return new ResponseDTO("Investment plan updated successfully", HttpStatus.OK.value(), responseDTO);
        } else {
            throw new FutureFundsException("Investment plan update failed");
        }
    }

    @Override
    public ResponseDTO deleteInvestment(Long investmentId) {
        InvestmentPlan investmentPlan = investmentPlanRepository.findById(investmentId)
                .orElseThrow(() -> new FutureFundsException("Investment with Id " + investmentId + " not found"));

        String investmentName = investmentPlan.getName();

        ResponseDTO notificationResponse = createNotification(
                investmentId,
                "Investment Plan Deleted",
                "Your investment plan '" + investmentName + "' has been deleted successfully.",
                NotificationType.INVESTMENT_DELETED
        );

        if (notificationResponse.getStatusCode() == HttpStatus.OK.value()) {
            investmentPlanRepository.delete(investmentPlan);
            return new ResponseDTO("Investment plan deleted successfully", HttpStatus.OK.value(), null);
        } else {
            throw new FutureFundsException("Investment plan deletion succeeded, but notification failed");
        }
    }


    @Override
    public ResponseDTO getAllInvestments(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new FutureFundsException("User with id"+ userId +"does not exist"));

        List<InvestmentPlan> plans = investmentPlanRepository.findByUserId(userId);

        if(plans.isEmpty()){
            return  new ResponseDTO("No Investment plans found by user",HttpStatus.NOT_FOUND.value(), null);
        }
        List<InvestmentResponseDTO> investmentResponseDTOList = plans.stream()
                .map(this::convertToResponse)
                .toList();

        return new ResponseDTO("Investment Plans Retrieved successfully", HttpStatus.OK.value(), investmentResponseDTOList);

    }

    @Override
    public ResponseDTO getInvestment(Long investmentId) {
        InvestmentPlan investmentPlan=investmentPlanRepository.findById(investmentId)
                .orElseThrow(() -> new FutureFundsException("Investment with Id" + investmentId + "not found"));

        InvestmentResponseDTO investmentResponseDTO = convertToResponse(investmentPlan);

        return new ResponseDTO("Investment Plan Details", HttpStatus.OK.value(), investmentResponseDTO);
    }

    @Override
    public ResponseDTO simulateInvestment(InvestmentPlanDTO investmentPlanDTO) {
        User user = userRepository.findByEmail(investmentPlanDTO.getUserEmail())
                .orElseThrow(() -> new FutureFundsException("User with Email"+investmentPlanDTO.getUserEmail()+"not found"));

        double targetAmount = getTargetAmount(investmentPlanDTO.getMonthlyAmount(),investmentPlanDTO.getExpectedReturn(),investmentPlanDTO.getDurationMonths());

        return new ResponseDTO("Simulation Completed\n Expected returns: " + targetAmount, HttpStatus.OK.value(), null);
    }

    @Override
    public ResponseDTO progressInvestment(Long investmentId) {
        InvestmentPlan investmentPlan = investmentPlanRepository.findById(investmentId)
                .orElseThrow(() -> new FutureFundsException("Investment with Id" + investmentId + "not found"));

        double progress = investmentPlan.getCurrent_value() / investmentPlan.getTarget_amount() * 100.0;

        return new ResponseDTO("Progress till now: " + progress + "%", HttpStatus.OK.value(), null);
    }

    @Override
    public ResponseDTO sendSummaryReport(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new FutureFundsException("User with id"+ userId +"does not exist"));

        List<InvestmentPlan> plans = investmentPlanRepository.findByUserId(userId);

        if(plans.isEmpty()){
            return  new ResponseDTO("No Investment plans found by user",HttpStatus.NOT_FOUND.value(), null);
        }
        List<InvestmentResponseDTO> investmentResponseDTOList = plans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        String filePath = "src/main/resources/static/pdf/investmentPlans.pdf";
        pdfGeneratorUtility.generateInvestmentPdf(investmentResponseDTOList,filePath);

        notificationService.sendPdfNotification(user.getEmail(), filePath);

        return new ResponseDTO("Report generated and sent successfully", HttpStatus.OK.value(), null);
    }

    public Double getTargetAmount(Double monthlyAmount, Double expectedReturn, Integer durationMonths) {
        // Future Value (FV) = P × [(1 + r)^n - 1] / r × (1 + r)
        double targetAmount = 0.0;
        if(monthlyAmount > 0 && expectedReturn > 0 && durationMonths > 0) {
            double r = (expectedReturn/12)/100.0;
            double n = durationMonths;
            targetAmount = monthlyAmount * ((Math.pow(1 + r, n) - 1) / r) * (1 + r);
        }
        return BigDecimal.valueOf(targetAmount).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public double calculateCurrentValue(double monthlyAmount, double annualRate, int monthsElapsed) {
        double r = (annualRate / 12) / 100.0;
        double value = monthlyAmount * ((Math.pow(1 + r, monthsElapsed) - 1) / r) * (1 + r);
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private InvestmentPlan getInvestmentPlan(InvestmentPlanDTO investmentPlanDTO, double targetAmount, double currentValue, User user) {
        InvestmentPlan investmentPlan = new InvestmentPlan();
        investmentPlan.setName(investmentPlanDTO.getName());
        investmentPlan.setType(investmentPlanDTO.getType());
        investmentPlan.setMonthly_amount(investmentPlanDTO.getMonthlyAmount());
        investmentPlan.setExpected_return(investmentPlanDTO.getExpectedReturn());
        investmentPlan.setDuration_months(investmentPlanDTO.getDurationMonths());
        investmentPlan.setTarget_amount(targetAmount);
        investmentPlan.setCurrent_value(currentValue);
        investmentPlan.setStartDate(LocalDate.now());
        investmentPlan.setNextPaymentDate(LocalDate.now().plusMonths(1));
        investmentPlan.setUser(user);
        return investmentPlan;
    }

    public int getCompletedMonths(LocalDate startDate) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(startDate, currentDate);
        return period.getYears() * 12 + period.getMonths();
    }

    @Override
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

    public ResponseDTO createNotification(@Valid Long investmentId,@Valid String title,@Valid String message,@Valid NotificationType type) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setInvestmentId(investmentId);
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
