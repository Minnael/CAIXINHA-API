package com.minnael.controle_gastos.service;

import com.minnael.controle_gastos.dto.GastoRequestDTO;
import com.minnael.controle_gastos.dto.GastoResponseDTO;
import com.minnael.controle_gastos.entity.Categoria;
import com.minnael.controle_gastos.entity.Gasto;
import com.minnael.controle_gastos.exception.ResourceNotFoundException;
import com.minnael.controle_gastos.mapper.GastoMapper;
import com.minnael.controle_gastos.repository.CategoriaRepository;
import com.minnael.controle_gastos.repository.GastoRepository;
import com.minnael.controle_gastos.security.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GastoService {

    private final GastoRepository gastoRepository;
    private final CategoriaRepository categoriaRepository;
    private final GastoMapper gastoMapper;

    public GastoResponseDTO criar(GastoRequestDTO requestDTO) {
        String userId = UserContext.getUserId();
        log.info("Criando novo gasto: {} para categoria ID: {} userId: {}", 
                requestDTO.getNome(), requestDTO.getCategoriaId(), userId);

        // Valida se a categoria pertence ao usuário autenticado
        Categoria categoria = categoriaRepository.findByIdAndUserId(requestDTO.getCategoriaId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o ID: " + requestDTO.getCategoriaId()));

        Gasto gasto = gastoMapper.toEntity(requestDTO, categoria, userId);
        gasto.onCreate();
        gasto = gastoRepository.save(gasto);

        log.info("Gasto criado com sucesso. ID: {}", gasto.getId());
        return gastoMapper.toResponseDTO(gasto, categoria);
    }

    public List<GastoResponseDTO> listarTodos() {
        String userId = UserContext.getUserId();
        log.info("Listando todos os gastos do userId: {}", userId);
        
        List<Gasto> gastos = gastoRepository.findByUserId(userId);
        return gastos.stream()
                .map(gasto -> {
                    Categoria categoria = categoriaRepository.findByIdAndUserId(gasto.getCategoriaId(), userId)
                            .orElse(null);
                    return gastoMapper.toResponseDTO(gasto, categoria);
                })
                .collect(Collectors.toList());
    }

    public List<GastoResponseDTO> listarPorCategoria(String categoriaId) {
        String userId = UserContext.getUserId();
        log.info("Listando gastos da categoria ID: {} para userId: {}", categoriaId, userId);

        // Valida se a categoria pertence ao usuário
        Categoria categoria = categoriaRepository.findByIdAndUserId(categoriaId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o ID: " + categoriaId));

        List<Gasto> gastos = gastoRepository.findByCategoriaIdAndUserIdOrderByCriadoEmDesc(categoriaId, userId);
        return gastos.stream()
                .map(gasto -> gastoMapper.toResponseDTO(gasto, categoria))
                .collect(Collectors.toList());
    }

    public GastoResponseDTO buscarPorId(String id) {
        String userId = UserContext.getUserId();
        log.info("Buscando gasto ID: {} para userId: {}", id, userId);
        
        Gasto gasto = gastoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto não encontrado com o ID: " + id));
        
        Categoria categoria = categoriaRepository.findByIdAndUserId(gasto.getCategoriaId(), userId)
                .orElse(null);
        return gastoMapper.toResponseDTO(gasto, categoria);
    }

    public GastoResponseDTO atualizar(String id, GastoRequestDTO requestDTO) {
        String userId = UserContext.getUserId();
        log.info("Atualizando gasto ID: {} para userId: {}", id, userId);

        Gasto gasto = gastoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto não encontrado com o ID: " + id));
        
        // Valida se a nova categoria pertence ao usuário
        Categoria categoriaNova = categoriaRepository.findByIdAndUserId(requestDTO.getCategoriaId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o ID: " + requestDTO.getCategoriaId()));

        gastoMapper.updateEntity(gasto, requestDTO, categoriaNova);
        gasto.onUpdate();
        gasto = gastoRepository.save(gasto);

        log.info("Gasto atualizado com sucesso. ID: {}", gasto.getId());
        return gastoMapper.toResponseDTO(gasto, categoriaNova);
    }

    public void deletar(String id) {
        String userId = UserContext.getUserId();
        log.info("Deletando gasto ID: {} para userId: {}", id, userId);

        Gasto gasto = gastoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto não encontrado com o ID: " + id));

        gastoRepository.delete(gasto);

        log.info("Gasto deletado com sucesso. ID: {}", id);
    }
}
