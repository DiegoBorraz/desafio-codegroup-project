-- Inserir membros iniciais (mantido igual)
INSERT INTO members (name, assignment) VALUES
('João Silva', 'gerente'),
('Maria Santos', 'funcionario'),
('Pedro Almeida', 'funcionario'),
('Ana Costa', 'gerente'),
('Carlos Oliveira', 'funcionario'),
('Fernanda Lima', 'funcionario');

-- Inserir projetos de exemplo (valores corrigidos)
INSERT INTO projects (name, start_date, expected_end_date, total_budget, description, status, risk_classification, manager_id) VALUES
('Sistema de Gestão', '2024-01-15', '2024-04-15', 85000.00, 'Desenvolvimento de sistema interno', 'IN_PROGRESS', 'LOW_RISK', 1),
('Portal E-commerce', '2024-02-01', '2024-08-01', 250000.00, 'Loja virtual para cliente externo', 'PLANNED', 'MEDIUM_RISK', 4),
('App Mobile', '2024-03-10', '2024-09-10', 600000.00, 'Aplicativo para dispositivos móveis', 'UNDER_REVIEW', 'HIGH_RISK', 1),
('Redesign Website', '2024-04-01', '2024-06-01', 120000.00, 'Reformulação do site corporativo', 'REVIEW_APPROVED', 'LOW_RISK', 4);

-- Inserir associações entre projetos e membros (mantido igual)
INSERT INTO project_members (project_id, member_id) VALUES
(1, 2), (1, 3),
(2, 2), (2, 5),
(3, 3), (3, 6),
(4, 5), (4, 6);