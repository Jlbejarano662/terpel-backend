package com.terpel.backend.model.dto;

import java.math.BigDecimal;

import com.terpel.backend.model.entity.EstadoEstacion;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EstacionResponseDto {
    private Long id;

    private String codigo;

    private String nombre;

    private String direccion;

    private String ciudad;

    private BigDecimal latitud;

    private BigDecimal longitud;

    @Enumerated(EnumType.STRING)
    private EstadoEstacion estado;
}
