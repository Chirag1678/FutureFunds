package com.cg.futurefunds.repository;

import com.cg.futurefunds.model.InvestmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentPlanRepository extends JpaRepository<InvestmentPlan, Long> {
    List<InvestmentPlan> findByUserId(Long userId);
}
