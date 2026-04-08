package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.domain.model.IngredienteTamano;
import uan.edu.co.crazy_bakery.domain.model.Tamano;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteTamanoRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TamanoRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngredienteTamanoServiceImplTest {

    @Mock
    private IngredienteTamanoRepository ingredienteTamanoRepository;

    @Mock
    private TamanoRepository tamanoRepository;

    @InjectMocks
    private IngredienteTamanoServiceImpl ingredienteTamanoService;

    @Test
    void consultarPorTamano_ShouldReturnListDeRelaciones() {
        Long tamanoId = 1L;
        IngredienteTamano ingredienteTamano = new IngredienteTamano();
        ingredienteTamano.setId(1L);

        when(ingredienteTamanoRepository.findByTamanoIdAndEstadoTrue(tamanoId))
                .thenReturn(Collections.singletonList(ingredienteTamano));

        List<IngredienteTamanoDTO> result = ingredienteTamanoService.consultarPorTamano(tamanoId);

        assertThat(result).isNotNull().hasSize(1);
    }

    @Test
    void crearRelacion_ShouldReturnIngredienteTamanoDTO() {
        CrearIngredienteTamanoDTO crearDto = new CrearIngredienteTamanoDTO();
        crearDto.setTamanoId(1L);
        crearDto.setTipoIngrediente(TipoIngrediente.BIZCOCHO);
        crearDto.setGramos(100.0f);

        Tamano tamano = new Tamano();
        tamano.setId(1L);

        IngredienteTamano saved = new IngredienteTamano();
        saved.setId(1L);
        saved.setTamano(tamano);
        saved.setTipoIngrediente(TipoIngrediente.BIZCOCHO);

        when(tamanoRepository.findByIdAndEstadoTrue(1L)).thenReturn(Optional.of(tamano));
        when(ingredienteTamanoRepository.save(any(IngredienteTamano.class))).thenReturn(saved);

        IngredienteTamanoDTO result = ingredienteTamanoService.crearRelacion(crearDto);

        assertThat(result).isNotNull();
    }

    @Test
    void crearRelacion_ShouldThrowExceptionCuandoTamanoNoExiste() {
        CrearIngredienteTamanoDTO crearDto = new CrearIngredienteTamanoDTO();
        crearDto.setTamanoId(99L);

        when(tamanoRepository.findByIdAndEstadoTrue(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredienteTamanoService.crearRelacion(crearDto))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void inactivarRelacion_ShouldReturnTrueCuandoExiste() {
        IngredienteTamano ingredienteTamano = new IngredienteTamano();
        ingredienteTamano.setEstado(true);

        when(ingredienteTamanoRepository.findById(1L)).thenReturn(Optional.of(ingredienteTamano));
        when(ingredienteTamanoRepository.save(any(IngredienteTamano.class))).thenReturn(ingredienteTamano);

        boolean result = ingredienteTamanoService.inactivarRelacion(1L);

        assertThat(result).isTrue();
        assertThat(ingredienteTamano.isEstado()).isFalse();
    }

    @Test
    void inactivarRelacion_ShouldReturnFalseCuandoNoExiste() {
        when(ingredienteTamanoRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = ingredienteTamanoService.inactivarRelacion(1L);

        assertThat(result).isFalse();
    }
}
