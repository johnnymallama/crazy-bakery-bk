package uan.edu.co.crazy_bakery.application.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uan.edu.co.crazy_bakery.application.dto.requests.AgregarNotaOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.OrdenDTO;
import uan.edu.co.crazy_bakery.application.mappers.OrdenMapper;
import uan.edu.co.crazy_bakery.application.services.OrdenService;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;
import uan.edu.co.crazy_bakery.domain.model.Nota;
import uan.edu.co.crazy_bakery.domain.model.Orden;
import uan.edu.co.crazy_bakery.domain.model.Receta;
import uan.edu.co.crazy_bakery.domain.model.Usuario;
import uan.edu.co.crazy_bakery.infrastructure.repositories.NotaRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.OrdenRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.RecetaRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.UsuarioRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static uan.edu.co.crazy_bakery.domain.enums.EstadoOrden.CREADO;

@Service
public class OrdenServiceImpl implements OrdenService {

    private final OrdenRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecetaRepository recetaRepository;
    private final NotaRepository notaRepository; // Inyectado
    private final OrdenMapper ordenMapper;
    private final int gananciaPorcentaje;

    public OrdenServiceImpl(OrdenRepository ordenRepository,
                            UsuarioRepository usuarioRepository,
                            RecetaRepository recetaRepository,
                            NotaRepository notaRepository, // Inyectado
                            OrdenMapper ordenMapper,
                            @Value("${cost.benefit.percentage}") int gananciaPorcentaje) {
        this.ordenRepository = ordenRepository;
        this.usuarioRepository = usuarioRepository;
        this.recetaRepository = recetaRepository;
        this.notaRepository = notaRepository; // Inyectado
        this.ordenMapper = ordenMapper;
        this.gananciaPorcentaje = gananciaPorcentaje;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenDTO> getAllOrdenes() {
        return ordenRepository.findAll().stream()
                .map(ordenMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenDTO getOrdenById(Long id) {
        return ordenRepository.findById(id)
                .map(ordenMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenDTO> getOrdenesByUsuario(String usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId).stream()
                .map(ordenMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenDTO> getOrdenesByEstado(EstadoOrden estado) {
        return ordenRepository.findByEstado(estado).stream()
                .map(ordenMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional
    public OrdenDTO createOrden(CrearOrdenDTO crearOrdenDTO) {
        Usuario usuario = usuarioRepository.findById(crearOrdenDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Receta> recetas = recetaRepository.findAllById(crearOrdenDTO.getRecetaIds());

        if (recetas.size() != crearOrdenDTO.getRecetaIds().size()) {
            throw new RuntimeException("Una o más recetas no fueron encontradas");
        }

        Orden orden = ordenMapper.toEntity(crearOrdenDTO);
        orden.setUsuario(usuario);
        orden.setRecetas(recetas);
        orden.setFecha(new Date());
        orden.setEstado(CREADO);
        recetas.forEach(receta -> this.calculatTotal(orden, receta));

        return ordenMapper.toDto(ordenRepository.save(orden));
    }

    @Override
    @Transactional
    public OrdenDTO cambiarEstadoOrden(Long id, EstadoOrden estado) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        orden.setEstado(estado);
        Orden ordenActualizada = ordenRepository.save(orden);

        return ordenMapper.toDto(ordenActualizada);
    }

    @Override
    @Transactional
    public OrdenDTO agregarNotaOrden(Long id, AgregarNotaOrdenDTO agregarNotaOrdenDTO) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        Usuario usuario = usuarioRepository.findById(agregarNotaOrdenDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Nota nuevaNota = new Nota();
        nuevaNota.setNota(agregarNotaOrdenDTO.getNota());
        nuevaNota.setUsuario(usuario);
        nuevaNota.setOrden(orden);

        // Guardamos la nota usando su repositorio
        notaRepository.save(nuevaNota);

        // Devolvemos la orden actualizada (la recargamos para asegurar que la lista de notas está al día)
        return ordenMapper.toDto(ordenRepository.findById(id).get());
    }

    @Override
    @Transactional
    public OrdenDTO agregarRecetaOrden(Long id, Long recetaId) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        if (orden.getRecetas() == null) {
            orden.setRecetas(new ArrayList<>());
        }

        orden.getRecetas().add(receta);
        this.calculatTotal(orden, receta);

        Orden ordenActualizada = ordenRepository.save(orden);

        return ordenMapper.toDto(ordenActualizada);
    }

    private void calculatTotal(Orden orden, Receta receta){
        float valorTorta = receta.getTorta().getValor();
        float totalReceta = receta.getCantidad() * (receta.getCostoManoObra() + receta.getCostoOperativo());
        float costoTotalProducto = totalReceta + valorTorta;

        float gananciaTotal = (costoTotalProducto * gananciaPorcentaje)/100;

        float valorTotal = costoTotalProducto + gananciaTotal;

        orden.setGanancia(gananciaTotal);

        orden.setValorTotal(valorTotal);
    }
}
