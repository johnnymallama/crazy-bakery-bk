package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uan.edu.co.crazy_bakery.application.dto.torta.CrearTortaDTO;
import uan.edu.co.crazy_bakery.application.dto.torta.TortaDTO;
import uan.edu.co.crazy_bakery.application.mappers.TortaMapper;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;
import uan.edu.co.crazy_bakery.domain.model.IngredienteTamano;
import uan.edu.co.crazy_bakery.domain.model.Tamano;
import uan.edu.co.crazy_bakery.domain.model.Torta;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteTamanoRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TamanoRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TortaRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TortaServiceImplTest {

    @Mock
    private TortaRepository tortaRepository;

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Mock
    private TamanoRepository tamanoRepository;

    @Mock
    private IngredienteTamanoRepository ingredienteTamanoRepository;

    @Mock
    private TortaMapper tortaMapper;

    @InjectMocks
    private TortaServiceImpl tortaService;

    @Test
    void crearTorta_ShouldReturnTortaDTO() {
        CrearTortaDTO crearTortaDTO = new CrearTortaDTO();
        crearTortaDTO.setBizcochoId(1L);
        crearTortaDTO.setRellenoId(2L);
        crearTortaDTO.setCuberturaId(3L);
        crearTortaDTO.setTamanoId(4L);

        Ingrediente bizcocho = new Ingrediente();
        bizcocho.setId(1L);
        bizcocho.setCostoPorGramo(10.0f);
        Ingrediente relleno = new Ingrediente();
        relleno.setId(2L);
        relleno.setCostoPorGramo(5.0f);
        Ingrediente cubertura = new Ingrediente();
        cubertura.setId(3L);
        cubertura.setCostoPorGramo(8.0f);

        Tamano tamano = new Tamano();
        tamano.setId(4L);

        IngredienteTamano itBizcocho = new IngredienteTamano();
        itBizcocho.setGramos(100);
        IngredienteTamano itRelleno = new IngredienteTamano();
        itRelleno.setGramos(50);
        IngredienteTamano itCubertura = new IngredienteTamano();
        itCubertura.setGramos(80);

        Torta torta = new Torta();
        torta.setId(1L);
        TortaDTO tortaDTO = new TortaDTO();
        tortaDTO.setId(1L);

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(bizcocho));
        when(ingredienteRepository.findById(2L)).thenReturn(Optional.of(relleno));
        when(ingredienteRepository.findById(3L)).thenReturn(Optional.of(cubertura));
        when(tamanoRepository.findById(4L)).thenReturn(Optional.of(tamano));
        when(ingredienteTamanoRepository.findByTamanoAndTipoIngredienteAndEstado(tamano, TipoIngrediente.BIZCOCHO, true)).thenReturn(Optional.of(itBizcocho));
        when(ingredienteTamanoRepository.findByTamanoAndTipoIngredienteAndEstado(tamano, TipoIngrediente.RELLENO, true)).thenReturn(Optional.of(itRelleno));
        when(ingredienteTamanoRepository.findByTamanoAndTipoIngredienteAndEstado(tamano, TipoIngrediente.COBERTURA, true)).thenReturn(Optional.of(itCubertura));
        when(tortaRepository.save(any(Torta.class))).thenReturn(torta);
        when(tortaMapper.toDTO(torta)).thenReturn(tortaDTO);

        TortaDTO result = tortaService.crearTorta(crearTortaDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void crearTorta_ShouldThrowExceptionCuandoBizcochoNoExiste() {
        CrearTortaDTO crearTortaDTO = new CrearTortaDTO();
        crearTortaDTO.setBizcochoId(1L);

        when(ingredienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tortaService.crearTorta(crearTortaDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bizcocho no encontrado");
    }

    @Test
    void crearTorta_ShouldThrowExceptionCuandoRellenoNoExiste() {
        CrearTortaDTO crearTortaDTO = new CrearTortaDTO();
        crearTortaDTO.setBizcochoId(1L);
        crearTortaDTO.setRellenoId(2L);

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(new Ingrediente()));
        when(ingredienteRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tortaService.crearTorta(crearTortaDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Relleno no encontrado");
    }

    @Test
    void crearTorta_ShouldThrowExceptionCuandoCuberturaNoExiste() {
        CrearTortaDTO crearTortaDTO = new CrearTortaDTO();
        crearTortaDTO.setBizcochoId(1L);
        crearTortaDTO.setRellenoId(2L);
        crearTortaDTO.setCuberturaId(3L);

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(new Ingrediente()));
        when(ingredienteRepository.findById(2L)).thenReturn(Optional.of(new Ingrediente()));
        when(ingredienteRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tortaService.crearTorta(crearTortaDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cubertura no encontrada");
    }

    @Test
    void crearTorta_ShouldThrowExceptionCuandoTamanoNoExiste() {
        CrearTortaDTO crearTortaDTO = new CrearTortaDTO();
        crearTortaDTO.setBizcochoId(1L);
        crearTortaDTO.setRellenoId(2L);
        crearTortaDTO.setCuberturaId(3L);
        crearTortaDTO.setTamanoId(4L);

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(new Ingrediente()));
        when(ingredienteRepository.findById(2L)).thenReturn(Optional.of(new Ingrediente()));
        when(ingredienteRepository.findById(3L)).thenReturn(Optional.of(new Ingrediente()));
        when(tamanoRepository.findById(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tortaService.crearTorta(crearTortaDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Tamaño no encontrado");
    }

    @Test
    void crearTorta_ShouldThrowExceptionCuandoRelacionIngredienteTamanoNoExiste() {
        CrearTortaDTO crearTortaDTO = new CrearTortaDTO();
        crearTortaDTO.setBizcochoId(1L);
        crearTortaDTO.setRellenoId(2L);
        crearTortaDTO.setCuberturaId(3L);
        crearTortaDTO.setTamanoId(4L);

        Ingrediente bizcocho = new Ingrediente();
        bizcocho.setId(1L);
        bizcocho.setCostoPorGramo(10.0f);
        Tamano tamano = new Tamano();
        tamano.setId(4L);

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(bizcocho));
        when(ingredienteRepository.findById(2L)).thenReturn(Optional.of(new Ingrediente()));
        when(ingredienteRepository.findById(3L)).thenReturn(Optional.of(new Ingrediente()));
        when(tamanoRepository.findById(4L)).thenReturn(Optional.of(tamano));
        when(ingredienteTamanoRepository.findByTamanoAndTipoIngredienteAndEstado(tamano, TipoIngrediente.BIZCOCHO, true)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tortaService.crearTorta(crearTortaDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No se encontró la relación ingrediente-tamaño para BIZCOCHO");
    }

    @Test
    void obtenerTodasLasTortas_ShouldReturnListDeTortaDTO() {
        Torta torta = new Torta();
        torta.setId(1L);
        TortaDTO tortaDTO = new TortaDTO();
        tortaDTO.setId(1L);

        when(tortaRepository.findAll()).thenReturn(Collections.singletonList(torta));
        when(tortaMapper.toDTO(torta)).thenReturn(tortaDTO);

        List<TortaDTO> result = tortaService.obtenerTodasLasTortas();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }
}
