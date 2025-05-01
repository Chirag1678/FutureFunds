package com.cg.futurefunds.controller;

import com.cg.futurefunds.dto.InvestmentPlanDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.service.InvestmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/investments")
public class InvestmentController {
    @Autowired
    private InvestmentService investmentService;

    @PostMapping("")
    public ResponseEntity<ResponseDTO> addInvestment(@Valid @RequestBody InvestmentPlanDTO investmentPlanDTO) {
        ResponseDTO responseDTO = investmentService.addInvestment(investmentPlanDTO);

        return new ResponseEntity<>(responseDTO, HttpStatusCode.valueOf(responseDTO.getStatusCode()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateInvestment(@PathVariable Long id, @Valid @RequestBody InvestmentPlanDTO investmentPlanDTO) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteInvestment(@PathVariable Long id) {
        ResponseDTO responseDTO = investmentService.deleteInvestment(id);

        return new ResponseEntity<>(responseDTO, HttpStatusCode.valueOf(responseDTO.getStatusCode()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO> getAllInvestments(@PathVariable Long userId) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getInvestment(@PathVariable Long id) {
        ResponseDTO responseDTO = investmentService.getInvestment(id);

        return new ResponseEntity<> (responseDTO,HttpStatusCode.valueOf(responseDTO.getStatusCode()));


    }
}
