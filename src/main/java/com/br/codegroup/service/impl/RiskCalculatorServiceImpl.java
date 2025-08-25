package com.br.codegroup.service.impl;

import com.br.codegroup.domain.Project;
import com.br.codegroup.exception.CustomException;
import com.br.codegroup.repository.ProjectRepository;
import com.br.codegroup.service.RiskCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RiskCalculatorServiceImpl implements RiskCalculatorService {

    private final ProjectRepository projectRepository;

    @Override
    public String calculateRisk(BigDecimal budget, LocalDate startDate, LocalDate expectedEndDate) {
        long months = ChronoUnit.MONTHS.between(startDate, expectedEndDate);

        if (budget.compareTo(new BigDecimal("100000")) <= 0 && months <= 3) {
            return "Baixo risco";
        } else if ((budget.compareTo(new BigDecimal("100000")) > 0 &&
                budget.compareTo(new BigDecimal("500000")) <= 0) ||
                (months > 3 && months <= 6)) {
            return "Médio risco";
        } else if (budget.compareTo(new BigDecimal("500000")) > 0 || months > 6) {
            return "Alto risco";
        }

        return "Risco não classificado";
    }

    @Override
    public String calculateRiskBasedOnProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException("Project not found with id: " + projectId));

        return calculateRisk(
                project.getTotalBudget(),
                project.getStartDate(),
                project.getExpectedEndDate()
        );
    }
}