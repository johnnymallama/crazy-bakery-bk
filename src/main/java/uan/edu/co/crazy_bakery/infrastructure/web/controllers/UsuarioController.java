package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarUsuarioDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearUsuarioDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.UsuarioDTO;
import uan.edu.co.crazy_bakery.application.mappers.UsuarioMapper;
import uan.edu.co.crazy_bakery.application.services.UsuarioService;

import java.util.List;

@Tag(name = "Usuarios", description = "Gestión de usuarios registrados en la plataforma")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario sincronizado con Firebase Authentication")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @PostMapping
    public ResponseEntity<UsuarioDTO> crearUsuario(@RequestBody CrearUsuarioDTO crearUsuarioDTO) {
        UsuarioDTO nuevoUsuario = usuarioService.crearUsuario(crearUsuarioDTO);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos los usuarios", description = "Retorna todos los usuarios registrados en la plataforma")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.getAllUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Retorna un usuario por su UID de Firebase. Devuelve 204 si está inactivo, 404 si no existe")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "204", description = "Usuario inactivo"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuario(@Parameter(description = "UID del usuario (Firebase)") @PathVariable String id) {
        return usuarioService.getUsuario(id)
                .map(usuario -> {
                    if (usuario.isEstado()) {
                        UsuarioDTO dto = UsuarioMapper.INSTANCE.usuarioToUsuarioDTO(usuario);
                        return ResponseEntity.ok(dto);
                    } else {
                        return ResponseEntity.noContent().build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Inactivar usuario", description = "Realiza una baja lógica del usuario (estado = false)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario inactivado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDTO> inactivarUsuario(@Parameter(description = "UID del usuario") @PathVariable String id) {
        return usuarioService.inactivarUsuario(id)
                .map(usuario -> new ResponseEntity<>(usuario, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de contacto y ubicación del usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@Parameter(description = "UID del usuario") @PathVariable String id, @RequestBody ActualizarUsuarioDTO actualizarUsuarioDTO) {
        return usuarioService.actualizarUsuario(id, actualizarUsuarioDTO)
                .map(usuario -> new ResponseEntity<>(usuario, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
