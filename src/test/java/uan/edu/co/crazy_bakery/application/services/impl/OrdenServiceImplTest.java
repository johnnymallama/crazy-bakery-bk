package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uan.edu.co.crazy_bakery.application.dto.requests.AgregarNotaOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.NotaDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.OrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.UsuarioDTO;
import uan.edu.co.crazy_bakery.application.mappers.OrdenMapper;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;
import uan.edu.co.crazy_bakery.domain.model.*;
import uan.edu.co.crazy_bakery.infrastructure.repositories.NotaRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.OrdenRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.RecetaRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.UsuarioRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenServiceImplTest {

    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private NotaRepository notaRepository;

    @Mock
    private OrdenMapper ordenMapper;

    private OrdenServiceImpl ordenService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;

    private static final int HISTORY_MONTH_COUNT = 3;

    @BeforeEach
    void setUp() {
        ordenService = new OrdenServiceImpl(ordenRepository, usuarioRepository, recetaRepository, notaRepository, ordenMapper, 10, HISTORY_MONTH_COUNT);

        usuario = new Usuario();
        usuario.setId("user123");
        usuario.setNombre("Test User");

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId("user123");
        usuarioDTO.setNombre("Test User");
    }

    private OrdenDTO buildOrdenDTO(Long id, EstadoOrden estado) {
        return OrdenDTO.builder()
                .id(id)
                .estado(estado)
                .usuario(usuarioDTO)
                .notas(new ArrayList<>())
                .build();
    }

    private Orden buildOrden(Long id, EstadoOrden estado) {
        Orden orden = new Orden();
        orden.setId(id);
        orden.setEstado(estado);
        orden.setUsuario(usuario);
        orden.setNotas(new ArrayList<>());
        orden.setRecetas(new ArrayList<>());
        return orden;
    }

    @Test
    void createOrden_ShouldReturnOrdenDTO() {
        CrearOrdenDTO crearOrdenDTO = new CrearOrdenDTO("user123", List.of(1L), null);

        Torta torta = new Torta();
        torta.setValor(10.0f);
        Receta receta = new Receta();
        receta.setTorta(torta);
        receta.setCostoManoObra(10.0f);
        receta.setCostoOperativo(20.0f);
        receta.setCantidad(1);

        Orden orden = buildOrden(1L, EstadoOrden.CREADO);
        OrdenDTO ordenDTO = buildOrdenDTO(1L, EstadoOrden.CREADO);
        ordenDTO.setValorTotal(44.0f);

        when(usuarioRepository.findById("user123")).thenReturn(Optional.of(usuario));
        when(recetaRepository.findAllById(List.of(1L))).thenReturn(List.of(receta));
        when(ordenMapper.toEntity(crearOrdenDTO)).thenReturn(orden);
        when(ordenRepository.save(any(Orden.class))).thenReturn(orden);
        when(ordenMapper.toDto(orden)).thenReturn(ordenDTO);

        OrdenDTO result = ordenService.createOrden(crearOrdenDTO);

        assertThat(result).isNotNull();
        assertThat(result.getValorTotal()).isEqualTo(44.0f);
        assertThat(result.getUsuario().getId()).isEqualTo("user123");
    }

    @Test
    void createOrden_ShouldThrowExceptionCuandoUsuarioNoExiste() {
        CrearOrdenDTO crearOrdenDTO = new CrearOrdenDTO("inexistente", List.of(1L), null);
        when(usuarioRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordenService.createOrden(crearOrdenDTO))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void createOrden_ShouldThrowExceptionCuandoRecetasNoCoinciden() {
        CrearOrdenDTO crearOrdenDTO = new CrearOrdenDTO("user123", List.of(1L, 2L), null);

        when(usuarioRepository.findById("user123")).thenReturn(Optional.of(usuario));
        when(recetaRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(new Receta()));

        assertThatThrownBy(() -> ordenService.createOrden(crearOrdenDTO))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getAllOrdenes_ShouldReturnListDeOrdenes() {
        Orden orden = buildOrden(1L, EstadoOrden.CREADO);
        OrdenDTO ordenDTO = buildOrdenDTO(1L, EstadoOrden.CREADO);

        when(ordenRepository.findByFechaAfterOrderByFechaDesc(any(Date.class))).thenReturn(List.of(orden));
        when(ordenMapper.toDto(any(Orden.class))).thenReturn(ordenDTO);

        List<OrdenDTO> result = ordenService.getAllOrdenes();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getEstado()).isEqualTo(EstadoOrden.CREADO);
        verify(ordenRepository, times(1)).findByFechaAfterOrderByFechaDesc(any(Date.class));
    }

    @Test
    void getOrdenById_ShouldReturnOrdenCuandoExiste() {
        Orden orden = buildOrden(1L, EstadoOrden.CREADO);
        OrdenDTO ordenDTO = buildOrdenDTO(1L, EstadoOrden.CREADO);

        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenMapper.toDto(orden)).thenReturn(ordenDTO);

        OrdenDTO result = ordenService.getOrdenById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getOrdenById_ShouldThrowExceptionCuandoNoExiste() {
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordenService.getOrdenById(99L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getOrdenesByUsuario_ShouldReturnListDeOrdenes() {
        Orden orden = buildOrden(1L, EstadoOrden.CREADO);
        OrdenDTO ordenDTO = buildOrdenDTO(1L, EstadoOrden.CREADO);

        when(ordenRepository.findByUsuarioIdAndFechaAfterOrderByFechaDesc(eq("user123"), any(Date.class))).thenReturn(List.of(orden));
        when(ordenMapper.toDto(any(Orden.class))).thenReturn(ordenDTO);

        List<OrdenDTO> result = ordenService.getOrdenesByUsuario("user123");

        assertThat(result).isNotEmpty().hasSize(1);
        assertThat(result.get(0).getUsuario().getId()).isEqualTo("user123");
        verify(ordenRepository).findByUsuarioIdAndFechaAfterOrderByFechaDesc(eq("user123"), any(Date.class));
    }

    @Test
    void getOrdenesByEstado_ShouldReturnListFiltradaPorEstado() {
        Orden orden = buildOrden(1L, EstadoOrden.CREADO);
        OrdenDTO ordenDTO = buildOrdenDTO(1L, EstadoOrden.CREADO);

        when(ordenRepository.findByEstado(EstadoOrden.CREADO)).thenReturn(List.of(orden));
        when(ordenMapper.toDto(any(Orden.class))).thenReturn(ordenDTO);

        List<OrdenDTO> result = ordenService.getOrdenesByEstado(EstadoOrden.CREADO);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getUsuario().getId()).isEqualTo("user123");
    }

    @Test
    void getOrdenesByFecha_ShouldReturnListFiltradaPorRango() {
        Orden orden = buildOrden(1L, EstadoOrden.CREADO);
        OrdenDTO ordenDTO = buildOrdenDTO(1L, EstadoOrden.CREADO);

        when(ordenRepository.findByFechaGreaterThanEqualAndFechaLessThan(any(Date.class), any(Date.class))).thenReturn(List.of(orden));
        when(ordenMapper.toDto(any(Orden.class))).thenReturn(ordenDTO);

        List<OrdenDTO> result = ordenService.getOrdenesByFecha(new Date(), new Date());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getUsuario().getId()).isEqualTo("user123");
    }

    @Test
    void cambiarEstadoOrden_ShouldActualizarEstado() {
        Orden orden = buildOrden(1L, EstadoOrden.CREADO);
        OrdenDTO ordenDTO = buildOrdenDTO(1L, EstadoOrden.EN_PROCESO);

        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ordenMapper.toDto(any(Orden.class))).thenReturn(ordenDTO);

        OrdenDTO result = ordenService.cambiarEstadoOrden(1L, EstadoOrden.EN_PROCESO);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo(EstadoOrden.EN_PROCESO);
        assertThat(result.getUsuario().getId()).isEqualTo("user123");
        verify(ordenRepository).save(orden);
    }

    @Test
    void cambiarEstadoOrden_ShouldThrowExceptionCuandoNoExiste() {
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordenService.cambiarEstadoOrden(99L, EstadoOrden.EN_PROCESO))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void agregarNotaOrden_ShouldAgregarNotaCorrectamente() {
        Long ordenId = 1L;
        AgregarNotaOrdenDTO dto = new AgregarNotaOrdenDTO();
        dto.setNota("Sin gluten");
        dto.setUsuarioId("user123");

        Orden ordenInicial = buildOrden(ordenId, EstadoOrden.CREADO);
        Nota nuevaNota = new Nota();
        nuevaNota.setId(1L);
        nuevaNota.setNota("Sin gluten");
        nuevaNota.setUsuario(usuario);

        Orden ordenActualizada = buildOrden(ordenId, EstadoOrden.CREADO);
        ordenActualizada.getNotas().add(nuevaNota);

        NotaDTO notaDTO = NotaDTO.builder().build();
        notaDTO.setNota("Sin gluten");
        notaDTO.setUsuarioNombre(usuario.getNombre());

        OrdenDTO ordenDTOFinal = buildOrdenDTO(ordenId, EstadoOrden.CREADO);
        ordenDTOFinal.getNotas().add(notaDTO);

        when(ordenRepository.findById(ordenId))
                .thenReturn(Optional.of(ordenInicial))
                .thenReturn(Optional.of(ordenActualizada));
        when(usuarioRepository.findById("user123")).thenReturn(Optional.of(usuario));
        when(notaRepository.save(any(Nota.class))).thenReturn(nuevaNota);
        when(ordenMapper.toDto(ordenActualizada)).thenReturn(ordenDTOFinal);

        OrdenDTO result = ordenService.agregarNotaOrden(ordenId, dto);

        assertThat(result).isNotNull();
        assertThat(result.getNotas()).hasSize(1);
        assertThat(result.getNotas().get(0).getNota()).isEqualTo("Sin gluten");
        verify(notaRepository).save(any(Nota.class));
        verify(ordenRepository, times(2)).findById(ordenId);
    }

    @Test
    void agregarNotaOrden_ShouldThrowExceptionCuandoOrdenNoExiste() {
        AgregarNotaOrdenDTO dto = new AgregarNotaOrdenDTO();
        dto.setNota("Nota");
        dto.setUsuarioId("user123");

        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordenService.agregarNotaOrden(99L, dto))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void agregarNotaOrden_ShouldThrowExceptionCuandoUsuarioNoExiste() {
        Long ordenId = 1L;
        AgregarNotaOrdenDTO dto = new AgregarNotaOrdenDTO();
        dto.setNota("Nota");
        dto.setUsuarioId("inexistente");

        Orden orden = buildOrden(ordenId, EstadoOrden.CREADO);
        when(ordenRepository.findById(ordenId)).thenReturn(Optional.of(orden));
        when(usuarioRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordenService.agregarNotaOrden(ordenId, dto))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void agregarRecetaOrden_ShouldAgregarRecetaYActualizarValor() {
        Long ordenId = 1L;
        Long recetaId = 2L;

        Orden orden = buildOrden(ordenId, EstadoOrden.CREADO);
        orden.setValorTotal(50.0f);

        Torta torta = new Torta();
        torta.setValor(10.0f);
        Receta receta = new Receta();
        receta.setId(recetaId);
        receta.setCostoManoObra(10.0f);
        receta.setCostoOperativo(20.0f);
        receta.setCantidad(1);
        receta.setTorta(torta);

        OrdenDTO ordenDTO = buildOrdenDTO(ordenId, EstadoOrden.CREADO);
        ordenDTO.setValorTotal(44.0f);

        when(ordenRepository.findById(ordenId)).thenReturn(Optional.of(orden));
        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(receta));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ordenMapper.toDto(any(Orden.class))).thenReturn(ordenDTO);

        OrdenDTO result = ordenService.agregarRecetaOrden(ordenId, recetaId);

        assertThat(result).isNotNull();
        assertThat(result.getValorTotal()).isEqualTo(44.0f);
        assertThat(result.getUsuario().getId()).isEqualTo("user123");
        verify(ordenRepository).save(orden);
    }

    @Test
    void agregarRecetaOrden_ShouldThrowExceptionCuandoOrdenNoExiste() {
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordenService.agregarRecetaOrden(99L, 1L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void agregarRecetaOrden_ShouldThrowExceptionCuandoRecetaNoExiste() {
        Orden orden = buildOrden(1L, EstadoOrden.CREADO);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(recetaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordenService.agregarRecetaOrden(1L, 99L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void agregarRecetaOrden_ShouldInicializarListaCuandoRecetasEsNull() {
        Long ordenId = 1L;
        Long recetaId = 2L;

        Orden ordenConRecetasNull = new Orden();
        ordenConRecetasNull.setId(ordenId);
        ordenConRecetasNull.setEstado(EstadoOrden.CREADO);
        ordenConRecetasNull.setUsuario(usuario);
        ordenConRecetasNull.setNotas(new ArrayList<>());

        Torta torta = new Torta();
        torta.setValor(10.0f);
        Receta receta = new Receta();
        receta.setId(recetaId);
        receta.setCostoManoObra(10.0f);
        receta.setCostoOperativo(20.0f);
        receta.setCantidad(1);
        receta.setTorta(torta);

        OrdenDTO ordenDTO = buildOrdenDTO(ordenId, EstadoOrden.CREADO);

        when(ordenRepository.findById(ordenId)).thenReturn(Optional.of(ordenConRecetasNull));
        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(receta));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ordenMapper.toDto(any(Orden.class))).thenReturn(ordenDTO);

        OrdenDTO result = ordenService.agregarRecetaOrden(ordenId, recetaId);

        assertThat(result).isNotNull();
        verify(ordenRepository).save(ordenConRecetasNull);
    }
}
