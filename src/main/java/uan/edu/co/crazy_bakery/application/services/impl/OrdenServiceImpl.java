package uan.edu.co.crazy_bakery.application.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.OrdenDTO;
import uan.edu.co.crazy_bakery.application.mappers.OrdenMapper;
import uan.edu.co.crazy_bakery.application.services.OrdenService;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;
import uan.edu.co.crazy_bakery.domain.model.Orden;
import uan.edu.co.crazy_bakery.domain.model.Receta;
import uan.edu.co.crazy_bakery.domain.model.Usuario;
import uan.edu.co.crazy_bakery.infrastructure.repositories.OrdenRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.RecetaRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.UsuarioRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrdenServiceImpl implements OrdenService {

    private final OrdenRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecetaRepository recetaRepository;
    private final OrdenMapper ordenMapper;

    @Override
    public List<OrdenDTO> getAllOrdenes() {
        return ordenRepository.findAll().stream()
                .map(ordenMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrdenDTO getOrdenById(Long id) {
        return ordenRepository.findById(id)
                .map(ordenMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }

    @Override
    public List<OrdenDTO> getOrdenesByUsuario(String usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId).stream()
                .map(ordenMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdenDTO> getOrdenesByEstado(EstadoOrden estado) {
        return ordenRepository.findByEstado(estado).stream()
                .map(ordenMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdenDTO> getOrdenesByFecha(Date fechaInicio, Date fechaFin) {
        Calendar c = Calendar.getInstance();
        c.setTime(fechaFin);
        c.add(Calendar.DATE, 1);
        Date fechaFinAjustada = c.getTime();

        return ordenRepository.findByFechaGreaterThanEqualAndFechaLessThan(fechaInicio, fechaFinAjustada).stream()
                .map(ordenMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrdenDTO createOrden(CrearOrdenDTO crearOrdenDTO) {
        Usuario usuario = usuarioRepository.findById(crearOrdenDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Receta> recetas = recetaRepository.findAllById(crearOrdenDTO.getRecetaIds());

        if (recetas.size() != crearOrdenDTO.getRecetaIds().size()) {
            throw new RuntimeException("Una o más recetas no fueron encontradas");
        }

        float valorTotal = (float) recetas.stream().mapToDouble(Receta::getCostoTotal).sum();

        Orden orden = ordenMapper.toEntity(crearOrdenDTO);
        orden.setUsuario(usuario);
        orden.setRecetas(recetas);
        orden.setFecha(new Date());
        orden.setValorTotal(valorTotal);
        orden.setEstado(uan.edu.co.crazy_bakery.domain.enums.EstadoOrden.CREADO);

        return ordenMapper.toDto(ordenRepository.save(orden));
    }

    @Override
    public OrdenDTO cambiarEstadoOrden(Long id, EstadoOrden estado) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        orden.setEstado(estado);
        Orden ordenActualizada = ordenRepository.save(orden);

        return ordenMapper.toDto(ordenActualizada);
    }

    @Override
    public OrdenDTO agregarNotaOrden(Long id, String nota) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if (orden.getNotas() == null) {
            orden.setNotas(new ArrayList<>());
        }

        orden.getNotas().add(nota);
        Orden ordenActualizada = ordenRepository.save(orden);

        return ordenMapper.toDto(ordenActualizada);
    }

    @Override
    public OrdenDTO agregarRecetaOrden(Long id, Long recetaId) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        if (orden.getRecetas() == null) {
            orden.setRecetas(new ArrayList<>());
        }

        orden.getRecetas().add(receta);
        float nuevoValorTotal = (float) orden.getRecetas().stream().mapToDouble(Receta::getCostoTotal).sum();
        orden.setValorTotal(nuevoValorTotal);

        Orden ordenActualizada = ordenRepository.save(orden);

        return ordenMapper.toDto(ordenActualizada);
    }
}
