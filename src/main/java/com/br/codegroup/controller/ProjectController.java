package com.br.codegroup.controller;


import com.br.codegroup.dto.ProjectDTO;
import com.br.codegroup.dto.ProjectRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(
        name = "Projects",
        description = "API para gerenciamento completo do portfólio de projetos.\n\n" +
                "📊 **Funcionalidades**:\n" +
                "- CRUD completo de projetos\n" +
                "- Gestão de equipes e alocações\n" +
                "- Cálculo automático de risco\n" +
                "- Controle de status e transições\n" +
                "- Relatórios e analytics\n\n" +
                "🎯 **Regras de Negócio Implementadas**:\n" +
                "- Classificação de risco baseada em orçamento e prazo\n" +
                "- Fluxo sequencial de status (não é possível pular etapas)\n" +
                "- Validação de gerentes e funcionários\n" +
                "- Limites de alocação de membros\n" +
                "- Restrições de exclusão por status"
)
public interface ProjectController {

    @Operation(
            summary = "Listar todos os projetos com paginação",
            description = "Retorna uma lista paginada de todos os projetos do portfólio.\n\n" +
                    "**Características**:\n" +
                    "- Suporte a ordenação por diversos campos\n" +
                    "- Paginação configurável (page, size, sort)\n" +
            "- Inclui dados de gerente e equipe\n\n" +
            "📌 **Cenários de Uso**:\n" +
            "- Visualização geral do portfólio\n" +
            "- Dashboard de projetos\n" +
            "- Análise de capacidade",
    parameters = {
        @Parameter(
                name = "page",
                description = "Número da página (0-based)",
                example = "0"
        ),
        @Parameter(
                name = "size",
                description = "Quantidade de itens por página",
                example = "10"
        ),
        @Parameter(
                name = "sort",
                description = "Campo para ordenação (ex: name,asc)",
                example = "name,asc"
        )
    }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de projetos recuperada com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    ResponseEntity<Page<ProjectDTO>> getAllProjects(Pageable pageable);

    @Operation(
            summary = "Buscar projeto por ID",
            description = "Recupera os detalhes completos de um projeto específico.\n\n" +
                    "**Inclui**:\n" +
                    "- Dados básicos do projeto\n" +
                    "- Informações do gerente responsável\n" +
            "- Lista de membros da equipe\n" +
            "- Classificação de risco calculada\n\n" +
            "📌 **Cenários de Uso**:\n" +
            "- Edição de projetos\n" +
            "- Visualização de detalhes\n" +
            "- Análise de projeto específico",
    parameters = {
        @Parameter(
                name = "id",
                description = "ID único do projeto",
                required = true,
                example = "1"
        )
    }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Projeto encontrado com sucesso",
            content = @Content(schema = @Schema(implementation = ProjectDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Projeto não encontrado"
    )
    ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id);

    @Operation(
            summary = "Criar novo projeto",
            description = "Cria um novo projeto no portfólio com validações completas.\n\n" +
                    "**Validações Aplicadas**:\n" +
                      "- Gerente deve existir e ter atribuição 'gerente'\n" +
            "- Membros devem existir e ter atribuição 'funcionario'\n" +
            "- Projeto deve ter entre 1 e 10 membros\n" +
            "- Membros não podem estar em mais de 3 projetos ativos\n" +
            "- Datas devem ser consistentes\n\n" +
            "📌 **Cenários de Uso**:\n" +
            "- Iniciação de novos projetos\n" +
            "- Planejamento de portfólio\n" +
            "- Alocação de recursos",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados do projeto a ser criado",
            required = true,
            content = @Content(schema = @Schema(implementation = ProjectRequestDTO.class))
    )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Projeto criado com sucesso",
            content = @Content(schema = @Schema(implementation = ProjectDTO.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou violação de regras de negócio"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Gerente ou membros não encontrados"
    )
    ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectRequestDTO projectRequestDTO);

    @Operation(
            summary = "Atualizar projeto existente",
            description = "Atualiza os dados de um projeto existente com validações completas.\n\n" +
                    "**Validações Aplicadas**:\n" +
                      "- Transições de status devem seguir sequência lógica\n" +
            "- Projetos com status iniciado/andamento/encerrado não podem ter status alterado\n" +
            "- Validações de gerente e membros mantidas\n" +
            "- Recalcula classificação de risco automaticamente\n\n" +
            "📌 **Cenários de Uso**:\n" +
            "- Atualização de informações do projeto\n" +
            "- Progressão de status\n" +
            "- Realocação de recursos",
    parameters = {
        @Parameter(
                name = "id",
                description = "ID único do projeto a ser atualizado",
                required = true,
                example = "1"
        )
    },
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Novos dados do projeto",
            required = true,
            content = @Content(schema = @Schema(implementation = ProjectRequestDTO.class))
    )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Projeto atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = ProjectDTO.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou violação de regras de negócio"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Projeto, gerente ou membros não encontrados"
    )
    ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDTO projectRequestDTO);

