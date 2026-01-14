package com.minnael.controle_gastos.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "gastos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gasto {

    @Id
    private String id;

    @Indexed // Índice para performance em queries multi-tenant
    private String userId; // ID do usuário proprietário (do JWT)

    private String nome;
    private String descricao;
    private BigDecimal valor;
    private String categoriaId; // Referência ao ID da categoria

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    public void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}
