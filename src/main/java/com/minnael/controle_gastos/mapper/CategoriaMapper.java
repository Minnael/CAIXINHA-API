package com.minnael.controle_gastos.mapper;

import com.minnael.controle_gastos.dto.CategoriaRequestDTO;
import com.minnael.controle_gastos.dto.CategoriaResponseDTO;
import com.minnael.controle_gastos.dto.GastoResponseDTO;
import com.minnael.controle_gastos.entity.Categoria;
import com.minnael.controle_gastos.entity.Gasto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoriaMapper {

    public Categoria toEntity(CategoriaRequestDTO dto, String userId) {
        Categoria categoria = new Categoria();
        categoria.setUserId(userId);
        categoria.setNome(dto.getNome());
        categoria.setIcone(dto.getIcone());
        categoria.setDescricao(dto.getDescricao());
        categoria.setGastoMensal(dto.getGastoMensal() != null ? dto.getGastoMensal() : BigDecimal.ZERO);
        return categoria;
    }

    public CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .icone(categoria.getIcone())
                .descricao(categoria.getDescricao())
                .gastoMensal(categoria.getGastoMensal() != null ? categoria.getGastoMensal() : BigDecimal.ZERO)
                .gastoAtual(BigDecimal.ZERO) // Será calculado no service
                .totalGastos(0) // Será calculado no service
                .criadoEm(categoria.getCriadoEm())
                .atualizadoEm(categoria.getAtualizadoEm())
                .build();
    }

    public CategoriaResponseDTO toResponseDTOWithGastos(Categoria categoria, List<Gasto> gastos) {
        BigDecimal gastoAtual = gastos != null ? 
            gastos.stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
                
        return CategoriaResponseDTO.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .icone(categoria.getIcone())
                .descricao(categoria.getDescricao())
                .gastoMensal(categoria.getGastoMensal() != null ? categoria.getGastoMensal() : BigDecimal.ZERO)
                .gastoAtual(gastoAtual)
                .totalGastos(gastos != null ? gastos.size() : 0)
                .criadoEm(categoria.getCriadoEm())
                .atualizadoEm(categoria.getAtualizadoEm())
                .gastos(gastos != null ? 
                        gastos.stream()
                            .map(gasto -> GastoResponseDTO.builder()
                                    .id(gasto.getId())
                                    .nome(gasto.getNome())
                                    .descricao(gasto.getDescricao())
                                    .valor(gasto.getValor())
                                    .categoriaId(categoria.getId())
                                    .categoriaNome(categoria.getNome())
                                    .criadoEm(gasto.getCriadoEm())
                                    .atualizadoEm(gasto.getAtualizadoEm())
                                    .build())
                            .collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

    public void updateEntity(Categoria categoria, CategoriaRequestDTO dto) {
        categoria.setNome(dto.getNome());
        categoria.setIcone(dto.getIcone());
        categoria.setDescricao(dto.getDescricao());
        if (dto.getGastoMensal() != null) {
            categoria.setGastoMensal(dto.getGastoMensal());
        }
    }
}
