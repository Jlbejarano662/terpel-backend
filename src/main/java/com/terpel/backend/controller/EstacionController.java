package com.terpel.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terpel.backend.model.dto.EstacionRequestDto;
import com.terpel.backend.model.dto.EstacionResponseDto;
import com.terpel.backend.service.EstacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/stations")
@Tag(name = "Estaciones", description = "API de administración de estaciones")
public class EstacionController {

    private final EstacionService estacionService;

    @PostMapping()
    @Operation(summary = "Crear estación", description = "Crea una nueva estación con los datos proporcionados")
    @ApiResponse(responseCode = "201", description = "Estación creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
    @ApiResponse(responseCode = "500", description = "Error interno inesperado")
    public ResponseEntity<EstacionResponseDto> crearEstacion(@Valid @RequestBody EstacionRequestDto estacionDto) {
        log.info("Creando estación: {}", estacionDto);
        EstacionResponseDto createdEstacion = estacionService.crearEstacion(estacionDto);
        return ResponseEntity.status(201).body(createdEstacion);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener estación", description = "Obtiene los detalles de una estación por su ID")
    @ApiResponse(responseCode = "200", description = "Estación encontrada exitosamente")
    @ApiResponse(responseCode = "404", description = "Estación no encontrada")
    @ApiResponse(responseCode = "500", description = "Error interno inesperado")
    public ResponseEntity<EstacionResponseDto> obtenerEstacion(
            @PathVariable(name = "id") @NotNull(message = "El ID es obligatorio") Long id) {
        log.info("Obteniendo estación con ID: {}", id);
        EstacionResponseDto estacion = estacionService.obtenerEstacionPorId(id);
        return ResponseEntity.ok(estacion);
    }

    @GetMapping()
    @Operation(summary = "Listar estaciones", description = "Obtiene una lista de todas las estaciones")
    @ApiResponse(responseCode = "200", description = "Estaciones listadas exitosamente")
    @ApiResponse(responseCode = "500", description = "Error interno inesperado")
    public ResponseEntity<List<EstacionResponseDto>> listarEstaciones() {
        log.info("Listando estaciones");
        List<EstacionResponseDto> estaciones = estacionService.obtenerTodasLasEstaciones();
        return ResponseEntity.ok(estaciones);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar estación", description = "Actualiza los datos de una estación por su ID")
    @ApiResponse(responseCode = "200", description = "Estación actualizada exitosamente")
    @ApiResponse(responseCode = "404", description = "Estación no encontrada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
    @ApiResponse(responseCode = "500", description = "Error interno inesperado")
    public ResponseEntity<EstacionResponseDto> actualizarEstacion(
            @PathVariable(name = "id") @NotNull(message = "El ID es obligatorio") Long id,
            @Valid @RequestBody EstacionRequestDto estacionDto) {
        log.info("Actualizando estación con ID: {}", id);
        EstacionResponseDto updatedEstacion = estacionService.actualizarEstacion(id, estacionDto);
        return ResponseEntity.ok(updatedEstacion);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar estación", description = "Elimina una estación por su ID")
    @ApiResponse(responseCode = "204", description = "Estación eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Estación no encontrada")
    @ApiResponse(responseCode = "500", description = "Error interno inesperado")
    public ResponseEntity<Void> eliminarEstacion(
            @PathVariable(name = "id") @NotNull(message = "El ID es obligatorio") Long id) {
        log.info("Eliminando estación con ID: {}", id);
        estacionService.eliminarEstacion(id);
        return ResponseEntity.noContent().build();
    }
}
