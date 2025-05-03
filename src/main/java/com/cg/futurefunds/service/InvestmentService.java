package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.InvestmentPlanDTO;
import com.cg.futurefunds.dto.InvestmentResponseDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.model.InvestmentPlan;

public interface InvestmentService {
    ResponseDTO addInvestment(InvestmentPlanDTO investmentPlanDTO);
    ResponseDTO updateInvestment(Long investmentId, InvestmentPlanDTO investmentPlanDTO);
    ResponseDTO deleteInvestment(Long investmentId);
    ResponseDTO getAllInvestments(Long userId);
    ResponseDTO getInvestment(Long investmentId);
    ResponseDTO simulateInvestment(InvestmentPlanDTO investmentPlanDTO);
    ResponseDTO progressInvestment(Long investmentId);
    InvestmentResponseDTO convertToResponse(InvestmentPlan investmentPlan);
    ResponseDTO sendSummaryReport(Long userId);
}
