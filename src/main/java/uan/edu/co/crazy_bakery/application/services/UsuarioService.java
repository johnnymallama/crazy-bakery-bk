package uan.edu.co.crazy_bakery.application.services;

import uan.edu.co.crazy_bakery.application.dto.requests.CrearUsuarioDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.UsuarioDTO;

import java.util.Optional;

public interface UsuarioService {
    UsuarioDTO crearUsuario(CrearUsuarioDTO crearUsuarioDTO);
    Optional<UsuarioDTO> getUsuario(String id);
}
