package com.minnael.controle_gastos.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    private String id;

    @Indexed // Índice para performance em queries multi-tenant
    private String userId; // ID do usuário proprietário (do JWT)

    private String nome;
    private String icone;
    private String descricao;
    private BigDecimal gastoMensal; // Meta/limite mensal definido pelo usuário

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    // Calcula o gasto atual total baseado nos gastos (será feito via agregação)
    public BigDecimal calcularGastoAtual() {
        return BigDecimal.ZERO; // Será calculado dinamicamente no service
    }

    public void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    public void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}
