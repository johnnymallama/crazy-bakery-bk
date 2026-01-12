package uan.edu.co.crazy_bakery.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.mappers.IngredienteMapper;
import uan.edu.co.crazy_bakery.application.services.impl.IngredienteServiceImpl;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredienteServiceTest {

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Mock
    private IngredienteMapper ingredienteMapper;

    @InjectMocks
    private IngredienteServiceImpl ingredienteService;

    private CrearIngredienteDTO crearIngredienteDTO;
    private Ingrediente ingredienteSinGuardar;
    private Ingrediente ingredienteGuardado;
    private IngredienteDTO ingredienteDTO;

    @BeforeEach
    void setUp() {
        crearIngredienteDTO = new CrearIngredienteDTO();
        crearIngredienteDTO.setNombre("Levadura");
        crearIngredienteDTO.setComposicion("Hongos");
        crearIngredienteDTO.setTipoIngrediente(TipoIngrediente.BIZCOCHO);
        crearIngredienteDTO.setValor(500);

        ingredienteSinGuardar = new Ingrediente();
        ingredienteSinGuardar.setNombre("Levadura");
        ingredienteSinGuardar.setComposicion("Hongos");
        ingredienteSinGuardar.setTipoIngrediente(TipoIngrediente.BIZCOCHO);
        ingredienteSinGuardar.setValor(500);

        ingredienteGuardado = new Ingrediente();
        ingredienteGuardado.setId(1L);
        ingredienteGuardado.setNombre("Levadura");
        ingredienteGuardado.setComposicion("Hongos");
        ingredienteGuardado.setTipoIngrediente(TipoIngrediente.BIZCOCHO);
        ingredienteGuardado.setValor(500);
        ingredienteGuardado.setEstado(true);

        ingredienteDTO = new IngredienteDTO();
        ingredienteDTO.setId(1L);
        ingredienteDTO.setNombre("Levadura");
        ingredienteDTO.setComposicion("Hongos");
        ingredienteDTO.setTipoIngrediente(TipoIngrediente.BIZCOCHO);
        ingredienteDTO.setValor(500);
    }

    @Test
    void createIngrediente_ShouldReturnIngredienteDTO() {
        // Arrange
        when(ingredienteMapper.crearIngredienteDTOToIngrediente(crearIngredienteDTO)).thenReturn(ingredienteSinGuardar);
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingredienteGuardado);
        when(ingredienteMapper.ingredienteToIngredienteDTO(ingredienteGuardado)).thenReturn(ingredienteDTO);

        // Act
        IngredienteDTO result = ingredienteService.createIngrediente(crearIngredienteDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Levadura");
    }

    @Test
    void getIngrediente_ShouldReturnOptionalOfIngrediente() {
        // Arrange
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingredienteGuardado));

        // Act
        Optional<Ingrediente> result = ingredienteService.getIngrediente(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void getAllIngredientes_ShouldReturnListOfIngredienteDTO() {
        // Arrange
        when(ingredienteRepository.findAllByEstado(true)).thenReturn(Collections.singletonList(ingredienteGuardado));
        when(ingredienteMapper.ingredienteToIngredienteDTO(ingredienteGuardado)).thenReturn(ingredienteDTO);

        // Act
        List<IngredienteDTO> result = ingredienteService.getAllIngredientes();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void updateIngrediente_ShouldReturnOptionalOfIngredienteDTO() {
        // Arrange
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingredienteGuardado));
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingredienteGuardado);
        when(ingredienteMapper.ingredienteToIngredienteDTO(ingredienteGuardado)).thenReturn(ingredienteDTO);

        // Act
        Optional<IngredienteDTO> result = ingredienteService.updateIngrediente(1L, crearIngredienteDTO);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void updateIngrediente_ShouldReturnEmptyOptionalWhenNotFound() {
        // Arrange
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<IngredienteDTO> result = ingredienteService.updateIngrediente(1L, crearIngredienteDTO);

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    void deactivateIngrediente_ShouldReturnOptionalOfIngredienteDTO() {
        // Arrange
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingredienteGuardado));
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingredienteGuardado);
        when(ingredienteMapper.ingredienteToIngredienteDTO(ingredienteGuardado)).thenReturn(ingredienteDTO);

        // Act
        Optional<IngredienteDTO> result = ingredienteService.deactivateIngrediente(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(ingredienteRepository, times(1)).save(any(Ingrediente.class));
    }

    @Test
    void deactivateIngrediente_ShouldReturnEmptyOptionalWhenNotFound() {
        // Arrange
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<IngredienteDTO> result = ingredienteService.deactivateIngrediente(1L);

        // Assert
        assertThat(result).isNotPresent();
    }

    @Test
    void findByTipoIngrediente_ShouldReturnListOfIngredienteDTO() {
        // Arrange
        TipoIngrediente tipoIngrediente = TipoIngrediente.BIZCOCHO;
        when(ingredienteRepository.findByTipoIngrediente(tipoIngrediente)).thenReturn(Collections.singletonList(ingredienteGuardado));
        when(ingredienteMapper.ingredienteToIngredienteDTO(ingredienteGuardado)).thenReturn(ingredienteDTO);

        // Act
        List<IngredienteDTO> result = ingredienteService.findByTipoIngrediente(tipoIngrediente);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTipoIngrediente()).isEqualTo(tipoIngrediente);
    }
}
