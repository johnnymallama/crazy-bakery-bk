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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {"spring.flyway.enabled=false"})
class IngredienteTamanoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IngredienteTamanoRepository ingredienteTamanoRepository;

    private Tamano tamano;

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

        IngredienteTamano ingredienteTamano = new IngredienteTamano();
        ingredienteTamano.setTamano(tamano);
        ingredienteTamano.setTipoIngrediente(TipoIngrediente.BIZCOCHO);
        ingredienteTamano.setGramos(100.0f);
        ingredienteTamano.setEstado(true);
        entityManager.persist(ingredienteTamano);

        entityManager.flush();
    }

    @Test
    void testFindByTamanoIdAndEstadoTrue() {
        // Act
        List<IngredienteTamano> result = ingredienteTamanoRepository.findByTamanoIdAndEstadoTrue(tamano.getId());

        // Assert
        assertThat(result).hasSize(1);
    }
}
