package uan.edu.co.crazy_bakery.application.services;

import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarUsuarioDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearUsuarioDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.UsuarioDTO;
import uan.edu.co.crazy_bakery.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    UsuarioDTO crearUsuario(CrearUsuarioDTO crearUsuarioDTO);
    Optional<Usuario> getUsuario(String id);
    List<UsuarioDTO> getAllUsuarios();
    Optional<UsuarioDTO> inactivarUsuario(String id);
    Optional<UsuarioDTO> actualizarUsuario(String id, ActualizarUsuarioDTO actualizarUsuarioDTO);
}
