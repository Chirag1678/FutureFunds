package com.cg.futurefunds.controller;

import com.cg.futurefunds.dto.InvestmentPlanDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.service.InvestmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/investments")
public class InvestmentController {
    @Autowired
    private InvestmentService investmentService;

    @PostMapping("/")
    public ResponseEntity<ResponseDTO> addInvestment(@Valid @RequestBody InvestmentPlanDTO investmentPlanDTO) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateInvestment(@PathVariable Long investmentId, @Valid @RequestBody InvestmentPlanDTO investmentPlanDTO) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteInvestment(@PathVariable Long investmentId) {
        return null;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO> getAllInvestments(@PathVariable Long userId) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getInvestment(@PathVariable Long investmentId) {
        return null;
    }
}
