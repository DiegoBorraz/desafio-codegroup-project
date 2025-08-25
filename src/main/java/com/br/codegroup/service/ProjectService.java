package com.br.codegroup.service;

import com.br.codegroup.domain.ProjectStatus;
import com.br.codegroup.dto.PortfolioReportDTO;
import com.br.codegroup.dto.ProjectDTO;
import com.br.codegroup.dto.ProjectRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {

    // CRUD básico
    ProjectDTO createProject(ProjectRequestDTO requestDTO);
    ProjectDTO findById(Long id);
    Page<ProjectDTO> findAll(Pageable pageable, String filter);
    ProjectDTO updateProject(Long id, ProjectRequestDTO requestDTO);
    void deleteProject(Long id);

    // Relatórios
    PortfolioReportDTO generatePortfolioReport();

    // Gestão de status
    ProjectDTO updateProjectStatus(Long id, ProjectStatus newStatus);
    void validateStatusTransition(ProjectStatus currentStatus, ProjectStatus newStatus);

    // Buscas específicas
    Page<ProjectDTO> findByStatus(ProjectStatus status, Pageable pageable);
    Page<ProjectDTO> findByManagerId(Long managerId, Pageable pageable);
    Page<ProjectDTO> findByMemberId(Long memberId, Pageable pageable);

    // Validações de negócio
    void validateProjectRequest(ProjectRequestDTO requestDTO, Long projectId);
    boolean isMemberAvailableForProject(Long memberId);
    void validateManager(Long managerId);
    void validateEmployee(Long memberId);
}