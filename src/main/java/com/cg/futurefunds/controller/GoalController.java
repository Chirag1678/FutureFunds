package com.cg.futurefunds.controller;

import com.cg.futurefunds.dto.GoalDTO;
import com.cg.futurefunds.dto.ResponseDTO;
import com.cg.futurefunds.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goals")
public class GoalController {
    @Autowired
    private GoalService goalService;
    @PostMapping("")
    public ResponseEntity <ResponseDTO> addGoal(@Valid @RequestBody GoalDTO goalDTO){
        ResponseDTO responseDTO = goalService.addGoal( goalDTO);
        return new ResponseEntity<>(responseDTO, HttpStatusCode.valueOf(responseDTO.getStatusCode()));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateGoal(@PathVariable Long id,@Valid @RequestBody GoalDTO goalDTO){
        ResponseDTO responseDTO = goalService.updateGoal(id,goalDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.valueOf(responseDTO.getStatusCode()));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteGoal(@PathVariable Long id){
        ResponseDTO responseDTO = goalService.deleteGoal(id);
        return new ResponseEntity<>(responseDTO,HttpStatusCode.valueOf(responseDTO.getStatusCode()));
    }
    @GetMapping("/user/{userid}")
    public ResponseEntity<ResponseDTO> getAllGoals(@PathVariable Long userid){
        ResponseDTO responseDTO = goalService.getAllGoals(userid);
        return new ResponseEntity<>(responseDTO,HttpStatusCode.valueOf(responseDTO.getStatusCode()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getGoal(@PathVariable Long id){
        ResponseDTO responseDTO = goalService.getGoal(id);
        return new ResponseEntity<>(responseDTO,HttpStatusCode.valueOf(responseDTO.getStatusCode()));
    }


}
