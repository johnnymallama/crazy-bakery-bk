package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearRecetaDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.RecetaDTO;
import uan.edu.co.crazy_bakery.application.dto.torta.TortaDTO;
import uan.edu.co.crazy_bakery.application.mappers.RecetaMapper;
import uan.edu.co.crazy_bakery.application.services.storage.StorageService;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;
import uan.edu.co.crazy_bakery.domain.model.Receta;
import uan.edu.co.crazy_bakery.domain.model.Tamano;
import uan.edu.co.crazy_bakery.domain.model.Torta;
import uan.edu.co.crazy_bakery.infrastructure.repositories.RecetaRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TortaRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaServiceImplTest {

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private TortaRepository tortaRepository;

    @Mock
    private RecetaMapper recetaMapper;

    @Mock
    private StorageService storageService;

    private RecetaServiceImpl recetaService;

    @BeforeEach
    void setUp() {
        recetaService = new RecetaServiceImpl(recetaRepository, tortaRepository, recetaMapper, storageService, 10, 10);
    }

    @Test
    void getUltimasImagenes_ShouldReturnListDeUrls() {
        List<String> expectedUrls = Arrays.asList(
            "http://example.com/img1.jpg",
            "http://example.com/img2.jpg"
        );
        when(recetaRepository.findUltimasImagenes()).thenReturn(expectedUrls);

        List<String> result = recetaService.getUltimasImagenes();

        assertThat(result).isNotNull().hasSize(2).isEqualTo(expectedUrls);
        verify(recetaRepository, times(1)).findUltimasImagenes();
    }

    @Test
    void crearReceta_ShouldReturnRecetaDTOConUrlDeFirebase() throws IOException {
        long tortaId = 1L;
        String originalUrl = "http://example.com/cake.png";
        String firebaseUrl = "http://firebase-storage.com/receta-123.jpg";

        CrearRecetaDTO crearRecetaDTO = new CrearRecetaDTO();
        crearRecetaDTO.setTortaId(tortaId);
        crearRecetaDTO.setCantidad(2);
        crearRecetaDTO.setTipoReceta(TipoReceta.TORTA);
        crearRecetaDTO.setPrompt("a delicious chocolate cake");
        crearRecetaDTO.setImagenUrl(originalUrl);

        Tamano tamano = new Tamano();
        tamano.setTiempo(1.5f);
        Torta torta = new Torta();
        torta.setId(tortaId);
        torta.setValor(50.0f);
        torta.setTamano(tamano);

        Receta receta = new Receta();
        receta.setTorta(torta);
        receta.setCantidad(2);
        receta.setTipoReceta(TipoReceta.TORTA);
        receta.setPrompt("a delicious chocolate cake");
        receta.setImagenUrl(originalUrl);

        Receta recetaGuardada = new Receta();
        recetaGuardada.setId(1L);
        recetaGuardada.setTorta(torta);
        recetaGuardada.setCantidad(2);
        recetaGuardada.setCostoManoObra(15.0f);
        recetaGuardada.setCostoOperativo(15.0f);
        recetaGuardada.setEstado(true);
        recetaGuardada.setPrompt("a delicious chocolate cake");
        recetaGuardada.setImagenUrl(firebaseUrl);

        RecetaDTO expectedDto = new RecetaDTO();
        expectedDto.setId(1L);
        expectedDto.setTorta(new TortaDTO());
        expectedDto.setCantidad(2);
        expectedDto.setEstado(true);
        expectedDto.setPrompt("a delicious chocolate cake");
        expectedDto.setImagenUrl(firebaseUrl);

        when(tortaRepository.findById(tortaId)).thenReturn(Optional.of(torta));
        when(recetaMapper.crearRecetaDTOToReceta(crearRecetaDTO, torta)).thenReturn(receta);
        when(storageService.uploadFileFromUrl(eq(originalUrl), anyString())).thenReturn(firebaseUrl);
        when(recetaRepository.save(any(Receta.class))).thenReturn(recetaGuardada);
        when(recetaMapper.recetaToRecetaDTO(recetaGuardada)).thenReturn(expectedDto);

        RecetaDTO result = recetaService.crearReceta(crearRecetaDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPrompt()).isEqualTo("a delicious chocolate cake");
        assertThat(result.getImagenUrl()).isEqualTo(firebaseUrl);
        assertThat(result.isEstado()).isTrue();
        assertThat(receta.getCostoManoObra()).isEqualTo(15.0f);
        assertThat(receta.getCostoOperativo()).isEqualTo(15.0f);

        verify(tortaRepository, times(1)).findById(tortaId);
        verify(storageService, times(1)).uploadFileFromUrl(eq(originalUrl), anyString());
        verify(recetaRepository, times(1)).save(any(Receta.class));
        verify(recetaMapper, times(1)).crearRecetaDTOToReceta(crearRecetaDTO, torta);
        verify(recetaMapper, times(1)).recetaToRecetaDTO(recetaGuardada);
    }

    @Test
    void crearReceta_ShouldThrowExceptionCuandoTortaNoExiste() {
        CrearRecetaDTO crearRecetaDTO = new CrearRecetaDTO();
        crearRecetaDTO.setTortaId(1L);

        when(tortaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recetaService.crearReceta(crearRecetaDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Torta no encontrada con id: 1");
    }

    @Test
    void crearReceta_ShouldThrowRuntimeExceptionCuandoStorageFalla() throws IOException {
        long tortaId = 1L;
        String originalUrl = "http://example.com/cake.png";

        CrearRecetaDTO crearRecetaDTO = new CrearRecetaDTO();
        crearRecetaDTO.setTortaId(tortaId);
        crearRecetaDTO.setCantidad(1);
        crearRecetaDTO.setTipoReceta(TipoReceta.TORTA);
        crearRecetaDTO.setPrompt("cake test");
        crearRecetaDTO.setImagenUrl(originalUrl);

        Tamano tamano = new Tamano();
        tamano.setTiempo(1.0f);
        Torta torta = new Torta();
        torta.setId(tortaId);
        torta.setValor(50.0f);
        torta.setTamano(tamano);

        Receta receta = new Receta();
        receta.setTorta(torta);

        when(tortaRepository.findById(tortaId)).thenReturn(Optional.of(torta));
        when(recetaMapper.crearRecetaDTOToReceta(crearRecetaDTO, torta)).thenReturn(receta);
        when(storageService.uploadFileFromUrl(anyString(), anyString())).thenThrow(new IOException("Error de red"));

        assertThatThrownBy(() -> recetaService.crearReceta(crearRecetaDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error al procesar la imagen de la receta");
    }

    @Test
    void obtenerRecetaPorId_ShouldReturnRecetaDTO() {
        long recetaId = 1L;
        Receta receta = new Receta();
        receta.setId(recetaId);
        receta.setPrompt("test prompt");
        receta.setImagenUrl("test url");

        RecetaDTO expectedDto = new RecetaDTO();
        expectedDto.setId(recetaId);
        expectedDto.setPrompt("test prompt");
        expectedDto.setImagenUrl("test url");

        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(receta));
        when(recetaMapper.recetaToRecetaDTO(receta)).thenReturn(expectedDto);

        RecetaDTO result = recetaService.obtenerRecetaPorId(recetaId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(recetaId);
        assertThat(result.getPrompt()).isEqualTo("test prompt");
        assertThat(result.getImagenUrl()).isEqualTo("test url");

        verify(recetaRepository, times(1)).findById(recetaId);
        verify(recetaMapper, times(1)).recetaToRecetaDTO(receta);
    }

    @Test
    void obtenerRecetaPorId_ShouldThrowExceptionCuandoNoExiste() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recetaService.obtenerRecetaPorId(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Receta no encontrada con id: 1");
    }
}
