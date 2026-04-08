package com.terpel.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.terpel.backend.model.dto.EstacionRequestDto;
import com.terpel.backend.model.dto.EstacionResponseDto;
import com.terpel.backend.model.entity.Estacion;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EstacionMapper {

    /** Convierte una entidad a un DTO. */
    EstacionResponseDto toDto(Estacion entity);

    /** Convierte un DTO a una entidad. */
    @Mapping(target = "id", ignore = true)
    Estacion toEntity(EstacionRequestDto dto);

    /** Actualiza una entidad existente con los datos del DTO, preservando el id. */
    @Mapping(target = "id", ignore = true)
    void updateEntity(EstacionRequestDto dto, @MappingTarget Estacion entity);

    /** Convierte una lista de entidades a lista de DTOs. */
    List<EstacionResponseDto> toDtoList(List<Estacion> entities);
}
