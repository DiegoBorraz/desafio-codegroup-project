-- Adicionar colunas de auditoria nas tabelas existentes
ALTER TABLE members ADD COLUMN created_by VARCHAR(100);
ALTER TABLE members ADD COLUMN updated_by VARCHAR(100);

ALTER TABLE projects ADD COLUMN created_by VARCHAR(100);
ALTER TABLE projects ADD COLUMN updated_by VARCHAR(100);

ALTER TABLE project_status_history ADD COLUMN change_reason TEXT;