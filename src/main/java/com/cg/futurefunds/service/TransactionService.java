package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.ResponseDTO;

public interface TransactionService {
    ResponseDTO payment(Long investmentId);
    ResponseDTO dueDateNotification();
    ResponseDTO paymentConfirmationNotification();
    ResponseDTO investmentMaturedNotification();
    ResponseDTO goalAchievementNotification();
}
