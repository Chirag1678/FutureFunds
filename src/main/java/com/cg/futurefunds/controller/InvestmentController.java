package com.cg.futurefunds.controller;

import com.cg.futurefunds.dto.InvestmentPlanDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.service.InvestmentService;
import com.cg.futurefunds.utility.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private JwtUtility jwtUtility;

    @PostMapping
    public ResponseEntity<ResponseDTO> addInvestment(@Valid @RequestBody InvestmentPlanDTO investmentPlanDTO, HttpServletRequest request) {
        String token = getToken(request);
        String email = jwtUtility.extractEmail(token);
        investmentPlanDTO.setUserEmail(email);
        ResponseDTO responseDTO = investmentService.addInvestment(investmentPlanDTO);

        return new ResponseEntity<>(responseDTO, HttpStatusCode.valueOf(responseDTO.getStatusCode()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateInvestment(@PathVariable Long id, @Valid @RequestBody InvestmentPlanDTO investmentPlanDTO, HttpServletRequest request) {
        String token = getToken(request);
        String email = jwtUtility.extractEmail(token);
        investmentPlanDTO.setUserEmail(email);
        ResponseDTO responseDTO = investmentService.updateInvestment(id,investmentPlanDTO);

        return  new ResponseEntity<>(responseDTO,HttpStatusCode.valueOf(responseDTO.getStatusCode()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteInvestment(@PathVariable Long id) {
        ResponseDTO responseDTO = investmentService.deleteInvestment(id);

        return new ResponseEntity<>(responseDTO, HttpStatusCode.valueOf(responseDTO.getStatusCode()));
    }

    @GetMapping("/user")
    public ResponseEntity<ResponseDTO> getAllInvestments(HttpServletRequest request) {
        String token = getToken(request);
        Long userId = jwtUtility.extractUserId(token);
        ResponseDTO responseDTO = investmentService.getAllInvestments(userId);

        return  new ResponseEntity<>(responseDTO, HttpStatusCode.valueOf(responseDTO.getStatusCode()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getInvestment(@PathVariable Long id) {
        ResponseDTO responseDTO = investmentService.getInvestment(id);

        return new ResponseEntity<> (responseDTO,HttpStatusCode.valueOf(responseDTO.getStatusCode()));
    }

    @GetMapping("/simulate")
    public ResponseEntity<ResponseDTO> simulateInvestment(@Valid @RequestBody InvestmentPlanDTO investmentPlanDTO) {
        ResponseDTO responseDTO = investmentService.simulateInvestment(investmentPlanDTO);

        return new ResponseEntity<>(responseDTO,HttpStatusCode.valueOf(responseDTO.getStatusCode()));
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<ResponseDTO> progressInvestment(@PathVariable Long id) {
        ResponseDTO responseDTO = investmentService.progressInvestment(id);

        return new ResponseEntity<>(responseDTO,HttpStatusCode.valueOf(responseDTO.getStatusCode()));
    }

    public String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
