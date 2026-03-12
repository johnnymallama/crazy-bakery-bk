package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import uan.edu.co.crazy_bakery.domain.enums.TipoUsuario;
import uan.edu.co.crazy_bakery.domain.model.Usuario;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class UsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        Usuario usuarioActivo = new Usuario();
        usuarioActivo.setId("usuario-activo-1");
        usuarioActivo.setNombre("Juan");
        usuarioActivo.setApellido("Perez");
        usuarioActivo.setEmail("juan.perez@example.com");
        usuarioActivo.setTipo(TipoUsuario.CONSUMIDOR);
        usuarioActivo.setEstado(true);
        entityManager.persist(usuarioActivo);

        Usuario usuarioInactivo = new Usuario();
        usuarioInactivo.setId("usuario-inactivo-1");
        usuarioInactivo.setNombre("Ana");
        usuarioInactivo.setApellido("Lopez");
        usuarioInactivo.setEmail("ana.lopez@example.com");
        usuarioInactivo.setTipo(TipoUsuario.CONSUMIDOR);
        usuarioInactivo.setEstado(false);
        entityManager.persist(usuarioInactivo);

        entityManager.flush();
    }

    @Test
    void testFindAllByEstado_activos() {
        List<Usuario> activos = usuarioRepository.findAllByEstado(true);

        assertThat(activos).isNotEmpty();
        assertThat(activos).allMatch(u -> u.isEstado());
    }

    @Test
    void testFindAllByEstado_inactivos() {
        List<Usuario> inactivos = usuarioRepository.findAllByEstado(false);

        assertThat(inactivos).isNotEmpty();
        assertThat(inactivos).allMatch(u -> !u.isEstado());
    }
}
