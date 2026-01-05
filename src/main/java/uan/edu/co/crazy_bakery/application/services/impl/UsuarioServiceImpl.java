package uan.edu.co.crazy_bakery.application.services.impl;

import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarUsuarioDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearUsuarioDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.UsuarioDTO;
import uan.edu.co.crazy_bakery.application.mappers.UsuarioMapper;
import uan.edu.co.crazy_bakery.domain.model.Usuario;
import uan.edu.co.crazy_bakery.infrastructure.repositories.UsuarioRepository;
import uan.edu.co.crazy_bakery.application.services.UsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UsuarioDTO crearUsuario(CrearUsuarioDTO crearUsuarioDTO) {
        Usuario usuario = UsuarioMapper.INSTANCE.crearUsuarioDTOToUsuario(crearUsuarioDTO);
        usuario.setEstado(true); // Establecer el estado como activo
        usuario = usuarioRepository.save(usuario);
        return UsuarioMapper.INSTANCE.usuarioToUsuarioDTO(usuario);
    }

    @Override
    public Optional<Usuario> getUsuario(String id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public List<UsuarioDTO> getAllUsuarios() {
        return usuarioRepository.findAllByEstado(true)
                .stream()
                .map(UsuarioMapper.INSTANCE::usuarioToUsuarioDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioDTO> inactivarUsuario(String id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setEstado(false);
                    usuarioRepository.save(usuario);
                    return UsuarioMapper.INSTANCE.usuarioToUsuarioDTO(usuario);
                });
    }

    @Override
    public Optional<UsuarioDTO> actualizarUsuario(String id, ActualizarUsuarioDTO dto) {
        return usuarioRepository.findById(id)
            .map(usuario -> {
                usuario.setTelefono(dto.getTelefono());
                usuario.setDireccion(dto.getDireccion());
                usuario.setDepartamento(dto.getDepartamento());
                usuario.setCiudad(dto.getCiudad());
                usuarioRepository.save(usuario);
                return UsuarioMapper.INSTANCE.usuarioToUsuarioDTO(usuario);
            });
    }
}
