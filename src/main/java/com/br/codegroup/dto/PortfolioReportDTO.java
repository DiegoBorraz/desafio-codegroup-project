package com.br.codegroup.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class PortfolioReportDTO {
    private Map<String, Long> projectsByStatus;
    private Map<String, BigDecimal> budgetByStatus;
    private Double averageDurationDays;
    private Long uniqueMembersCount;
}