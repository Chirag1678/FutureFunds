package com.cg.futurefunds.model;

public enum NotificationType {
    // Investment-related notifications
    INVESTMENT_CREATED,
    INVESTMENT_UPDATED,
    INVESTMENT_DELETED,
    INVESTMENT_DUE_DATE_REMINDER,
    INVESTMENT_PAYMENT_SUCCESSFUL,
    INVESTMENT_MATURED,

    // Goal milestone notifications
    GOAL_MILESTONE_25_PERCENT,
    GOAL_MILESTONE_50_PERCENT,
    GOAL_MILESTONE_75_PERCENT,
    GOAL_MILESTONE_100_PERCENT
}
