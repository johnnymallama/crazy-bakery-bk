package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarUsuarioDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearUsuarioDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.UsuarioDTO;
import uan.edu.co.crazy_bakery.application.services.UsuarioService;
import uan.edu.co.crazy_bakery.domain.model.Usuario;
import uan.edu.co.crazy_bakery.infrastructure.web.security.FirebaseTokenFilter;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UsuarioController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FirebaseTokenFilter.class)
)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioDTO usuarioDTO;
    private CrearUsuarioDTO crearUsuarioDTO;

    @BeforeEach
    void setUp() {
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId("user123");
        usuarioDTO.setEmail("test@example.com");
        usuarioDTO.setNombre("Test User");
        usuarioDTO.setEstado(true);

        crearUsuarioDTO = new CrearUsuarioDTO();
        crearUsuarioDTO.setEmail("test@example.com");
        crearUsuarioDTO.setNombre("Test User");
    }

    @Test
    void crearUsuario_Success() throws Exception {
        when(usuarioService.crearUsuario(any(CrearUsuarioDTO.class))).thenReturn(usuarioDTO);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearUsuarioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("user123"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getAllUsuarios_Success() throws Exception {
        when(usuarioService.getAllUsuarios()).thenReturn(Collections.singletonList(usuarioDTO));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("user123"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void getUsuario_FoundAndActive() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId("user123");
        usuario.setEstado(true);

        when(usuarioService.getUsuario("user123")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/usuarios/user123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("user123"));
    }

    @Test
    void getUsuario_FoundAndInactive() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId("user123");
        usuario.setEstado(false);

        when(usuarioService.getUsuario("user123")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/usuarios/user123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUsuario_NotFound() throws Exception {
        when(usuarioService.getUsuario("user123")).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/user123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void inactivarUsuario_Success() throws Exception {
        usuarioDTO.setEstado(false);
        when(usuarioService.inactivarUsuario("user123")).thenReturn(Optional.of(usuarioDTO));

        mockMvc.perform(delete("/usuarios/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value(false));
    }

    @Test
    void inactivarUsuario_NotFound() throws Exception {
        when(usuarioService.inactivarUsuario("user123")).thenReturn(Optional.empty());

        mockMvc.perform(delete("/usuarios/user123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarUsuario_Success() throws Exception {
        ActualizarUsuarioDTO actualizarDto = new ActualizarUsuarioDTO();
        actualizarDto.setTelefono("1234567890");
        actualizarDto.setDireccion("Calle Falsa 123");

        usuarioDTO.setTelefono("1234567890");
        usuarioDTO.setDireccion("Calle Falsa 123");

        when(usuarioService.actualizarUsuario(eq("user123"), any(ActualizarUsuarioDTO.class)))
                .thenReturn(Optional.of(usuarioDTO));

        mockMvc.perform(put("/usuarios/user123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizarDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telefono").value("1234567890"))
                .andExpect(jsonPath("$.direccion").value("Calle Falsa 123"));
    }

    @Test
    void actualizarUsuario_NotFound() throws Exception {
        ActualizarUsuarioDTO actualizarDto = new ActualizarUsuarioDTO();

        when(usuarioService.actualizarUsuario(eq("user123"), any(ActualizarUsuarioDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/usuarios/user123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizarDto)))
                .andExpect(status().isNotFound());
    }
}
