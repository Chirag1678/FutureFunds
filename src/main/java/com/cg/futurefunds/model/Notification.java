package com.cg.futurefunds.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@RequiredArgsConstructor
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;
    private String title;
    private String message;
    private String type;
    private LocalDateTime scheduledAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "investment_plan_id", nullable = true)
    @JsonManagedReference
    private InvestmentPlan investmentPlan;

    @ManyToOne
    @JoinColumn(name = "goal_id", nullable = true)
    @JsonManagedReference
    private Goal goal;

}
