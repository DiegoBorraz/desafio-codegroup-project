package com.br.codegroup.service;


import com.br.codegroup.domain.Member;
import com.br.codegroup.dto.MemberDTO;
import com.br.codegroup.dto.MemberRequestDTO;
import com.br.codegroup.exception.CustomException;
import com.br.codegroup.exception.ResourceNotFoundException;
import com.br.codegroup.repository.MemberRepository;
import com.br.codegroup.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member;
    private MemberDTO memberDTO;
    private MemberRequestDTO memberRequestDTO;

    @BeforeEach
    void setUp() {
        // Configuração dos objetos de teste
        member = new Member();
        member.setId(1L);
        member.setName("João Silva");
        member.setAssignment("funcionario");

        memberDTO = new MemberDTO();
        memberDTO.setId(1L);
        memberDTO.setName("João Silva");
        memberDTO.setAssignment("funcionario");

        memberRequestDTO = new MemberRequestDTO();
        memberRequestDTO.setName("João Silva");
        memberRequestDTO.setAssignment("funcionario");
    }

    @Test
    void createMember_WithValidData_ShouldReturnMemberDTO() {
        // Arrange
        when(memberRepository.existsByNameAndAssignment(anyString(), anyString())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(modelMapper.map(any(Member.class), eq(MemberDTO.class))).thenReturn(memberDTO);

        // Act
        MemberDTO result = memberService.createMember(memberRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("João Silva", result.getName());
        assertEquals("funcionario", result.getAssignment());

        verify(memberRepository, times(1)).existsByNameAndAssignment(anyString(), anyString());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void createMember_WithDuplicateNameAndAssignment_ShouldThrowCustomException() {
        // Arrange
        when(memberRepository.existsByNameAndAssignment(anyString(), anyString())).thenReturn(true);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            memberService.createMember(memberRequestDTO);
        });

        assertEquals("Já existe um membro com este nome e atribuição", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void createMember_WithInvalidAssignment_ShouldThrowCustomException() {
        // Arrange
        memberRequestDTO.setAssignment("invalido");

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            memberService.createMember(memberRequestDTO);
        });

        assertEquals("Atribuição inválida. Deve ser 'gerente' ou 'funcionario'", exception.getMessage());
        verify(memberRepository, never()).existsByNameAndAssignment(anyString(), anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void getMemberById_WithExistingId_ShouldReturnMemberDTO() {
        // Arrange
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(modelMapper.map(any(Member.class), eq(MemberDTO.class))).thenReturn(memberDTO);

        // Act
        MemberDTO result = memberService.getMemberById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("João Silva", result.getName());

        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    void getMemberById_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            memberService.getMemberById(999L);
        });

        assertEquals("Membro não encontrado com ID: 999", exception.getMessage());
        verify(memberRepository, times(1)).findById(999L);
    }

    @Test
    void getAllMembers_ShouldReturnListOfMemberDTOs() {
        // Arrange
        Member member2 = new Member();
        member2.setId(2L);
        member2.setName("Maria Santos");
        member2.setAssignment("gerente");

        MemberDTO memberDTO2 = new MemberDTO();
        memberDTO2.setId(2L);
        memberDTO2.setName("Maria Santos");
        memberDTO2.setAssignment("gerente");

        when(memberRepository.findAll()).thenReturn(Arrays.asList(member, member2));
        when(modelMapper.map(member, MemberDTO.class)).thenReturn(memberDTO);
        when(modelMapper.map(member2, MemberDTO.class)).thenReturn(memberDTO2);

        // Act
        List<MemberDTO> result = memberService.getAllMembers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("João Silva", result.get(0).getName());
        assertEquals("Maria Santos", result.get(1).getName());

        verify(memberRepository, times(1)).findAll();
    }

    @Test
    void getMembersByAssignment_WithValidAssignment_ShouldReturnFilteredList() {
        // Arrange
        when(memberRepository.findByAssignment("funcionario")).thenReturn(Arrays.asList(member));
        when(modelMapper.map(any(Member.class), eq(MemberDTO.class))).thenReturn(memberDTO);

        // Act
        List<MemberDTO> result = memberService.getMembersByAssignment("funcionario");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("funcionario", result.get(0).getAssignment());

        verify(memberRepository, times(1)).findByAssignment("funcionario");
    }

    @Test
    void getMembersByAssignment_WithInvalidAssignment_ShouldThrowCustomException() {
        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            memberService.getMembersByAssignment("invalido");
        });

        assertEquals("Atribuição inválida. Deve ser 'gerente' ou 'funcionario'", exception.getMessage());
        verify(memberRepository, never()).findByAssignment(anyString());
    }

    @Test
    void findById_WithExistingId_ShouldReturnMember() {
        // Arrange
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // Act
        Member result = memberService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("João Silva", result.getName());

        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    void findById_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            memberService.findById(999L);
        });

        assertEquals("Membro não encontrado com ID: 999", exception.getMessage());
        verify(memberRepository, times(1)).findById(999L);
    }

    @Test
    void convertToDTO_ShouldReturnMemberDTO() {
        // Arrange
        when(modelMapper.map(member, MemberDTO.class)).thenReturn(memberDTO);

        // Act
        MemberDTO result = memberService.convertToDTO(member);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("João Silva", result.getName());
        assertEquals("funcionario", result.getAssignment());

        verify(modelMapper, times(1)).map(member, MemberDTO.class);
    }
}