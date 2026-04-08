package com.terpel.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terpel.backend.config.CacheConfig;
import com.terpel.backend.exception.DuplicateResourceException;
import com.terpel.backend.exception.ResourceNotFoundException;
import com.terpel.backend.model.dto.EstacionRequestDto;
import com.terpel.backend.model.dto.EstacionResponseDto;
import com.terpel.backend.model.entity.EstadoEstacion;
import com.terpel.backend.service.EstacionService;

@WebMvcTest(EstacionController.class)
@Import(CacheConfig.class)
class EstacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private EstacionService estacionService;

    private EstacionRequestDto requestDto;
    private EstacionResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = EstacionRequestDto.builder()
                .codigo("EST001")
                .nombre("Estación Central")
                .direccion("Calle Principal 123")
                .ciudad("Bogotá")
                .latitud(new BigDecimal("4.60970000"))
                .longitud(new BigDecimal("-74.08170000"))
                .estado(EstadoEstacion.ACTIVA)
                .build();

        responseDto = EstacionResponseDto.builder()
                .id(1L)
                .codigo("EST001")
                .nombre("Estación Central")
                .direccion("Calle Principal 123")
                .ciudad("Bogotá")
                .latitud(new BigDecimal("4.60970000"))
                .longitud(new BigDecimal("-74.08170000"))
                .estado(EstadoEstacion.ACTIVA)
                .build();
    }

    // ── POST /api/stations ─────────────────────────────────────────────────────

    @Test
    void crearEstacion_debeRetornar201_cuandoDatosValidos() throws Exception {
        when(estacionService.crearEstacion(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("EST001"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crearEstacion_debeRetornar400_cuandoCamposObligatoriosFaltantes() throws Exception {
        EstacionRequestDto invalido = EstacionRequestDto.builder().build();

        mockMvc.perform(post("/api/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void crearEstacion_debeRetornar409_cuandoCodigoDuplicado() throws Exception {
        when(estacionService.crearEstacion(any()))
                .thenThrow(new DuplicateResourceException("Ya existe una estación con el código: EST001"));

        mockMvc.perform(post("/api/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    // ── GET /api/stations/{id} ─────────────────────────────────────────────────

    @Test
    void obtenerEstacion_debeRetornar200_cuandoExiste() throws Exception {
        when(estacionService.obtenerEstacionPorId(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/stations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("EST001"));
    }

    @Test
    void obtenerEstacion_debeRetornar404_cuandoNoExiste() throws Exception {
        when(estacionService.obtenerEstacionPorId(99L))
                .thenThrow(new ResourceNotFoundException("Estación no encontrada con ID: 99"));

        mockMvc.perform(get("/api/stations/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ── GET /api/stations ──────────────────────────────────────────────────────

    @Test
    void listarEstaciones_debeRetornar200_conListaDeEstaciones() throws Exception {
        when(estacionService.obtenerTodasLasEstaciones()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigo").value("EST001"));
    }

    // ── PUT /api/stations/{id} ─────────────────────────────────────────────────

    @Test
    void actualizarEstacion_debeRetornar200_cuandoDatosValidos() throws Exception {
        when(estacionService.actualizarEstacion(eq(1L), any())).thenReturn(responseDto);

        mockMvc.perform(put("/api/stations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("EST001"));
    }

    @Test
    void actualizarEstacion_debeRetornar404_cuandoNoExiste() throws Exception {
        when(estacionService.actualizarEstacion(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Estación no encontrada con ID: 99"));

        mockMvc.perform(put("/api/stations/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ── DELETE /api/stations/{id} ──────────────────────────────────────────────

    @Test
    void eliminarEstacion_debeRetornar204_cuandoExiste() throws Exception {
        doNothing().when(estacionService).eliminarEstacion(1L);

        mockMvc.perform(delete("/api/stations/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarEstacion_debeRetornar404_cuandoNoExiste() throws Exception {
        doThrow(new ResourceNotFoundException("Estación no encontrada con ID: 99"))
                .when(estacionService).eliminarEstacion(99L);

        mockMvc.perform(delete("/api/stations/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
