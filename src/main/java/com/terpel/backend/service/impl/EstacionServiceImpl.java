package com.terpel.backend.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.terpel.backend.exception.DuplicateResourceException;
import com.terpel.backend.exception.ResourceNotFoundException;
import com.terpel.backend.mapper.EstacionMapper;
import com.terpel.backend.model.dto.EstacionRequestDto;
import com.terpel.backend.model.dto.EstacionResponseDto;
import com.terpel.backend.model.entity.Estacion;
import com.terpel.backend.repository.EstacionRepository;
import com.terpel.backend.service.EstacionService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EstacionServiceImpl implements EstacionService {

    private static final String ESTACION_NO_ENCONTRADA = "Estación no encontrada con ID: ";
    private final EstacionRepository estacionRepository;
    private final EstacionMapper mapper;

    @Override
    @Transactional
    @CacheEvict(value = "estaciones", allEntries = true)
    public EstacionResponseDto crearEstacion(EstacionRequestDto estacion) {
        if (estacionRepository.existsByCodigo(estacion.getCodigo())) {
            throw new DuplicateResourceException("Ya existe una estación con el código: " + estacion.getCodigo());
        }
        Estacion estacionEntity = estacionRepository.save(mapper.toEntity(estacion));
        return mapper.toDto(estacionEntity);
    }

    @Override
    @Cacheable(value = "estacion", key = "#id")
    public EstacionResponseDto obtenerEstacionPorId(Long id) {
        Estacion estacion = estacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ESTACION_NO_ENCONTRADA + id));
        return mapper.toDto(estacion);
    }

    @Override
    @Cacheable("estaciones")
    public List<EstacionResponseDto> obtenerTodasLasEstaciones() {
        List<Estacion> estaciones = estacionRepository.findAll();
        return mapper.toDtoList(estaciones);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "estacion", key = "#id"),
        @CacheEvict(value = "estaciones", allEntries = true)
    })
    public EstacionResponseDto actualizarEstacion(Long id, EstacionRequestDto estacion) {
        Estacion estacionExistente = estacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ESTACION_NO_ENCONTRADA + id));

        if (!estacionExistente.getCodigo().equals(estacion.getCodigo())
                && estacionRepository.existsByCodigo(estacion.getCodigo())) {
            throw new DuplicateResourceException("Ya existe una estación con el código: " + estacion.getCodigo());
        }

        mapper.updateEntity(estacion, estacionExistente);

        Estacion updatedEstacion = estacionRepository.save(estacionExistente);
        return mapper.toDto(updatedEstacion);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "estacion", key = "#id"),
        @CacheEvict(value = "estaciones", allEntries = true)
    })
    public void eliminarEstacion(Long id) {
        Estacion estacionExistente = estacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ESTACION_NO_ENCONTRADA + id));
        estacionRepository.delete(estacionExistente);
    }

}
