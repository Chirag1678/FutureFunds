package com.cg.futurefunds.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@Table(name = "investment_plan")
public class InvestmentPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investment_plan_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private InvestmentType type;

    private double monthly_amount;
    private double expected_return;
    private int duration_months;
    private double target_amount;
    private double current_value;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
    private Goal goal;
}
