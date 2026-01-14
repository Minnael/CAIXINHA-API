package com.minnael.controle_gastos.controller;

import com.minnael.controle_gastos.dto.GastoRequestDTO;
import com.minnael.controle_gastos.dto.GastoResponseDTO;
import com.minnael.controle_gastos.service.GastoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gastos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Gastos", description = "Endpoints para gerenciamento de gastos")
public class GastoController {

    private final GastoService gastoService;

    @Operation(summary = "Criar novo gasto", description = "Cria um novo gasto vinculado a uma categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Gasto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @PostMapping
    public ResponseEntity<GastoResponseDTO> criar(@Valid @RequestBody GastoRequestDTO requestDTO) {
        GastoResponseDTO response = gastoService.criar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar todos os gastos", description = "Retorna uma lista com todos os gastos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de gastos retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<GastoResponseDTO>> listarTodos() {
        List<GastoResponseDTO> gastos = gastoService.listarTodos();
        return ResponseEntity.ok(gastos);
    }

    @Operation(summary = "Listar gastos por categoria", description = "Retorna todos os gastos de uma categoria específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de gastos retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<GastoResponseDTO>> listarPorCategoria(@PathVariable String categoriaId) {
        List<GastoResponseDTO> gastos = gastoService.listarPorCategoria(categoriaId);
        return ResponseEntity.ok(gastos);
    }

    @Operation(summary = "Buscar gasto por ID", description = "Retorna um gasto específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gasto encontrado"),
            @ApiResponse(responseCode = "404", description = "Gasto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GastoResponseDTO> buscarPorId(@PathVariable String id) {
        GastoResponseDTO gasto = gastoService.buscarPorId(id);
        return ResponseEntity.ok(gasto);
    }

    @Operation(summary = "Atualizar gasto", description = "Atualiza os dados de um gasto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gasto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Gasto ou categoria não encontrados")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GastoResponseDTO> atualizar(
            @PathVariable String id,
            @Valid @RequestBody GastoRequestDTO requestDTO) {
        GastoResponseDTO response = gastoService.atualizar(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deletar gasto", description = "Remove um gasto e atualiza o gasto mensal da categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Gasto deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Gasto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        gastoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
