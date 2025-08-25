package com.br.codegroup.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RiskCalculatorService {

    public String calculateRisk(BigDecimal budget, LocalDate startDate, LocalDate expectedEndDate);

    public String calculateRiskBasedOnProject(Long projectId);

}
