package com.terpel.backend.model.dto;

import java.math.BigDecimal;

import com.terpel.backend.model.entity.EstadoEstacion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EstacionRequestDto {
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 100, message = "El código no puede superar 100 caracteres")
    @Schema(description = "Identificador único de la estación", example = "EST001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    @Schema(description = "Nombre de la estación", example = "Estación Central", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no puede superar 200 caracteres")
    @Schema(description = "Dirección de la estación", example = "Calle Principal 123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String direccion;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede superar 100 caracteres")
    @Schema(description = "Ciudad donde se encuentra la estación", example = "Bogotá", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ciudad;

    @NotNull(message = "La latitud es obligatoria")
    @Digits(integer = 2, fraction = 8, message = "Latitud debe tener hasta 2 enteros y 8 decimales")
    @DecimalMin(value = "-90.0", message = "Latitud mínima -90.0")
    @DecimalMax(value = "90.0", message = "Latitud máxima 90.0")
    @Schema(description = "Latitud de la estación", example = "4.6097", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal latitud;

    @NotNull(message = "La longitud es obligatoria")
    @Digits(integer = 3, fraction = 8, message = "Longitud debe tener hasta 3 enteros y 8 decimales")
    @DecimalMin(value = "-180.0", message = "Longitud mínima -180.0")
    @DecimalMax(value = "180.0", message = "Longitud máxima 180.0")
    @Schema(description = "Longitud de la estación", example = "-74.0817", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal longitud;

    @NotNull(message = "El estado es obligatorio")
    @Schema(description = "Estado de la estación", example = "ACTIVA", requiredMode = Schema.RequiredMode.REQUIRED)
    @Enumerated(EnumType.STRING)
    private EstadoEstacion estado;
}
