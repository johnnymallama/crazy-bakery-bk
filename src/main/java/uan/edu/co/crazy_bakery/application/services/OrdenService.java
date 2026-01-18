package uan.edu.co.crazy_bakery.application.services;

import uan.edu.co.crazy_bakery.application.dto.requests.CrearOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.OrdenDTO;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;

import java.util.Date;
import java.util.List;

public interface OrdenService {

    List<OrdenDTO> getAllOrdenes();

    OrdenDTO getOrdenById(Long id);

    List<OrdenDTO> getOrdenesByUsuario(String usuarioId);

    List<OrdenDTO> getOrdenesByEstado(EstadoOrden estado);

    List<OrdenDTO> getOrdenesByFecha(Date fechaInicio, Date fechaFin);

    OrdenDTO createOrden(CrearOrdenDTO crearOrdenDTO);

    OrdenDTO cambiarEstadoOrden(Long id, EstadoOrden estado);

    OrdenDTO agregarNotaOrden(Long id, String nota);

    OrdenDTO agregarRecetaOrden(Long id, Long recetaId);

}
