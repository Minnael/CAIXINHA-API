package com.minnael.controle_gastos.repository;

import com.minnael.controle_gastos.entity.Gasto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GastoRepository extends MongoRepository<Gasto, String> {

    // Queries isoladas por usuário (multi-tenancy)
    List<Gasto> findByUserId(String userId);
    
    Optional<Gasto> findByIdAndUserId(String id, String userId);
    
    List<Gasto> findByCategoriaIdAndUserId(String categoriaId, String userId);

    List<Gasto> findByCategoriaIdAndUserIdOrderByCriadoEmDesc(String categoriaId, String userId);

    boolean existsByCategoriaIdAndUserId(String categoriaId, String userId);

    void deleteByCategoriaIdAndUserId(String categoriaId, String userId);
    
    void deleteByIdAndUserId(String id, String userId);
}
