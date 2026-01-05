package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarUsuarioDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearUsuarioDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.UsuarioDTO;
import uan.edu.co.crazy_bakery.application.services.UsuarioService;
import uan.edu.co.crazy_bakery.domain.model.Usuario; // Importación necesaria

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearUsuario() {
        CrearUsuarioDTO crearUsuarioDTO = new CrearUsuarioDTO();
        crearUsuarioDTO.setEmail("test@example.com");
        crearUsuarioDTO.setNombre("Test");

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId("1");
        usuarioDTO.setEmail(crearUsuarioDTO.getEmail());
        usuarioDTO.setNombre(crearUsuarioDTO.getNombre());
        usuarioDTO.setEstado(true);

        when(usuarioService.crearUsuario(any(CrearUsuarioDTO.class))).thenReturn(usuarioDTO);

        ResponseEntity<UsuarioDTO> result = usuarioController.crearUsuario(crearUsuarioDTO);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("1", result.getBody().getId());
        assertEquals("test@example.com", result.getBody().getEmail());
        assertEquals(true, result.getBody().isEstado());
    }

    @Test
    void testGetAllUsuarios() {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        List<UsuarioDTO> usuarioDTOList = Collections.singletonList(usuarioDTO);
        when(usuarioService.getAllUsuarios()).thenReturn(usuarioDTOList);

        ResponseEntity<List<UsuarioDTO>> result = usuarioController.getAllUsuarios();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals(usuarioDTO, result.getBody().get(0));
    }

    @Test
    void getUsuario_whenUserIsActive_shouldReturnOk() {
        String id = "1";
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEstado(true); // Usuario activo

        when(usuarioService.getUsuario(id)).thenReturn(Optional.of(usuario));

        ResponseEntity<?> response = usuarioController.getUsuario(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UsuarioDTO);
        assertEquals(id, ((UsuarioDTO) response.getBody()).getId());
    }

    @Test
    void getUsuario_whenUserIsInactive_shouldReturnNoContent() {
        String id = "1";
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEstado(false); // Usuario inactivo

        when(usuarioService.getUsuario(id)).thenReturn(Optional.of(usuario));

        ResponseEntity<?> response = usuarioController.getUsuario(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getUsuario_whenUserNotFound_shouldReturnNotFound() {
        String id = "1";
        when(usuarioService.getUsuario(id)).thenReturn(Optional.empty());

        ResponseEntity<?> response = usuarioController.getUsuario(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testInactivarUsuarioFound() {
        String id = "1";
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(id);
        usuarioDTO.setEstado(false);

        when(usuarioService.inactivarUsuario(id)).thenReturn(Optional.of(usuarioDTO));

        ResponseEntity<UsuarioDTO> result = usuarioController.inactivarUsuario(id);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isEstado());
    }

    @Test
    void testInactivarUsuarioNotFound() {
        String id = "1";
        when(usuarioService.inactivarUsuario(id)).thenReturn(Optional.empty());

        ResponseEntity<UsuarioDTO> result = usuarioController.inactivarUsuario(id);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void testActualizarUsuarioFound() {
        String id = "1";
        ActualizarUsuarioDTO actualizarDto = new ActualizarUsuarioDTO();
        actualizarDto.setTelefono("1234567890");
        actualizarDto.setDireccion("Calle Falsa 123");

        UsuarioDTO usuarioActualizadoDTO = new UsuarioDTO();
        usuarioActualizadoDTO.setId(id);
        usuarioActualizadoDTO.setTelefono(actualizarDto.getTelefono());
        usuarioActualizadoDTO.setDireccion(actualizarDto.getDireccion());

        when(usuarioService.actualizarUsuario(eq(id), any(ActualizarUsuarioDTO.class))).thenReturn(Optional.of(usuarioActualizadoDTO));

        ResponseEntity<UsuarioDTO> result = usuarioController.actualizarUsuario(id, actualizarDto);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("1234567890", result.getBody().getTelefono());
        assertEquals("Calle Falsa 123", result.getBody().getDireccion());
    }

    @Test
    void testActualizarUsuarioNotFound() {
        String id = "1";
        ActualizarUsuarioDTO actualizarDto = new ActualizarUsuarioDTO();
        when(usuarioService.actualizarUsuario(eq(id), any(ActualizarUsuarioDTO.class))).thenReturn(Optional.empty());

        ResponseEntity<UsuarioDTO> result = usuarioController.actualizarUsuario(id, actualizarDto);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
}
