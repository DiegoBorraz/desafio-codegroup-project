-- Tabela para auditoria de mudanças de status
CREATE TABLE project_status_history (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by BIGINT NOT NULL,
    CONSTRAINT fk_status_history_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_status_history_member FOREIGN KEY (changed_by) REFERENCES members(id)
);

-- Índice para melhor performance
CREATE INDEX idx_status_history_project ON project_status_history(project_id);