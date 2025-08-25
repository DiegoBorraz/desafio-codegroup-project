package com.br.codegroup.domain;

public enum ProjectStatus {
    UNDER_REVIEW("em análise"),
    REVIEW_COMPLETED("análise realizada"),
    REVIEW_APPROVED("análise aprovada"),
    STARTED("iniciado"),
    PLANNED("planejado"),
    IN_PROGRESS("em andamento"),
    COMPLETED("encerrado"),
    CANCELLED("cancelado");

    private final String displayName;

    ProjectStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ProjectStatus fromString(String value) {
        for (ProjectStatus status : values()) {
            if (status.name().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + value);
    }
}