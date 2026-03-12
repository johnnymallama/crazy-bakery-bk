package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearUsuarioDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.UsuarioDTO;
import uan.edu.co.crazy_bakery.domain.model.Usuario;
import uan.edu.co.crazy_bakery.infrastructure.repositories.UsuarioRepository;

import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarUsuarioDTO;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearUsuario() {
        CrearUsuarioDTO crearUsuarioDTO = new CrearUsuarioDTO();
        crearUsuarioDTO.setNombre("Test User");

        Usuario usuario = new Usuario();
        usuario.setId("1");
        usuario.setNombre("Test User");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioDTO result = usuarioService.crearUsuario(crearUsuarioDTO);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Test User", result.getNombre());
    }

    @Test
    void testGetUsuarioFound() {
        String id = "1";
        Usuario usuario = new Usuario();
        usuario.setId(id);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        // The service now returns Optional<Usuario> instead of Optional<UsuarioDTO>
        Optional<Usuario> result = usuarioService.getUsuario(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void testGetUsuarioNotFound() {
        String id = "1";
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // The service now returns Optional<Usuario> instead of Optional<UsuarioDTO>
        Optional<Usuario> result = usuarioService.getUsuario(id);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllUsuarios() {
        Usuario usuario = new Usuario();
        usuario.setId("1");
        List<Usuario> usuarioList = Collections.singletonList(usuario);
        // The service now calls findAllByEstado(true)
        when(usuarioRepository.findAllByEstado(true)).thenReturn(usuarioList);

        List<UsuarioDTO> result = usuarioService.getAllUsuarios();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }

    @Test
    void testInactivarUsuario_cuandoExiste_retornaUsuarioInactivo() {
        // Arrange
        String id = "1";
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre("Test User");
        usuario.setEstado(true);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Optional<UsuarioDTO> result = usuarioService.inactivarUsuario(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void testInactivarUsuario_cuandoNoExiste_retornaEmpty() {
        // Arrange
        String id = "inexistente";
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<UsuarioDTO> result = usuarioService.inactivarUsuario(id);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testActualizarUsuario_cuandoExiste_retornaUsuarioActualizado() {
        // Arrange
        String id = "1";
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre("Test User");

        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO();
        dto.setTelefono("3001234567");
        dto.setDireccion("Calle 10 # 20-30");
        dto.setDepartamento("Cundinamarca");
        dto.setCiudad("Bogotá");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Optional<UsuarioDTO> result = usuarioService.actualizarUsuario(id, dto);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void testActualizarUsuario_cuandoNoExiste_retornaEmpty() {
        // Arrange
        String id = "inexistente";
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO();

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<UsuarioDTO> result = usuarioService.actualizarUsuario(id, dto);

        // Assert
        assertFalse(result.isPresent());
    }
}
