package com.br.codegroup.dto;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MemberDTO {
    private Long id;
    private String name;
    private String assignment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}