package com.cg.futurefunds.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
    private LocalDate startDate;
    private LocalDate nextPaymentDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    private User user;

    @OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
    private Goal goal;

    @OneToMany(mappedBy = "investmentPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Notification> notifications;
}
