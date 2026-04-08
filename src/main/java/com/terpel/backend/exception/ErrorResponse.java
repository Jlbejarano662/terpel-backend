package com.terpel.backend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Estructura estándar de respuesta de error")
public class ErrorResponse {

    @Schema(description = "Código HTTP", example = "404")
    private int status;

    @Schema(description = "Tipo de error", example = "Not Found")
    private String error;

    @Schema(description = "Mensaje descriptivo", example = "Estación no encontrada con código: ABC123")
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Timestamp del error", example = "2026-04-02 10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Ruta de la petición", example = "/api/v1/estaciones/ABC123")
    private String path;

    @Schema(description = "Detalle de errores de validación (si aplica)")
    private List<String> details;
}
