package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.InvestmentPlanDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.repository.InvestmentPlanRepository;
import com.cg.futurefunds.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvestmentServiceImpl implements InvestmentService {
    @Autowired
    private InvestmentPlanRepository investmentPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseDTO addInvestment(InvestmentPlanDTO investmentPlanDTO) {
        return null;
    }

    @Override
    public ResponseDTO updateInvestment(Long investmentId, InvestmentPlanDTO investmentPlanDTO) {
        return null;
    }

    @Override
    public ResponseDTO deleteInvestment(Long investmentId) {
        return null;
    }

    @Override
    public ResponseDTO getAllInvestments(Long userId) {
        return null;
    }

    @Override
    public ResponseDTO getInvestment(Long investmentId) {
        return null;
    }
}
