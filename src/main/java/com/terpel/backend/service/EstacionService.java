package com.terpel.backend.service;

import java.util.List;

import com.terpel.backend.model.dto.EstacionRequestDto;
import com.terpel.backend.model.dto.EstacionResponseDto;

public interface EstacionService {

    /*
     * Crea una nueva estación.
     * 
     * @param estacion La estación a crear.
     * 
     * @return La estación creada.
     */
    EstacionResponseDto crearEstacion(EstacionRequestDto estacion);

    /*
     * Obtiene una estación por su ID.
     * 
     * @param id El ID de la estación.
     * 
     * @return La estación encontrada.
     */

    EstacionResponseDto obtenerEstacionPorId(Long id);

    /*
     * Obtiene todas las estaciones.
     * 
     * @return Una lista de todas las estaciones.
     */
    List<EstacionResponseDto> obtenerTodasLasEstaciones();

    /*
     * Actualiza una estación existente.
     * 
     * @param id El ID de la estación a actualizar.
     * 
     * @param estacion La estación con los datos actualizados.
     * 
     * @return La estación actualizada.
     */
    EstacionResponseDto actualizarEstacion(Long id, EstacionRequestDto estacion);

    /*
     * Elimina una estación.
     * 
     * @param id El ID de la estación a eliminar.
     */
    void eliminarEstacion(Long id);
}
