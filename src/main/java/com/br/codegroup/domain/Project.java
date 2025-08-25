package com.br.codegroup.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "expected_end_date", nullable = false)
    private LocalDate expectedEndDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "total_budget", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalBudget;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_classification")
    private RiskClassification riskClassification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    @ToString.Exclude
    private Member manager;

    @ManyToMany
    @JoinTable(
            name = "project_members",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Member> members = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateRiskClassification();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateRiskClassification();
    }

    public void calculateRiskClassification() {
        long months = ChronoUnit.MONTHS.between(startDate, expectedEndDate);

        if (totalBudget.compareTo(new BigDecimal("100000")) <= 0 && months <= 3) {
            riskClassification = RiskClassification.LOW_RISK;
        } else if ((totalBudget.compareTo(new BigDecimal("100000")) > 0 &&
                totalBudget.compareTo(new BigDecimal("500000")) <= 0) ||
                (months > 3 && months <= 6)) {
            riskClassification = RiskClassification.MEDIUM_RISK;
        } else if (totalBudget.compareTo(new BigDecimal("500000")) > 0 || months > 6) {
            riskClassification = RiskClassification.HIGH_RISK;
        } else {
            riskClassification = null;
        }
    }

    public boolean canBeDeleted() {
        return status != ProjectStatus.STARTED &&
                status != ProjectStatus.IN_PROGRESS &&
                status != ProjectStatus.COMPLETED;
    }
}