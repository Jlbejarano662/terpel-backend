package com.terpel.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.terpel.backend.exception.DuplicateResourceException;
import com.terpel.backend.exception.ResourceNotFoundException;
import com.terpel.backend.model.dto.EstacionRequestDto;
import com.terpel.backend.model.dto.EstacionResponseDto;
import com.terpel.backend.model.entity.EstadoEstacion;
import com.terpel.backend.repository.EstacionRepository;
import com.terpel.backend.service.EstacionService;

@SpringBootTest
@Transactional
class EstacionIntegrationTest {

    @Autowired
    private EstacionService estacionService;

    @Autowired
    private EstacionRepository estacionRepository;

    private EstacionRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = EstacionRequestDto.builder()
                .codigo("INT001")
                .nombre("Estación Integración")
                .direccion("Av. Test 456")
                .ciudad("Medellín")
                .latitud(new BigDecimal("6.25184000"))
                .longitud(new BigDecimal("-75.56359000"))
                .estado(EstadoEstacion.ACTIVA)
                .build();
    }

    @Test
    void crearEstacion_persisteEnBaseDeDatos() {
        EstacionResponseDto result = estacionService.crearEstacion(requestDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getCodigo()).isEqualTo("INT001");
        assertThat(estacionRepository.existsByCodigo("INT001")).isTrue();
    }

    @Test
    void obtenerEstacionPorId_retornaEstacionPersistida() {
        EstacionResponseDto created = estacionService.crearEstacion(requestDto);

        EstacionResponseDto result = estacionService.obtenerEstacionPorId(created.getId());

        assertThat(result.getId()).isEqualTo(created.getId());
        assertThat(result.getNombre()).isEqualTo("Estación Integración");
    }

    @Test
    void obtenerTodasLasEstaciones_retornaListaConRegistros() {
        EstacionRequestDto segunda = EstacionRequestDto.builder()
                .codigo("INT002")
                .nombre("Estación Integración 2")
                .direccion("Av. Test 456")
                .ciudad("Medellín")
                .latitud(new BigDecimal("6.25184000"))
                .longitud(new BigDecimal("-75.56359000"))
                .estado(EstadoEstacion.ACTIVA)
                .build();
        estacionService.crearEstacion(requestDto);
        estacionService.crearEstacion(segunda);

        List<EstacionResponseDto> result = estacionService.obtenerTodasLasEstaciones();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void actualizarEstacion_modificaDatosEnBaseDeDatos() {
        EstacionResponseDto created = estacionService.crearEstacion(requestDto);
        EstacionRequestDto updateDto = EstacionRequestDto.builder()
                .codigo("INT001")
                .nombre("Estación Actualizada")
                .direccion("Av. Test 456")
                .ciudad("Medellín")
                .latitud(new BigDecimal("6.25184000"))
                .longitud(new BigDecimal("-75.56359000"))
                .estado(EstadoEstacion.INACTIVA)
                .build();

        EstacionResponseDto result = estacionService.actualizarEstacion(created.getId(), updateDto);

        assertThat(result.getNombre()).isEqualTo("Estación Actualizada");
        assertThat(result.getEstado()).isEqualTo(EstadoEstacion.INACTIVA);
    }

    @Test
    void eliminarEstacion_eliminaRegistroDeBaseDeDatos() {
        EstacionResponseDto created = estacionService.crearEstacion(requestDto);
        Long id = created.getId();

        estacionService.eliminarEstacion(id);

        assertThatThrownBy(() -> estacionService.obtenerEstacionPorId(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void crearEstacion_lanzaExcepcion_cuandoCodigoDuplicado() {
        estacionService.crearEstacion(requestDto);

        assertThatThrownBy(() -> estacionService.crearEstacion(requestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("INT001");
    }
}
