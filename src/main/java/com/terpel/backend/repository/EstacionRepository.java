package com.terpel.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.terpel.backend.model.entity.Estacion;

@Repository
public interface EstacionRepository extends JpaRepository<Estacion, Long> {

    boolean existsByCodigo(String codigo);
}
