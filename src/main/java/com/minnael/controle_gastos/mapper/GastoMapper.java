package com.minnael.controle_gastos.mapper;

import com.minnael.controle_gastos.dto.GastoRequestDTO;
import com.minnael.controle_gastos.dto.GastoResponseDTO;
import com.minnael.controle_gastos.entity.Categoria;
import com.minnael.controle_gastos.entity.Gasto;
import org.springframework.stereotype.Component;

@Component
public class GastoMapper {

    public Gasto toEntity(GastoRequestDTO dto, Categoria categoria, String userId) {
        Gasto gasto = new Gasto();
        gasto.setUserId(userId);
        gasto.setNome(dto.getNome());
        gasto.setDescricao(dto.getDescricao());
        gasto.setValor(dto.getValor());
        gasto.setCategoriaId(dto.getCategoriaId());
        return gasto;
    }

    public GastoResponseDTO toResponseDTO(Gasto gasto, Categoria categoria) {
        return GastoResponseDTO.builder()
                .id(gasto.getId())
                .nome(gasto.getNome())
                .descricao(gasto.getDescricao())
                .valor(gasto.getValor())
                .categoriaId(gasto.getCategoriaId())
                .categoriaNome(categoria != null ? categoria.getNome() : "Categoria não encontrada")
                .criadoEm(gasto.getCriadoEm())
                .atualizadoEm(gasto.getAtualizadoEm())
                .build();
    }

    public void updateEntity(Gasto gasto, GastoRequestDTO dto, Categoria categoria) {
        gasto.setNome(dto.getNome());
        gasto.setDescricao(dto.getDescricao());
        gasto.setValor(dto.getValor());
        gasto.setCategoriaId(dto.getCategoriaId());
    }
}
