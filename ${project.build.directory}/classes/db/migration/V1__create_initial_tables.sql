-- Tabela de membros (funcionários)
CREATE TABLE members (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    assignment VARCHAR(100) NOT NULL CHECK (assignment IN ('gerente', 'funcionario')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    expected_end_date DATE NOT NULL,
    actual_end_date DATE,
    total_budget NUMERIC(15, 2) NOT NULL CHECK (total_budget >= 0),
    description TEXT,
    status VARCHAR(50) NOT NULL CHECK (status IN (
        'UNDER_REVIEW',
        'REVIEW_COMPLETED',
        'REVIEW_APPROVED',
        'STARTED',
        'PLANNED',
        'IN_PROGRESS',
        'COMPLETED',
        'CANCELLED'
    )),
    risk_classification VARCHAR(50) CHECK (risk_classification IN (
        'LOW_RISK',
        'MEDIUM_RISK',
        'HIGH_RISK'
    )),
    manager_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_project_manager FOREIGN KEY (manager_id) REFERENCES members(id)
);

-- Tabela de associação entre projetos e membros
CREATE TABLE project_members (
    project_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (project_id, member_id),
    CONSTRAINT fk_project_member_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_project_member_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

-- Índices para melhor performance
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_projects_manager ON projects(manager_id);
CREATE INDEX idx_projects_dates ON projects(start_date, expected_end_date);
CREATE INDEX idx_members_assignment ON members(assignment);
CREATE INDEX idx_project_members_member ON project_members(member_id);