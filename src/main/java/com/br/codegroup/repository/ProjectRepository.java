package com.br.codegroup.repository;


import com.br.codegroup.domain.Project;
import com.br.codegroup.domain.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);
    Page<Project> findByManagerId(Long managerId, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.id = :memberId")
    Page<Project> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // RELATÓRIOS - CORRIGIDOS para usar os valores do enum
    @Query("SELECT p.status, COUNT(p) FROM Project p GROUP BY p.status")
    List<Object[]> countProjectsByStatus();

    @Query("SELECT p.status, SUM(p.totalBudget) FROM Project p GROUP BY p.status")
    List<Object[]> sumBudgetByStatus();

    @Query(value = "SELECT AVG(actual_end_date - start_date) " +
            "FROM projects WHERE status = 'COMPLETED' AND actual_end_date IS NOT NULL",
            nativeQuery = true)
    Double averageDurationOfFinishedProjects();

    @Query("SELECT COUNT(DISTINCT pm.id) FROM Project p JOIN p.members pm")
    Long countDistinctMembersInProjects();

    // CORREÇÃO: Usar COMPLETED e CANCELLED
    @Query("SELECT COUNT(p) FROM Project p JOIN p.members m WHERE m.id = :memberId " +
            "AND p.status NOT IN ('COMPLETED', 'CANCELLED')")
    Long countActiveProjectsByMemberId(@Param("memberId") Long memberId);

    // CORREÇÃO: Usar COMPLETED e CANCELLED
    @Query("SELECT p FROM Project p WHERE p.status NOT IN ('COMPLETED', 'CANCELLED') " +
            "AND :memberId IN (SELECT m.id FROM p.members m)")
    List<Project> findActiveProjectsByMemberId(@Param("memberId") Long memberId);

}