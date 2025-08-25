package com.br.codegroup.controller;


import com.br.codegroup.dto.MemberDTO;
import com.br.codegroup.dto.MemberRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;


@Tag(
        name = "Members",
        description = "API REST externa para gerenciamento de membros (funcionários e gerentes).\n\n" +
                "📋 **Regras de Negócio**:\n" +
                "- Apenas duas atribuições são permitidas: 'gerente' ou 'funcionario'\n" +
                "- Gerentes podem ser responsáveis por projetos\n" +
                "- Apenas funcionários podem ser associados a projetos como membros da equipe\n" +
                "- Cada membro deve ter um nome único por atribuição"
)
public interface MemberController {

    @Operation(
            summary = "Criar novo membro",
            description = "Cria um novo membro na base. \n\n" +
                    "**Validações**:\n" +
                    "- Nome é obrigatório e deve ser único por atribuição\n" +
                    "- Atribuição deve ser 'gerente' ou 'funcionario'\n" +
                    "- Membros duplicados (mesmo nome e atribuição) não são permitidos\n\n" +
                    "📌 **Cenários de Uso**:\n" +
                    "- Cadastro inicial de gerentes para gerenciar projetos\n" +
                    "- Cadastro de funcionários para composição de equipes de projeto\n\n" +
                    "🎯 **Exemplo de uso**: Cadastrar um gerente para ser responsável por um projeto",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do membro a ser criado",
                    required = true
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Membro criado com sucesso",
            content = @Content(schema = @Schema(implementation = MemberDTO.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou membro já existe"
    )
    ResponseEntity<MemberDTO> createMember(@Valid @RequestBody MemberRequestDTO memberRequestDTO);

    @Operation(
            summary = "Buscar membro por ID",
            description = "Recupera os dados de um membro específico pelo seu identificador único.\n\n" +
                    "**Validações**:\n" +
                    "- ID deve existir na base de dados\n" +
                    "- Retorna 404 se o membro não for encontrado\n\n" +
                    "📌 **Cenários de Uso**:\n" +
                    "- Consultar dados de um gerente para associar a um projeto\n" +
                    "- Verificar se um funcionário existe antes de adicionar à equipe\n\n" +
                    "🎯 **Exemplo de uso**: Buscar dados de um gerente para definir como responsável de projeto"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Membro encontrado com sucesso",
            content = @Content(schema = @Schema(implementation = MemberDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Membro não encontrado"
    )
    ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id);

    @Operation(
            summary = "Listar todos os membros",
            description = "Retorna uma lista com todos os membros cadastrados no sistema mockado.\n\n" +
                    "**Observações**:\n" +
                    "- Retorna lista vazia se não houver membros cadastrados\n" +
                    "- Ordenação padrão por nome\n\n" +
                    "📌 **Cenários de Uso**:\n" +
                    "- Visualizar todos os recursos humanos disponíveis\n" +
                    "- Selecionar gerentes para projetos\n" +
                    "- Selecionar funcionários para equipes\n\n" +
                    "🎯 **Exemplo de uso**: Listar todos os membros para composição de equipes de projeto"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de membros recuperada com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MemberDTO.class)))
    )
    ResponseEntity<List<MemberDTO>> getAllMembers();

    @Operation(
            summary = "Listar membros por atribuição",
            description = "Filtra membros por tipo de atribuição (cargo).\n\n" +
                    "**Valores permitidos**:\n" +
                    "- 'gerente': Membros com função de gestão\n" +
                    "- 'funcionario': Membros para composição de equipes\n\n" +
                    "**Validações**:\n" +
                    "- Atribuição deve ser 'gerente' ou 'funcionario'\n" +
                    "- Retorna lista vazia se não houver membros com a atribuição especificada\n\n" +
                    "📌 **Cenários de Uso**:\n" +
                    "- Buscar apenas gerentes para atribuição a projetos\n" +
                    "- Buscar apenas funcionários para formação de equipes\n" +
                    "- Relatórios por tipo de atribuição\n\n" +
                    "🎯 **Exemplo de uso**: Listar todos os funcionários disponíveis para alocação em projetos",
            parameters = {
                    @Parameter(
                            name = "assignment",
                            description = "Tipo de atribuição: 'gerente' ou 'funcionario'",
                            required = true,
                            example = "gerente",
                            schema = @Schema(
                                    type = "string",
                                    allowableValues = {"gerente", "funcionario"},
                                    description = "Cargo do membro no sistema"
                            )
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista filtrada por atribuição",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MemberDTO.class)))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Atribuição inválida"
    )
    ResponseEntity<List<MemberDTO>> getMembersByAssignment(@RequestParam String assignment);
}