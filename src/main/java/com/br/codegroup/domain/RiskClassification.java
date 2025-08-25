package com.br.codegroup.domain;

public enum RiskClassification {
    LOW_RISK("Baixo risco"),
    MEDIUM_RISK("Médio risco"),
    HIGH_RISK("Alto risco");

    private final String displayName;

    RiskClassification(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RiskClassification fromString(String value) {
        for (RiskClassification risk : values()) {
            if (risk.name().equals(value)) {
                return risk;
            }
        }
        throw new IllegalArgumentException("Classificação de risco inválida: " + value);
    }
}