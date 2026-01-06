package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;
import uan.edu.co.crazy_bakery.domain.model.Tamano;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {"spring.flyway.enabled=false"})
class TamanoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TamanoRepository tamanoRepository;

    private Tamano tamanoActivo;
    private Tamano tamanoInactivo;

    @BeforeEach
    void setUp() {
        tamanoActivo = new Tamano();
        tamanoActivo.setNombre("Activo");
        tamanoActivo.setAlto(10);
        tamanoActivo.setDiametro(15);
        tamanoActivo.setPorciones(8);
        tamanoActivo.setTipoReceta(TipoReceta.TORTA);
        tamanoActivo.setEstado(true);
        entityManager.persist(tamanoActivo);

        tamanoInactivo = new Tamano();
        tamanoInactivo.setNombre("Inactivo");
        tamanoInactivo.setAlto(5);
        tamanoInactivo.setDiametro(10);
        tamanoInactivo.setPorciones(4);
        tamanoInactivo.setTipoReceta(TipoReceta.CUPCAKE);
        tamanoInactivo.setEstado(false);
        entityManager.persist(tamanoInactivo);

        entityManager.flush();
    }

    @Test
    void testFindByIdAndEstadoTrue_WhenFound() {
        Optional<Tamano> found = tamanoRepository.findByIdAndEstadoTrue(tamanoActivo.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(tamanoActivo.getId());
    }

    @Test
    void testFindByIdAndEstadoTrue_WhenNotFound() {
        Optional<Tamano> found = tamanoRepository.findByIdAndEstadoTrue(tamanoInactivo.getId());

        assertThat(found).isNotPresent();
    }

    @Test
    void testFindAllByEstadoTrue() {
        List<Tamano> foundList = tamanoRepository.findAllByEstadoTrue();

        assertThat(foundList).hasSize(1);
        assertThat(foundList.get(0).getId()).isEqualTo(tamanoActivo.getId());
        assertThat(foundList.get(0).isEstado()).isTrue();
    }
}
