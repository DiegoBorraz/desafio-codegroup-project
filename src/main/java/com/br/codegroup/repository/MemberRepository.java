package com.br.codegroup.repository;


import com.br.codegroup.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByAssignment(String assignment);
    Optional<Member> findByName(String name);
    boolean existsByNameAndAssignment(String name, String assignment);
}