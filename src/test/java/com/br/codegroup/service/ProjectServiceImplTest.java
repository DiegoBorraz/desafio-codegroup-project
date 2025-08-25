package com.br.codegroup.service;

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
import com.br.codegroup.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private ProjectDTO projectDTO;
    private ProjectRequestDTO projectRequestDTO;
    private Member manager;
    private Member employee;

    @BeforeEach
    void setUp() {
        // Configurar manager
        manager = new Member();
        manager.setId(1L);
        manager.setName("Gerente Teste");
        manager.setAssignment("gerente");

        // Configurar employee
        employee = new Member();
        employee.setId(2L);
        employee.setName("Funcionario Teste");
        employee.setAssignment("funcionario");

        // Configurar project
        project = new Project();
        project.setId(1L);
        project.setName("Projeto Teste");
        project.setStartDate(LocalDate.now());
        project.setExpectedEndDate(LocalDate.now().plusMonths(2));
        project.setTotalBudget(BigDecimal.valueOf(50000));
        project.setDescription("Descrição teste");
        project.setStatus(ProjectStatus.UNDER_REVIEW);
        project.setManager(manager);
        project.setMembers(new HashSet<>(Arrays.asList(employee)));

        // Configurar DTOs
        projectDTO = new ProjectDTO();
        projectDTO.setId(1L);
        projectDTO.setName("Projeto Teste");
        projectDTO.setManagerId(1L);
        projectDTO.setMemberIds(Set.of(2L));

        projectRequestDTO = new ProjectRequestDTO();
        projectRequestDTO.setName("Projeto Teste");
        projectRequestDTO.setStartDate(LocalDate.now());
        projectRequestDTO.setExpectedEndDate(LocalDate.now().plusMonths(2));
        projectRequestDTO.setTotalBudget(BigDecimal.valueOf(50000));
        projectRequestDTO.setDescription("Descrição teste");
        projectRequestDTO.setManagerId(1L);
        projectRequestDTO.setMemberIds(Set.of(2L));
    }

    // Método auxiliar para criar projetos completos
    private Project createCompleteProject(ProjectStatus status) {
        Project project = new Project();
        project.setId(1L);
        project.setName("Projeto Teste");
        project.setStartDate(LocalDate.now());
        project.setExpectedEndDate(LocalDate.now().plusMonths(2));
        project.setTotalBudget(BigDecimal.valueOf(50000));
        project.setDescription("Descrição teste");
        project.setStatus(status);
        project.setManager(manager);
        project.setMembers(new HashSet<>(Arrays.asList(employee)));
        return project;
    }

    @Test
    void createProject_WithValidData_ShouldReturnProjectDTO() {
        // Arrange
        when(memberService.findById(1L)).thenReturn(manager);
        when(memberService.findById(2L)).thenReturn(employee);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(modelMapper.map(any(ProjectRequestDTO.class), eq(Project.class))).thenReturn(project);
        when(modelMapper.map(any(Project.class), eq(ProjectDTO.class))).thenReturn(projectDTO);

        // Act
        ProjectDTO result = projectService.createProject(projectRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Projeto Teste", result.getName());
        verify(projectRepository, times(1)).save(any(Project.class));
        // 4 chamadas: validateManager (1x), validateEmployee (1x), e no createProject (2x)
        verify(memberService, times(4)).findById(anyLong());
    }

    @Test
    void createProject_WithInvalidManager_ShouldThrowCustomException() {
        // Arrange
        Member notManager = new Member();
        notManager.setId(3L);
        notManager.setAssignment("funcionario");

        when(memberService.findById(1L)).thenReturn(notManager);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            projectService.createProject(projectRequestDTO);
        });

        assertEquals("O membro com ID 1 não é um gerente", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void createProject_WithInvalidEmployee_ShouldThrowCustomException() {
        // Arrange
        Member notEmployee = new Member();
        notEmployee.setId(3L);
        notEmployee.setAssignment("gerente");

        when(memberService.findById(1L)).thenReturn(manager);
        when(memberService.findById(2L)).thenReturn(notEmployee);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            projectService.createProject(projectRequestDTO);
        });

        assertEquals("O membro com ID 2 não é um funcionário", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void createProject_WithNoMembers_ShouldThrowCustomException() {
        // Arrange
        ProjectRequestDTO invalidRequest = new ProjectRequestDTO();
        invalidRequest.setManagerId(1L);
        invalidRequest.setMemberIds(Set.of()); // Lista vazia

        when(memberService.findById(1L)).thenReturn(manager);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            projectService.createProject(invalidRequest);
        });

        assertEquals("O projeto deve ter pelo menos 1 membro", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void createProject_WithTooManyMembers_ShouldThrowCustomException() {
        // Arrange
        ProjectRequestDTO invalidRequest = new ProjectRequestDTO();
        invalidRequest.setManagerId(1L);
        invalidRequest.setMemberIds(Set.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)); // 11 membros

        when(memberService.findById(1L)).thenReturn(manager);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            projectService.createProject(invalidRequest);
        });

        assertEquals("O projeto não pode ter mais de 10 membros", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void findById_WithExistingId_ShouldReturnProjectDTO() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(modelMapper.map(any(Project.class), eq(ProjectDTO.class))).thenReturn(projectDTO);

        // Act
        ProjectDTO result = projectService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void findById_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            projectService.findById(999L);
        });

        assertEquals("Projeto não encontrado com ID: 999", exception.getMessage());
        verify(projectRepository, times(1)).findById(999L);
    }



    @Test
    void deleteProject_WithDeletableStatus_ShouldDeleteProject() {
        // Arrange
        Project deletableProject = createCompleteProject(ProjectStatus.UNDER_REVIEW);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(deletableProject));

        // Act
        projectService.deleteProject(1L);

        // Assert
        verify(projectRepository, times(1)).delete(deletableProject);
    }

    @Test
    void deleteProject_WithNonDeletableStatus_ShouldThrowCustomException() {
        // Arrange
        Project nonDeletableProject = createCompleteProject(ProjectStatus.STARTED);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(nonDeletableProject));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            projectService.deleteProject(1L);
        });

        assertEquals("Não é possível excluir projetos com status iniciado, em andamento ou encerrado",
                exception.getMessage());
        verify(projectRepository, never()).delete(any(Project.class));
    }

    @Test
    void updateProjectStatus_WithValidTransition_ShouldUpdateStatus() {
        // Arrange
        Project existingProject = createCompleteProject(ProjectStatus.UNDER_REVIEW);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(existingProject);
        when(modelMapper.map(any(Project.class), eq(ProjectDTO.class))).thenReturn(projectDTO);

        // Act
        ProjectDTO result = projectService.updateProjectStatus(1L, ProjectStatus.REVIEW_COMPLETED);

        // Assert
        assertNotNull(result);
        verify(projectRepository, times(1)).save(existingProject);
    }

    @Test
    void updateProjectStatus_WithInvalidTransition_ShouldThrowCustomException() {
        // Arrange
        Project existingProject = createCompleteProject(ProjectStatus.UNDER_REVIEW);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            projectService.updateProjectStatus(1L, ProjectStatus.IN_PROGRESS); // Pulando etapas
        });

        assertEquals("Transição de status inválida. A sequência deve ser respeitada", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void updateProjectStatus_ToCompleted_ShouldSetActualEndDate() {
        // Arrange
        Project existingProject = createCompleteProject(ProjectStatus.IN_PROGRESS);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(existingProject);
        when(modelMapper.map(any(Project.class), eq(ProjectDTO.class))).thenReturn(projectDTO);

        // Act
        projectService.updateProjectStatus(1L, ProjectStatus.COMPLETED);

        // Assert
        assertNotNull(existingProject.getActualEndDate());
        assertEquals(LocalDate.now(), existingProject.getActualEndDate());
        verify(projectRepository, times(1)).save(existingProject);
    }

    @Test
    void validateStatusTransition_WithValidSequence_ShouldNotThrowException() {
        // Act & Assert - Deve passar sem exceção
        assertDoesNotThrow(() -> {
            projectService.validateStatusTransition(ProjectStatus.UNDER_REVIEW, ProjectStatus.REVIEW_COMPLETED);
        });
    }

    @Test
    void validateStatusTransition_WithInvalidSequence_ShouldThrowCustomException() {
        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            projectService.validateStatusTransition(ProjectStatus.UNDER_REVIEW, ProjectStatus.IN_PROGRESS);
        });

        assertEquals("Transição de status inválida. A sequência deve ser respeitada", exception.getMessage());
    }

    @Test
    void validateStatusTransition_WithCancelled_ShouldNotThrowException() {
        // Act & Assert - Cancelamento pode ocorrer a qualquer momento
        assertDoesNotThrow(() -> {
            projectService.validateStatusTransition(ProjectStatus.UNDER_REVIEW, ProjectStatus.CANCELLED);
        });
    }

    @Test
    void isMemberAvailableForProject_WithAvailableMember_ShouldReturnTrue() {
        // Arrange
        when(projectRepository.countActiveProjectsByMemberId(1L)).thenReturn(2L);

        // Act
        boolean result = projectService.isMemberAvailableForProject(1L);

        // Assert
        assertTrue(result);
        verify(projectRepository, times(1)).countActiveProjectsByMemberId(1L);
    }

    @Test
    void isMemberAvailableForProject_WithUnavailableMember_ShouldReturnFalse() {
        // Arrange
        when(projectRepository.countActiveProjectsByMemberId(1L)).thenReturn(3L);

        // Act
        boolean result = projectService.isMemberAvailableForProject(1L);

        // Assert
        assertFalse(result);
        verify(projectRepository, times(1)).countActiveProjectsByMemberId(1L);
    }

    @Test
    void generatePortfolioReport_ShouldReturnReportDTO() {
        // Arrange
        List<Object[]> countList = Arrays.asList(
                new Object[]{ProjectStatus.UNDER_REVIEW, 5L},
                new Object[]{ProjectStatus.STARTED, 3L}
        );

        List<Object[]> budgetList = Arrays.asList(
                new Object[]{ProjectStatus.UNDER_REVIEW, BigDecimal.valueOf(100000)},
                new Object[]{ProjectStatus.STARTED, BigDecimal.valueOf(200000)}
        );

        when(projectRepository.countProjectsByStatus()).thenReturn(countList);
        when(projectRepository.sumBudgetByStatus()).thenReturn(budgetList);
        when(projectRepository.averageDurationOfFinishedProjects()).thenReturn(30.5);
        when(projectRepository.countDistinctMembersInProjects()).thenReturn(25L);

        // Act
        PortfolioReportDTO result = projectService.generatePortfolioReport();

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getProjectsByStatus().get("em análise"));
        assertEquals(3L, result.getProjectsByStatus().get("iniciado"));
        assertEquals(BigDecimal.valueOf(100000), result.getBudgetByStatus().get("em análise"));
        assertEquals(BigDecimal.valueOf(200000), result.getBudgetByStatus().get("iniciado"));
        assertEquals(30.5, result.getAverageDurationDays());
        assertEquals(25L, result.getUniqueMembersCount());

        verify(projectRepository, times(1)).countProjectsByStatus();
        verify(projectRepository, times(1)).sumBudgetByStatus();
        verify(projectRepository, times(1)).averageDurationOfFinishedProjects();
        verify(projectRepository, times(1)).countDistinctMembersInProjects();
    }
}