    @Operation(
            summary = "Excluir projeto",
            description = "Remove um projeto do portfólio com validações de status.\n\n" +
                    "**Restrições**:\n" +
                      "- Projetos com status 'iniciado', 'em andamento' ou 'encerrado' não podem ser excluídos\n" +
            "- Apenas projetos em status inicial podem ser removidos\n\n" +
            "📌 **Cenários de Uso**:\n" +
            "- Remoção de projetos cancelados\n" +
            "- Limpeza de projetos em planejamento\n" +
            "- Correção de cadastros incorretos",
    parameters = {
        @Parameter(
                name = "id",
                description = "ID único do projeto a ser excluído",
                required = true,
                example = "1"
        )
    }
    )
    @ApiResponse(
            responseCode = "204",
            description = "Projeto excluído com sucesso"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Não é possível excluir projeto com status atual"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Projeto não encontrado"
    )
    ResponseEntity<Void> deleteProject(@PathVariable Long id);

    @Operation(
            summary = "Buscar projetos por nome",
            description = "Filtra projetos por nome com correspondência parcial (case insensitive).\n\n" +
                    "**Funcionalidades**:\n" +
                      "- Busca por substring no nome\n" +
            "- Suporte a paginação e ordenação\n" +
            "- Retorna resultados parciais\n\n" +
            "📌 **Cenários de Uso**:\n" +
            "- Busca rápida de projetos\n" +
            "- Filtragem por nome\n" +
            "- Localização de projetos específicos",
    parameters = {
        @Parameter(
                name = "name",
                description = "Termo de busca para o nome do projeto",
                required = true,
                example = "sistema"
        ),
        @Parameter(
                name = "page",
                description = "Número da página (0-based)",
                example = "0"
        ),
        @Parameter(
                name = "size",
                description = "Quantidade de itens por página",
                example = "10"
        ),
        @Parameter(
                name = "sort",
                description = "Campo para ordenação",
                example = "name,asc"
        )
    }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Projetos filtrados por nome",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    ResponseEntity<Page<ProjectDTO>> getProjectsByName(
            @RequestParam String name,
            Pageable pageable);

    @Operation(
            summary = "Buscar projetos por status",
            description = "Filtra projetos por status específico.\n\n" +
                    "**Status disponíveis**:\n" +
                    "- UNDER_REVIEW (em análise)\n" +
            "- REVIEW_COMPLETED (análise realizada)\n" +
            "- REVIEW_APPROVED (análise aprovada)\n" +
            "- STARTED (iniciado)\n" +
            "- PLANNED (planejado)\n" +
            "- IN_PROGRESS (em andamento)\n" +
            "- COMPLETED (encerrado)\n" +
            "- CANCELLED (cancelado)\n\n" +
            "📌 **Cenários de Uso**:\n" +
            "- Filtro por fase do projeto\n" +
            "- Relatórios por status\n" +
            "- Gestão por estado",
    parameters = {
        @Parameter(
                name = "status",
                description = "Status do projeto para filtro",
                required = true,
                example = "IN_PROGRESS",
                schema = @Schema(
                        type = "string",
                        allowableValues = {
                                "UNDER_REVIEW", "REVIEW_COMPLETED", "REVIEW_APPROVED",
                                "STARTED", "PLANNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"
                        }
                )
        ),
        @Parameter(
                name = "page",
                description = "Número da página (0-based)",
                example = "0"
        ),
        @Parameter(
                name = "size",
                description = "Quantidade de itens por página",
                example = "10"
        ),
        @Parameter(
                name = "sort",
                description = "Campo para ordenação",
                example = "name,asc"
        )
    }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Projetos filtrados por status",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Status inválido"
    )
    ResponseEntity<Page<ProjectDTO>> getProjectsByStatus(
            @RequestParam String status,
            Pageable pageable);

    @Operation(
            summary = "Buscar projetos por gerente",
            description = "Filtra projetos por gerente responsável.\n\n" +
                    "**Validações**:\n" +
                      "- Gerente deve existir\n" +
            "- Gerente deve ter atribuição 'gerente'\n\n" +
            "📌 **Cenários de Uso**:\n" +
            "- Visualização de portfólio por gerente\n" +
            "- Gestão de responsabilidades\n" +
            "- Análise de carga de trabalho",
    parameters = {
        @Parameter(
                name = "managerId",
                description = "ID do gerente responsável",
                required = true,
                example = "1"
        ),
        @Parameter(
                name = "page",
                description = "Número da página (0-based)",
                example = "0"
        ),
        @Parameter(
                name = "size",
                description = "Quantidade de itens por página",
                example = "10"
        ),
        @Parameter(
                name = "sort",
                description = "Campo para ordenação",
                example = "name,asc"
        )
    }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Projetos do gerente especificado",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Gerente não encontrado ou não é gerente"
    )
    ResponseEntity<Page<ProjectDTO>> getProjectsByManager(
            @PathVariable Long managerId,
            Pageable pageable);
}