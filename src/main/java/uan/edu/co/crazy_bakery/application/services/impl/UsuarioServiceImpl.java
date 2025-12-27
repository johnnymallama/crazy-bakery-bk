package uan.edu.co.crazy_bakery.application.services.impl;

import org.springframework.stereotype.Service;
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
        usuario = usuarioRepository.save(usuario);
        return UsuarioMapper.INSTANCE.usuarioToUsuarioDTO(usuario);
    }

    @Override
    public Optional<UsuarioDTO> getUsuario(String id) {
        return usuarioRepository.findById(id)
                .map(UsuarioMapper.INSTANCE::usuarioToUsuarioDTO);
    }

    @Override
    public List<UsuarioDTO> getAllUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioMapper.INSTANCE::usuarioToUsuarioDTO)
                .collect(Collectors.toList());
    }
}
