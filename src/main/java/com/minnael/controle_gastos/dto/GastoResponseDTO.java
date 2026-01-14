package com.minnael.controle_gastos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoResponseDTO {

    private String id;
    private String nome;
    private String descricao;
    private BigDecimal valor;
    private String categoriaId;
    private String categoriaNome;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
