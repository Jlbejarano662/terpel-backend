package com.terpel.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.terpel.backend.exception.DuplicateResourceException;
import com.terpel.backend.exception.ResourceNotFoundException;
import com.terpel.backend.mapper.EstacionMapper;
import com.terpel.backend.model.dto.EstacionRequestDto;
import com.terpel.backend.model.dto.EstacionResponseDto;
import com.terpel.backend.model.entity.Estacion;
import com.terpel.backend.model.entity.EstadoEstacion;
import com.terpel.backend.repository.EstacionRepository;
import com.terpel.backend.service.impl.EstacionServiceImpl;

@ExtendWith(MockitoExtension.class)
class EstacionServiceImplTest {

    @Mock
    private EstacionRepository estacionRepository;

    @Mock
    private EstacionMapper mapper;

    @InjectMocks
    private EstacionServiceImpl service;

    private EstacionRequestDto requestDto;
    private EstacionResponseDto responseDto;
    private Estacion estacion;

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

        estacion = Estacion.builder()
                .id(1L)
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

    // ── crearEstacion ──────────────────────────────────────────────────────────

    @Test
    void crearEstacion_debeRetornarResponseDto_cuandoCodigoNoExiste() {
        when(estacionRepository.existsByCodigo("EST001")).thenReturn(false);
        when(mapper.toEntity(requestDto)).thenReturn(estacion);
        when(estacionRepository.save(estacion)).thenReturn(estacion);
        when(mapper.toDto(estacion)).thenReturn(responseDto);

        EstacionResponseDto result = service.crearEstacion(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getCodigo()).isEqualTo("EST001");
        verify(estacionRepository).save(estacion);
    }

    @Test
    void crearEstacion_debeLanzarDuplicateResourceException_cuandoCodigoYaExiste() {
        when(estacionRepository.existsByCodigo("EST001")).thenReturn(true);

        assertThatThrownBy(() -> service.crearEstacion(requestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("EST001");

        verify(estacionRepository, never()).save(any());
    }

    // ── obtenerEstacionPorId ───────────────────────────────────────────────────

    @Test
    void obtenerEstacionPorId_debeRetornarResponseDto_cuandoExiste() {
        when(estacionRepository.findById(1L)).thenReturn(Optional.of(estacion));
        when(mapper.toDto(estacion)).thenReturn(responseDto);

        EstacionResponseDto result = service.obtenerEstacionPorId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void obtenerEstacionPorId_debeLanzarResourceNotFoundException_cuandoNoExiste() {
        when(estacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerEstacionPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── obtenerTodasLasEstaciones ──────────────────────────────────────────────

    @Test
    void obtenerTodasLasEstaciones_debeRetornarListaDeEstaciones() {
        when(estacionRepository.findAll()).thenReturn(List.of(estacion));
        when(mapper.toDtoList(List.of(estacion))).thenReturn(List.of(responseDto));

        List<EstacionResponseDto> result = service.obtenerTodasLasEstaciones();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCodigo()).isEqualTo("EST001");
    }

    // ── actualizarEstacion ─────────────────────────────────────────────────────

    @Test
    void actualizarEstacion_debeRetornarResponseDtoActualizado_cuandoExiste() {
        when(estacionRepository.findById(1L)).thenReturn(Optional.of(estacion));
        doNothing().when(mapper).updateEntity(requestDto, estacion);
        when(estacionRepository.save(estacion)).thenReturn(estacion);
        when(mapper.toDto(estacion)).thenReturn(responseDto);

        EstacionResponseDto result = service.actualizarEstacion(1L, requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(mapper).updateEntity(requestDto, estacion);
    }

    @Test
    void actualizarEstacion_debeLanzarResourceNotFoundException_cuandoNoExiste() {
        when(estacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizarEstacion(99L, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(estacionRepository, never()).save(any());
    }

    @Test
    void actualizarEstacion_debeLanzarDuplicateResourceException_cuandoNuevoCodigoYaExiste() {
        Estacion existente = Estacion.builder().id(1L).codigo("EST001").build();
        requestDto = EstacionRequestDto.builder()
                .codigo("EST002")
                .nombre("Estación Central")
                .direccion("Calle Principal 123")
                .ciudad("Bogotá")
                .latitud(new BigDecimal("4.60970000"))
                .longitud(new BigDecimal("-74.08170000"))
                .estado(EstadoEstacion.ACTIVA)
                .build();

        when(estacionRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(estacionRepository.existsByCodigo("EST002")).thenReturn(true);

        assertThatThrownBy(() -> service.actualizarEstacion(1L, requestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("EST002");
    }

    // ── eliminarEstacion ───────────────────────────────────────────────────────

    @Test
    void eliminarEstacion_debeEliminar_cuandoExiste() {
        when(estacionRepository.findById(1L)).thenReturn(Optional.of(estacion));
        doNothing().when(estacionRepository).delete(estacion);

        service.eliminarEstacion(1L);

        verify(estacionRepository).delete(estacion);
    }

    @Test
    void eliminarEstacion_debeLanzarResourceNotFoundException_cuandoNoExiste() {
        when(estacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminarEstacion(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(estacionRepository, never()).delete(any());
    }
}
