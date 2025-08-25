package com.br.codegroup.controller.impl;


import com.br.codegroup.controller.MemberController;
import com.br.codegroup.dto.MemberDTO;
import com.br.codegroup.dto.MemberRequestDTO;
import com.br.codegroup.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/external/members")
@RequiredArgsConstructor
public class MemberControllerImpl implements MemberController {

    private final MemberService memberService;

    @Override
    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@Valid @RequestBody MemberRequestDTO memberRequestDTO) {
        MemberDTO createdMember = memberService.createMember(memberRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        MemberDTO member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        List<MemberDTO> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @Override
    @GetMapping("/assignment")
    public ResponseEntity<List<MemberDTO>> getMembersByAssignment(@RequestParam String assignment) {
        List<MemberDTO> members = memberService.getMembersByAssignment(assignment);
        return ResponseEntity.ok(members);
    }
}