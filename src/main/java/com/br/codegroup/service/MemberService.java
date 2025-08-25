package com.br.codegroup.service;

import com.br.codegroup.domain.Member;
import com.br.codegroup.dto.MemberDTO;
import com.br.codegroup.dto.MemberRequestDTO;

import java.util.List;

public interface MemberService {

    // Métodos para API externa mockada
    MemberDTO createMember(MemberRequestDTO requestDTO);
    MemberDTO getMemberById(Long id);
    List<MemberDTO> getAllMembers();
    List<MemberDTO> getMembersByAssignment(String assignment);

    // Métodos internos para validação
    Member findById(Long id);

    // Conversões
    MemberDTO convertToDTO(Member member);
}