package com.br.codegroup.dto;

import com.br.codegroup.domain.ProjectStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class ProjectDTO {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private LocalDate actualEndDate;
    private BigDecimal totalBudget;
    private String description;
    private ProjectStatus status;
    private String riskClassification;
    private Long managerId;
    private String managerName;
    private Set<Long> memberIds;
}
