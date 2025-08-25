package com.br.codegroup.service.impl;

import com.br.codegroup.domain.Member;
import com.br.codegroup.domain.Project;
import com.br.codegroup.domain.ProjectStatus;
import com.br.codegroup.dto.PortfolioReportDTO;
import com.br.codegroup.dto.ProjectDTO;
import com.br.codegroup.dto.ProjectRequestDTO;
import com.br.codegroup.exception.CustomException;
import com.br.codegroup.exception.ResourceNotFoundException;
import com.br.codegroup.repository.ProjectRepository;
import com.br.codegroup.service.MemberService;
import com.br.codegroup.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberService memberService;
    private final ModelMapper modelMapper;

    private static final List<ProjectStatus> STATUS_ORDER = Arrays.asList(
            ProjectStatus.UNDER_REVIEW,
            ProjectStatus.REVIEW_COMPLETED,
            ProjectStatus.REVIEW_APPROVED,
            ProjectStatus.STARTED,
            ProjectStatus.PLANNED,
            ProjectStatus.IN_PROGRESS,
            ProjectStatus.COMPLETED
    );

    @Override
    @Transactional
    public ProjectDTO createProject(ProjectRequestDTO requestDTO) {
        validateProjectRequest(requestDTO, null);

        Project project = modelMapper.map(requestDTO, Project.class);
        project.setStatus(ProjectStatus.UNDER_REVIEW);

        // Configurar gerente
        Member manager = memberService.findById(requestDTO.getManagerId());
        project.setManager(manager);

        // Configurar membros
        Set<Member> members = requestDTO.getMemberIds().stream()
                .map(memberService::findById)
                .collect(Collectors.toSet());
        project.setMembers(members);

        // Calcular risco
        project.calculateRiskClassification();

        Project savedProject = projectRepository.save(project);
        return convertToDTO(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDTO findById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com ID: " + id));
        return convertToDTO(project);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> findAll(Pageable pageable, String filter) {
        Page<Project> projects;
        if (filter != null && !filter.isEmpty()) {
            projects = projectRepository.findByNameContainingIgnoreCase(filter, pageable);
        } else {
            projects = projectRepository.findAll(pageable);
        }
        return projects.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public ProjectDTO updateProject(Long id, ProjectRequestDTO requestDTO) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com ID: " + id));

        validateProjectRequest(requestDTO, id);

        if (requestDTO.getStatus() != null && !requestDTO.getStatus().equals(existingProject.getStatus())) {
            validateStatusTransition(existingProject.getStatus(), requestDTO.getStatus());
        }

        if (!existingProject.canBeDeleted() && requestDTO.getStatus() != null &&
                !requestDTO.getStatus().equals(existingProject.getStatus())) {
            throw new CustomException("Não é possível alterar status de projetos com status iniciado, em andamento ou encerrado");
        }

        modelMapper.map(requestDTO, existingProject);

        // Atualizar gerente se necessário
        if (!existingProject.getManager().getId().equals(requestDTO.getManagerId())) {
            Member newManager = memberService.findById(requestDTO.getManagerId());
            existingProject.setManager(newManager);
        }

        // Atualizar membros
        Set<Member> newMembers = requestDTO.getMemberIds().stream()
                .map(memberService::findById)
                .collect(Collectors.toSet());
        existingProject.setMembers(newMembers);

        existingProject.calculateRiskClassification();

        Project updatedProject = projectRepository.save(existingProject);
        return convertToDTO(updatedProject);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com ID: " + id));

        if (!project.canBeDeleted()) {
            throw new CustomException("Não é possível excluir projetos com status iniciado, em andamento ou encerrado");
        }

        projectRepository.delete(project);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioReportDTO generatePortfolioReport() {
        PortfolioReportDTO report = new PortfolioReportDTO();

        Map<String, Long> projectsByStatus = new HashMap<>();
        Map<String, BigDecimal> budgetByStatus = new HashMap<>();

        projectRepository.countProjectsByStatus().forEach(arr -> {
            ProjectStatus status = (ProjectStatus) arr[0];
            Long count = (Long) arr[1];
            projectsByStatus.put(status.getDisplayName(), count);
        });

        projectRepository.sumBudgetByStatus().forEach(arr -> {
            ProjectStatus status = (ProjectStatus) arr[0];
            BigDecimal total = (BigDecimal) arr[1];
            budgetByStatus.put(status.getDisplayName(), total != null ? total : BigDecimal.ZERO);
        });

        report.setProjectsByStatus(projectsByStatus);
        report.setBudgetByStatus(budgetByStatus);

        Double averageDuration = projectRepository.averageDurationOfFinishedProjects();
        report.setAverageDurationDays(averageDuration != null ? averageDuration : 0.0);

        Long uniqueMembers = projectRepository.countDistinctMembersInProjects();
        report.setUniqueMembersCount(uniqueMembers != null ? uniqueMembers : 0L);

        return report;
    }

    @Override
    @Transactional
    public ProjectDTO updateProjectStatus(Long id, ProjectStatus newStatus) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com ID: " + id));

        if (newStatus != ProjectStatus.CANCELLED) {
            validateStatusTransition(project.getStatus(), newStatus);
        }

        project.setStatus(newStatus);

        if (newStatus == ProjectStatus.COMPLETED) {
            project.setActualEndDate(LocalDate.now());
        }

        Project updatedProject = projectRepository.save(project);
        return convertToDTO(updatedProject);
    }

    @Override
    public void validateStatusTransition(ProjectStatus currentStatus, ProjectStatus newStatus) {
        if (newStatus == ProjectStatus.CANCELLED) {
            return; // Cancelamento pode ocorrer a qualquer momento
        }

        int currentIndex = STATUS_ORDER.indexOf(currentStatus);
        int newIndex = STATUS_ORDER.indexOf(newStatus);

        if (currentIndex == -1 || newIndex == -1 || newIndex != currentIndex + 1) {
            throw new CustomException("Transição de status inválida. A sequência deve ser respeitada");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> findByStatus(ProjectStatus status, Pageable pageable) {
        Page<Project> projects = projectRepository.findByStatus(status, pageable);
        return projects.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> findByManagerId(Long managerId, Pageable pageable) {
        Page<Project> projects = projectRepository.findByManagerId(managerId, pageable);
        return projects.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> findByMemberId(Long memberId, Pageable pageable) {
        Page<Project> projects = projectRepository.findByMemberId(memberId, pageable);
        return projects.map(this::convertToDTO);
    }

    @Override
    public void validateProjectRequest(ProjectRequestDTO requestDTO, Long projectId) {
        // Validar gerente
        validateManager(requestDTO.getManagerId());

        // Validar membros
        if (requestDTO.getMemberIds() == null || requestDTO.getMemberIds().isEmpty()) {
            throw new CustomException("O projeto deve ter pelo menos 1 membro");
        }

        if (requestDTO.getMemberIds().size() > 10) {
            throw new CustomException("O projeto não pode ter mais de 10 membros");
        }

        for (Long memberId : requestDTO.getMemberIds()) {
            validateEmployee(memberId);
            if (projectId == null || !isMemberAlreadyInProject(memberId, projectId)) {
                if (!isMemberAvailableForProject(memberId)) {
                    throw new CustomException("Membro com ID " + memberId + " já está em 3 projetos ativos");
                }
            }
        }

        // Validar datas
        if (requestDTO.getStartDate() != null && requestDTO.getExpectedEndDate() != null) {
            if (requestDTO.getExpectedEndDate().isBefore(requestDTO.getStartDate())) {
                throw new CustomException("A data de término prevista não pode ser anterior à data de início");
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMemberAvailableForProject(Long memberId) {
        Long activeProjects = projectRepository.countActiveProjectsByMemberId(memberId);
        return activeProjects < 3;
    }

    @Override
    public void validateManager(Long managerId) {
        Member manager = memberService.findById(managerId);
        if (!"gerente".equalsIgnoreCase(manager.getAssignment())) {
            throw new CustomException("O membro com ID " + managerId + " não é um gerente");
        }
    }

    @Override
    public void validateEmployee(Long memberId) {
        Member member = memberService.findById(memberId);
        if (!"funcionario".equalsIgnoreCase(member.getAssignment())) {
            throw new CustomException("O membro com ID " + memberId + " não é um funcionário");
        }
    }

    private ProjectDTO convertToDTO(Project project) {
        ProjectDTO dto = modelMapper.map(project, ProjectDTO.class);

        if (project.getManager() != null) {
            dto.setManagerId(project.getManager().getId());
            dto.setManagerName(project.getManager().getName());
        }

        if (project.getMembers() != null) {
            dto.setMemberIds(project.getMembers().stream()
                    .map(Member::getId)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    private boolean isMemberAlreadyInProject(Long memberId, Long projectId) {
        if (projectId == null) {
            return false; // É uma criação nova
        }

        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent()) {
            return project.get().getMembers().stream()
                    .anyMatch(member -> member.getId().equals(memberId));
        }
        return false;
    }
}