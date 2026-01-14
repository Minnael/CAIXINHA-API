package com.minnael.controle_gastos.service;

import com.minnael.controle_gastos.dto.CategoriaRequestDTO;
import com.minnael.controle_gastos.dto.CategoriaResponseDTO;
import com.minnael.controle_gastos.entity.Categoria;
import com.minnael.controle_gastos.entity.Gasto;
import com.minnael.controle_gastos.exception.BusinessException;
import com.minnael.controle_gastos.exception.ResourceNotFoundException;
import com.minnael.controle_gastos.mapper.CategoriaMapper;
import com.minnael.controle_gastos.repository.CategoriaRepository;
import com.minnael.controle_gastos.repository.GastoRepository;
import com.minnael.controle_gastos.security.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final GastoRepository gastoRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaResponseDTO criar(CategoriaRequestDTO requestDTO) {
        String userId = UserContext.getUserId(); // Extrai userId do contexto autenticado
        log.info("Criando nova categoria: {} para userId: {}", requestDTO.getNome(), userId);

        if (categoriaRepository.existsByNomeAndUserId(requestDTO.getNome(), userId)) {
            throw new BusinessException("Já existe uma categoria com o nome: " + requestDTO.getNome());
        }

        Categoria categoria = categoriaMapper.toEntity(requestDTO, userId);
        categoria.onCreate();
        categoria = categoriaRepository.save(categoria);

        log.info("Categoria criada com sucesso. ID: {}", categoria.getId());
        return categoriaMapper.toResponseDTO(categoria);
    }

    public List<CategoriaResponseDTO> listarTodas() {
        String userId = UserContext.getUserId();
        log.info("Listando todas as categorias do userId: {}", userId);
        
        List<Categoria> categorias = categoriaRepository.findByUserId(userId);
        return categorias.stream()
                .map(categoria -> {
                    CategoriaResponseDTO dto = categoriaMapper.toResponseDTO(categoria);
                    // Calcula gasto atual
                    BigDecimal gastoAtual = gastoRepository.findByCategoriaIdAndUserId(categoria.getId(), userId)
                        .stream()
                        .map(Gasto::getValor)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    dto.setGastoAtual(gastoAtual);
                    dto.setTotalGastos((int) gastoRepository.findByCategoriaIdAndUserId(categoria.getId(), userId).size());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public CategoriaResponseDTO buscarPorId(String id) {
        String userId = UserContext.getUserId();
        log.info("Buscando categoria ID: {} para userId: {}", id, userId);
        
        Categoria categoria = categoriaRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o ID: " + id));
        
        // Busca os gastos separadamente (já filtrados por userId)
        List<Gasto> gastos = gastoRepository.findByCategoriaIdAndUserId(id, userId);
        return categoriaMapper.toResponseDTOWithGastos(categoria, gastos);
    }

    public CategoriaResponseDTO atualizar(String id, CategoriaRequestDTO requestDTO) {
        String userId = UserContext.getUserId();
        log.info("Atualizando categoria ID: {} para userId: {}", id, userId);

        Categoria categoria = categoriaRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o ID: " + id));

        // Verifica se o novo nome já existe em outra categoria do mesmo usuário
        if (!categoria.getNome().equals(requestDTO.getNome()) && 
            categoriaRepository.existsByNomeAndUserId(requestDTO.getNome(), userId)) {
            throw new BusinessException("Já existe uma categoria com o nome: " + requestDTO.getNome());
        }

        categoriaMapper.updateEntity(categoria, requestDTO);
        categoria.onUpdate();
        categoria = categoriaRepository.save(categoria);

        log.info("Categoria atualizada com sucesso. ID: {}", categoria.getId());
        BigDecimal gastoAtual = gastoRepository.findByCategoriaIdAndUserId(id, userId)
            .stream()
            .map(Gasto::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        CategoriaResponseDTO dto = categoriaMapper.toResponseDTO(categoria);
        dto.setGastoAtual(gastoAtual);
        dto.setTotalGastos((int) gastoRepository.findByCategoriaIdAndUserId(id, userId).size());
        return dto;
    }

    public void deletar(String id) {
        String userId = UserContext.getUserId();
        log.info("Deletando categoria ID: {} para userId: {}", id, userId);

        Categoria categoria = categoriaRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o ID: " + id));

        // Verifica se existem gastos vinculados (apenas do usuário)
        if (gastoRepository.existsByCategoriaIdAndUserId(id, userId)) {
            throw new BusinessException("Não é possível deletar a categoria pois existem gastos vinculados a ela");
        }

        categoriaRepository.delete(categoria);
        log.info("Categoria deletada com sucesso. ID: {}", id);
    }
}
