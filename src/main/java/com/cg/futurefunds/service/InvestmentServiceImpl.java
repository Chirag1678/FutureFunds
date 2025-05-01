package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.InvestmentPlanDTO;
import com.cg.futurefunds.dto.InvestmentResponseDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.dto.UserResponseDTO;
import com.cg.futurefunds.exceptions.FutureFundsException;
import com.cg.futurefunds.model.InvestmentPlan;
import com.cg.futurefunds.model.User;
import com.cg.futurefunds.repository.InvestmentPlanRepository;
import com.cg.futurefunds.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Service
public class InvestmentServiceImpl implements InvestmentService {
    @Autowired
    private InvestmentPlanRepository investmentPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseDTO addInvestment(InvestmentPlanDTO investmentPlanDTO) {
        User user = userRepository.findByEmail(investmentPlanDTO.getUserEmail())
                .orElseThrow(() -> new FutureFundsException("User with email: " + investmentPlanDTO.getUserEmail() + " not exists."));

        double targetAmount = getTargetAmount(investmentPlanDTO.getMonthlyAmount(), investmentPlanDTO.getExpectedReturn(), investmentPlanDTO.getDurationMonths());
        int completedMonths = getCompletedMonths(LocalDate.now());
        double currentValue = calculateCurrentValue(investmentPlanDTO.getMonthlyAmount(), investmentPlanDTO.getExpectedReturn(), completedMonths);

        InvestmentPlan investmentPlan = getInvestmentPlan(investmentPlanDTO, targetAmount, currentValue, user);

        investmentPlanRepository.save(investmentPlan);

        InvestmentResponseDTO investmentResponseDTO = convertToResponse(investmentPlan);

        return new ResponseDTO("Investment plan added successfully", 201, investmentResponseDTO);
    }


    @Override
    public ResponseDTO updateInvestment(Long investmentId, InvestmentPlanDTO investmentPlanDTO) {
        InvestmentPlan investmentPlan = investmentPlanRepository.findById(investmentId)
                .orElseThrow(() ->  new FutureFundsException("Investment with Id" + investmentId + "not found"));

        User user = userRepository.findByEmail(investmentPlanDTO.getUserEmail())
                .orElseThrow(() -> new FutureFundsException("User with Email"+investmentPlanDTO.getUserEmail()+"not found"));

                investmentPlan.setName(investmentPlanDTO.getName());
                investmentPlan.setType(investmentPlanDTO.getType());
                investmentPlan.setMonthly_amount(investmentPlanDTO.getMonthlyAmount());
                investmentPlan.setExpected_return(investmentPlanDTO.getExpectedReturn());
                investmentPlan.setDuration_months(investmentPlanDTO.getDurationMonths());
                investmentPlan.setUser(user);

                double targetAmount = getTargetAmount(investmentPlanDTO.getMonthlyAmount(),investmentPlanDTO.getExpectedReturn(),investmentPlanDTO.getDurationMonths());
                int completedMonths = getCompletedMonths(investmentPlan.getStartDate());
                double currentValue = calculateCurrentValue(investmentPlanDTO.getMonthlyAmount(),investmentPlanDTO.getExpectedReturn(),completedMonths);
                investmentPlan.setTarget_amount(targetAmount);
                investmentPlan.setCurrent_value(currentValue);

                investmentPlanRepository.save(investmentPlan);

                InvestmentResponseDTO responseDTO = convertToResponse(investmentPlan);
                return  new ResponseDTO("Investment Updated Successfully",200,responseDTO);
    }

    @Override
    public ResponseDTO deleteInvestment(Long investmentId) {

        try{
            InvestmentPlan investmentPlan=investmentPlanRepository.getReferenceById(investmentId);
            investmentPlanRepository.delete(investmentPlan);
            InvestmentResponseDTO investmentResponseDTO = convertToResponse(investmentPlan);
            return new ResponseDTO("Investment Plan Deleted Successfully", 200, null);
        }
        catch (Exception e) {
            throw new FutureFundsException("Investment Plan does not Exist");
        }
    }

    @Override
    public ResponseDTO getAllInvestments(Long userId) {
        return null;
    }

    @Override
    public ResponseDTO getInvestment(Long investmentId) {

        try{
            InvestmentPlan investmentPlan=investmentPlanRepository.getReferenceById(investmentId);
            InvestmentResponseDTO investmentResponseDTO = convertToResponse(investmentPlan);
            return new ResponseDTO("Investment Plan Details", 200, investmentResponseDTO);
        } catch (Exception e) {
            throw new FutureFundsException("No investment plan found with given id");
        }
    }

    public Double getTargetAmount(Double monthlyAmount, Double expectedReturn, Integer durationMonths) {
        // Future Value (FV) = P × [(1 + r)^n - 1] / r × (1 + r)
        double targetAmount = 0.0;
        if( monthlyAmount > 0 && expectedReturn > 0 && durationMonths > 0 ) {
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


    private static InvestmentPlan getInvestmentPlan(InvestmentPlanDTO investmentPlanDTO, double targetAmount, double currentValue, User user) {
        InvestmentPlan investmentPlan = new InvestmentPlan();
        investmentPlan.setName(investmentPlanDTO.getName());
        investmentPlan.setType(investmentPlanDTO.getType());
        investmentPlan.setMonthly_amount(investmentPlanDTO.getMonthlyAmount());
        investmentPlan.setExpected_return(investmentPlanDTO.getExpectedReturn());
        investmentPlan.setDuration_months(investmentPlanDTO.getDurationMonths());
        investmentPlan.setTarget_amount(targetAmount);
        investmentPlan.setCurrent_value(currentValue);
        investmentPlan.setStartDate(LocalDate.now());
        investmentPlan.setUser(user);
        return investmentPlan;
    }

    public int getCompletedMonths(LocalDate startDate) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(startDate, currentDate);
        return period.getYears() * 12 + period.getMonths();
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
