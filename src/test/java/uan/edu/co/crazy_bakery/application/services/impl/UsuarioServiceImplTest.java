package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarUsuarioDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearUsuarioDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.UsuarioDTO;
import uan.edu.co.crazy_bakery.domain.model.Usuario;
import uan.edu.co.crazy_bakery.infrastructure.repositories.UsuarioRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId("user123");
        usuario.setNombre("Test User");
        usuario.setEmail("test@example.com");
        usuario.setEstado(true);
    }

    @Test
    void crearUsuario_ShouldReturnUsuarioDTO() {
        CrearUsuarioDTO crearUsuarioDTO = new CrearUsuarioDTO();
        crearUsuarioDTO.setNombre("Test User");
        crearUsuarioDTO.setEmail("test@example.com");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioDTO result = usuarioService.crearUsuario(crearUsuarioDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("user123");
        assertThat(result.getNombre()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void getUsuario_ShouldReturnUsuarioCuandoExiste() {
        when(usuarioRepository.findById("user123")).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.getUsuario("user123");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("user123");
    }

    @Test
    void getUsuario_ShouldReturnEmptyOptionalCuandoNoExiste() {
        when(usuarioRepository.findById("user123")).thenReturn(Optional.empty());

        Optional<Usuario> result = usuarioService.getUsuario("user123");

        assertThat(result).isNotPresent();
    }

    @Test
    void getAllUsuarios_ShouldReturnListDeUsuariosActivos() {
        when(usuarioRepository.findAllByEstado(true)).thenReturn(Collections.singletonList(usuario));

        List<UsuarioDTO> result = usuarioService.getAllUsuarios();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("user123");
    }

    @Test
    void inactivarUsuario_ShouldReturnUsuarioCuandoExiste() {
        when(usuarioRepository.findById("user123")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Optional<UsuarioDTO> result = usuarioService.inactivarUsuario("user123");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("user123");
    }

    @Test
    void inactivarUsuario_ShouldReturnEmptyOptionalCuandoNoExiste() {
        when(usuarioRepository.findById("inexistente")).thenReturn(Optional.empty());

        Optional<UsuarioDTO> result = usuarioService.inactivarUsuario("inexistente");

        assertThat(result).isNotPresent();
    }

    @Test
    void actualizarUsuario_ShouldReturnUsuarioActualizadoCuandoExiste() {
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO();
        dto.setTelefono("3001234567");
        dto.setDireccion("Calle 10 # 20-30");
        dto.setDepartamento("Cundinamarca");
        dto.setCiudad("Bogotá");

        when(usuarioRepository.findById("user123")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Optional<UsuarioDTO> result = usuarioService.actualizarUsuario("user123", dto);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("user123");
    }

    @Test
    void actualizarUsuario_ShouldReturnEmptyOptionalCuandoNoExiste() {
        when(usuarioRepository.findById(eq("inexistente"))).thenReturn(Optional.empty());

        Optional<UsuarioDTO> result = usuarioService.actualizarUsuario("inexistente", new ActualizarUsuarioDTO());

        assertThat(result).isNotPresent();
    }
}
