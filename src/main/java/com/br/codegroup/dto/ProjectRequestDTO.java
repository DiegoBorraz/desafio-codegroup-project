package com.br.codegroup.dto;


import com.br.codegroup.domain.ProjectStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class ProjectRequestDTO {

    @NotBlank
    private String name;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate expectedEndDate;

    private LocalDate actualEndDate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal totalBudget;

    private String description;

    private ProjectStatus status;

    @NotNull
    private Long managerId;

    private Set<Long> memberIds;
}