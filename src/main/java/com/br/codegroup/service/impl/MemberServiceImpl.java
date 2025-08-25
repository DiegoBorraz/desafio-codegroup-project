package com.br.codegroup.service.impl;


import com.br.codegroup.domain.Member;
import com.br.codegroup.dto.MemberDTO;
import com.br.codegroup.dto.MemberRequestDTO;
import com.br.codegroup.exception.CustomException;
import com.br.codegroup.exception.ResourceNotFoundException;
import com.br.codegroup.repository.MemberRepository;
import com.br.codegroup.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public MemberDTO createMember(MemberRequestDTO requestDTO) {
        validateAssignment(requestDTO.getAssignment());

        if (memberRepository.existsByNameAndAssignment(requestDTO.getName(), requestDTO.getAssignment())) {
            throw new CustomException("Já existe um membro com este nome e atribuição");
        }

        Member member = new Member();
        member.setName(requestDTO.getName());
        member.setAssignment(requestDTO.getAssignment());

        Member savedMember = memberRepository.save(member);
        return convertToDTO(savedMember);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getMemberById(Long id) {
        Member member = findById(id);
        return convertToDTO(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDTO> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDTO> getMembersByAssignment(String assignment) {
        validateAssignment(assignment);

        return memberRepository.findByAssignment(assignment).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado com ID: " + id));
    }

    @Override
    public MemberDTO convertToDTO(Member member) {
        return modelMapper.map(member, MemberDTO.class);
    }

    private void validateAssignment(String assignment) {
        if (!"gerente".equalsIgnoreCase(assignment) && !"funcionario".equalsIgnoreCase(assignment)) {
            throw new CustomException("Atribuição inválida. Deve ser 'gerente' ou 'funcionario'");
        }
    }
}