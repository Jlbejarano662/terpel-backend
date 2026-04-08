package com.terpel.backend.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "estaciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEstacion estado;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

}
