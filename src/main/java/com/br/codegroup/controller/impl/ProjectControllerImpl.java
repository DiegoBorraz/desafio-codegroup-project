package com.br.codegroup.controller.impl;

import com.br.codegroup.controller.ProjectController;
import com.br.codegroup.domain.ProjectStatus;
import com.br.codegroup.dto.ProjectDTO;
import com.br.codegroup.dto.ProjectRequestDTO;
import com.br.codegroup.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectControllerImpl implements ProjectController {

    private final ProjectService projectService;

    @Override
    @GetMapping
    public ResponseEntity<Page<ProjectDTO>> getAllProjects(@Parameter(hidden = true) Pageable pageable) {
        Page<ProjectDTO> projects = projectService.findAll(pageable, null);
        return ResponseEntity.ok(projects);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        ProjectDTO project = projectService.findById(id);
        return ResponseEntity.ok(project);
    }

    @Override
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectRequestDTO projectRequestDTO) {
        ProjectDTO createdProject = projectService.createProject(projectRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDTO projectRequestDTO) {
        ProjectDTO updatedProject = projectService.updateProject(id, projectRequestDTO);
        return ResponseEntity.ok(updatedProject);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<Page<ProjectDTO>> getProjectsByName(
            @RequestParam String name,
            Pageable pageable) {
        Page<ProjectDTO> projects = projectService.findAll(pageable, name);
        return ResponseEntity.ok(projects);
    }

    @Override
    @GetMapping("/status")
    public ResponseEntity<Page<ProjectDTO>> getProjectsByStatus(
            @RequestParam String status,
            Pageable pageable) {
        try {
            ProjectStatus projectStatus = ProjectStatus.valueOf(status.toUpperCase());
            Page<ProjectDTO> projects = projectService.findByStatus(projectStatus, pageable);
            return ResponseEntity.ok(projects);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<Page<ProjectDTO>> getProjectsByManager(
            @PathVariable Long managerId,
            Pageable pageable) {
        Page<ProjectDTO> projects = projectService.findByManagerId(managerId, pageable);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<Page<ProjectDTO>> getProjectsByMember(
            @PathVariable Long memberId,
            Pageable pageable) {
        Page<ProjectDTO> projects = projectService.findByMemberId(memberId, pageable);
        return ResponseEntity.ok(projects);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectDTO> updateProjectStatus(
            @PathVariable Long id,
            @RequestParam ProjectStatus newStatus) {
        ProjectDTO updatedProject = projectService.updateProjectStatus(id, newStatus);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping("/report")
    public ResponseEntity<Object> generatePortfolioReport() {
        try {
            return ResponseEntity.ok(projectService.generatePortfolioReport());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/risk")
    public ResponseEntity<String> calculateProjectRisk(@PathVariable Long id) {
        try {
            ProjectDTO project = projectService.findById(id);
            return ResponseEntity.ok(project.getRiskClassification());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Projeto não encontrado: " + e.getMessage());
        }
    }
}