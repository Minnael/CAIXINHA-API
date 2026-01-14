package com.minnael.controle_gastos.repository;

import com.minnael.controle_gastos.entity.Categoria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends MongoRepository<Categoria, String> {

    // Queries isoladas por usuário (multi-tenancy)
    List<Categoria> findByUserId(String userId);
    
    Optional<Categoria> findByIdAndUserId(String id, String userId);
    
    Optional<Categoria> findByNomeAndUserId(String nome, String userId);

    boolean existsByNomeAndUserId(String nome, String userId);
    
    boolean existsByIdAndUserId(String id, String userId);
    
    void deleteByIdAndUserId(String id, String userId);
}
