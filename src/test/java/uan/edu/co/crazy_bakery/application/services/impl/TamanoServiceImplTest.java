package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.TamanoDTO;
import uan.edu.co.crazy_bakery.application.mappers.TamanoMapper;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;
import uan.edu.co.crazy_bakery.domain.model.Tamano;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TamanoRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TamanoServiceImplTest {

    @Mock
    private TamanoRepository tamanoRepository;

    @Mock
    private TamanoMapper tamanoMapper;

    @InjectMocks
    private TamanoServiceImpl tamanoService;

    private Tamano tamano;
    private TamanoDTO tamanoDTO;
    private CrearTamanoDTO crearTamanoDTO;
    private ActualizarTamanoDTO actualizarTamanoDTO;

    @BeforeEach
    void setUp() {
        crearTamanoDTO = new CrearTamanoDTO("Personal", 10, 15, 8, TipoReceta.TORTA, 1.5F);
        actualizarTamanoDTO = new ActualizarTamanoDTO(12, 18, 10, 1.5F);

        tamano = new Tamano();
        tamano.setId(1L);
        tamano.setNombre("Personal");
        tamano.setEstado(true);

        tamanoDTO = new TamanoDTO();
        tamanoDTO.setId(1L);
        tamanoDTO.setNombre("Personal");
    }

    @Test
    void crearTamano_ShouldReturnTamanoDTO() {
        when(tamanoMapper.crearTamanoDTOToTamano(any(CrearTamanoDTO.class))).thenReturn(tamano);
        when(tamanoRepository.save(any(Tamano.class))).thenReturn(tamano);
        when(tamanoMapper.tamanoToTamanoDTO(any(Tamano.class))).thenReturn(tamanoDTO);

        TamanoDTO result = tamanoService.crearTamano(crearTamanoDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(tamanoDTO.getId());
        verify(tamanoRepository, times(1)).save(any(Tamano.class));
    }

    @Test
    void obtenerTamanoPorId_ShouldReturnTamanoDTO() {
        when(tamanoRepository.findByIdAndEstadoTrue(1L)).thenReturn(Optional.of(tamano));
        when(tamanoMapper.tamanoToTamanoDTO(any(Tamano.class))).thenReturn(tamanoDTO);

        TamanoDTO result = tamanoService.obtenerTamanoPorId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(tamanoDTO.getId());
    }

    @Test
    void obtenerTamanoPorId_ShouldThrowExceptionCuandoNoExiste() {
        when(tamanoRepository.findByIdAndEstadoTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tamanoService.obtenerTamanoPorId(1L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void obtenerTodosLosTamanos_ShouldReturnListDeTamanoDTO() {
        when(tamanoRepository.findAllByEstadoTrue()).thenReturn(Collections.singletonList(tamano));
        when(tamanoMapper.tamanosToTamanoDTOs(anyList())).thenReturn(Collections.singletonList(tamanoDTO));

        List<TamanoDTO> result = tamanoService.obtenerTodosLosTamanos();

        assertThat(result).isNotNull().hasSize(1);
    }

    @Test
    void actualizarTamano_ShouldReturnTamanoActualizado() {
        when(tamanoRepository.findByIdAndEstadoTrue(1L)).thenReturn(Optional.of(tamano));
        when(tamanoRepository.save(any(Tamano.class))).thenReturn(tamano);
        when(tamanoMapper.tamanoToTamanoDTO(any(Tamano.class))).thenReturn(tamanoDTO);

        TamanoDTO result = tamanoService.actualizarTamano(1L, actualizarTamanoDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(tamanoDTO.getId());
        verify(tamanoRepository, times(1)).save(tamano);
    }

    @Test
    void actualizarTamano_ShouldThrowExceptionCuandoNoExiste() {
        when(tamanoRepository.findByIdAndEstadoTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tamanoService.actualizarTamano(1L, actualizarTamanoDTO))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void eliminarTamano_ShouldInactivarTamano() {
        when(tamanoRepository.findByIdAndEstadoTrue(1L)).thenReturn(Optional.of(tamano));

        tamanoService.eliminarTamano(1L);

        assertThat(tamano.isEstado()).isFalse();
        verify(tamanoRepository, times(1)).save(tamano);
    }

    @Test
    void eliminarTamano_ShouldThrowExceptionCuandoNoExiste() {
        when(tamanoRepository.findByIdAndEstadoTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tamanoService.eliminarTamano(1L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void obtenerTamanosPorTipoReceta_ShouldReturnListFiltrada() {
        when(tamanoRepository.findAllByTipoRecetaAndEstadoTrue(TipoReceta.TORTA)).thenReturn(Collections.singletonList(tamano));
        when(tamanoMapper.tamanosToTamanoDTOs(anyList())).thenReturn(Collections.singletonList(tamanoDTO));

        List<TamanoDTO> result = tamanoService.obtenerTamanosPorTipoReceta(TipoReceta.TORTA);

        assertThat(result).isNotNull().hasSize(1);
    }
}
