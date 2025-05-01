package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.InvestmentPlanDTO;
import com.cg.futurefunds.dto.ResponseDTO;

public interface InvestmentService {
    ResponseDTO addInvestment(InvestmentPlanDTO investmentPlanDTO);
    ResponseDTO updateInvestment(Long investmentId, InvestmentPlanDTO investmentPlanDTO);
    ResponseDTO deleteInvestment(Long investmentId);
    ResponseDTO getAllInvestments(Long userId);
    ResponseDTO getInvestment(Long investmentId);
}
