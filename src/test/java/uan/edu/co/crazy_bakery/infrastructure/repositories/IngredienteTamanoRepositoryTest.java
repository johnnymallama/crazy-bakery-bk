package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;
import uan.edu.co.crazy_bakery.domain.model.IngredienteTamano;
import uan.edu.co.crazy_bakery.domain.model.Tamano;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class IngredienteTamanoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IngredienteTamanoRepository ingredienteTamanoRepository;

    private Tamano tamano;
    private IngredienteTamano relacionActiva;

    @BeforeEach
    void setUp() {
        tamano = new Tamano();
        tamano.setNombre("Test");
        tamano.setTipoReceta(TipoReceta.TORTA);
        tamano.setEstado(true);
        tamano.setDiametro(20);
        tamano.setAlto(10);
        tamano.setPorciones(8);
        entityManager.persist(tamano);

        relacionActiva = new IngredienteTamano();
        relacionActiva.setTamano(tamano);
        relacionActiva.setTipoIngrediente(TipoIngrediente.BIZCOCHO);
        relacionActiva.setGramos(100.0f);
        relacionActiva.setEstado(true);
        entityManager.persist(relacionActiva);

        IngredienteTamano relacionInactiva = new IngredienteTamano();
        relacionInactiva.setTamano(tamano);
        relacionInactiva.setTipoIngrediente(TipoIngrediente.RELLENO);
        relacionInactiva.setGramos(50.0f);
        relacionInactiva.setEstado(false);
        entityManager.persist(relacionInactiva);

        entityManager.flush();
    }

    @Test
    void findByTamanoIdAndEstadoTrue_ShouldReturnSoloRelacionesActivas() {
        List<IngredienteTamano> result = ingredienteTamanoRepository.findByTamanoIdAndEstadoTrue(tamano.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTipoIngrediente()).isEqualTo(TipoIngrediente.BIZCOCHO);
        assertThat(result.get(0).isEstado()).isTrue();
    }

    @Test
    void findByTamanoIdAndEstadoTrue_ShouldReturnListaVaciaCuandoNoHayActivas() {
        Tamano otroTamano = new Tamano();
        otroTamano.setNombre("Otro");
        otroTamano.setTipoReceta(TipoReceta.CUPCAKE);
        otroTamano.setEstado(true);
        otroTamano.setDiametro(10);
        otroTamano.setAlto(5);
        otroTamano.setPorciones(4);
        entityManager.persistAndFlush(otroTamano);

        List<IngredienteTamano> result = ingredienteTamanoRepository.findByTamanoIdAndEstadoTrue(otroTamano.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findByTamanoAndTipoIngredienteAndEstado_ShouldReturnRelacionCuandoExiste() {
        Optional<IngredienteTamano> result = ingredienteTamanoRepository
                .findByTamanoAndTipoIngredienteAndEstado(tamano, TipoIngrediente.BIZCOCHO, true);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(relacionActiva.getId());
        assertThat(result.get().getGramos()).isEqualTo(100.0f);
    }

    @Test
    void findByTamanoAndTipoIngredienteAndEstado_ShouldReturnEmptyParaRelacionInactiva() {
        Optional<IngredienteTamano> result = ingredienteTamanoRepository
                .findByTamanoAndTipoIngredienteAndEstado(tamano, TipoIngrediente.RELLENO, true);

        assertThat(result).isNotPresent();
    }

    @Test
    void findByTamanoAndTipoIngredienteAndEstado_ShouldReturnEmptyParaTipoInexistente() {
        Optional<IngredienteTamano> result = ingredienteTamanoRepository
                .findByTamanoAndTipoIngredienteAndEstado(tamano, TipoIngrediente.COBERTURA, true);

        assertThat(result).isNotPresent();
    }
}
