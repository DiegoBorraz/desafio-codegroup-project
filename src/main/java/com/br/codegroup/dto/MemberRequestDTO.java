package com.br.codegroup.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberRequestDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String assignment; // "gerente" ou "funcionario"
}