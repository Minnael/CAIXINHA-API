package com.minnael.controle_gastos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaResponseDTO {

    private String id;
    private String nome;
    private String icone;
    private String descricao;
    private BigDecimal gastoMensal; // Meta/limite definido pelo usuário
    private BigDecimal gastoAtual;  // Quanto já foi gasto (calculado)
    private Integer totalGastos;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private List<GastoResponseDTO> gastos;
}
