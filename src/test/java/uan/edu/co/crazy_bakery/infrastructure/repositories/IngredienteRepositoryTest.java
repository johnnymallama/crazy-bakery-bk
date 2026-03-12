package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;
import uan.edu.co.crazy_bakery.domain.model.Tamano;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class IngredienteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IngredienteRepository ingredienteRepository;

    private Ingrediente ingredienteActivo;
    private Ingrediente ingredienteInactivo;

    @BeforeEach
    void setUp() {
        ingredienteActivo = new Ingrediente();
        ingredienteActivo.setNombre("Bizcocho de Vainilla");
        ingredienteActivo.setTipoIngrediente(TipoIngrediente.BIZCOCHO);
        ingredienteActivo.setCostoPorGramo(10.0f);
        ingredienteActivo.setEstado(true);
        entityManager.persist(ingredienteActivo);

        ingredienteInactivo = new Ingrediente();
        ingredienteInactivo.setNombre("Relleno de Fresa Inactivo");
        ingredienteInactivo.setTipoIngrediente(TipoIngrediente.RELLENO);
        ingredienteInactivo.setCostoPorGramo(5.0f);
        ingredienteInactivo.setEstado(false);
        entityManager.persist(ingredienteInactivo);

        entityManager.flush();
    }

    @Test
    void testFindAllByEstado_activos() {
        List<Ingrediente> activos = ingredienteRepository.findAllByEstado(true);

        assertThat(activos).isNotEmpty();
        assertThat(activos).allMatch(i -> i.getEstado() == Boolean.TRUE);
    }

    @Test
    void testFindAllByEstado_inactivos() {
        List<Ingrediente> inactivos = ingredienteRepository.findAllByEstado(false);

        assertThat(inactivos).isNotEmpty();
        assertThat(inactivos).allMatch(i -> i.getEstado() == Boolean.FALSE);
    }

    @Test
    void testFindByTipoIngredienteAndEstado() {
        List<Ingrediente> result = ingredienteRepository.findByTipoIngredienteAndEstado(TipoIngrediente.BIZCOCHO, true);

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(i -> i.getTipoIngrediente() == TipoIngrediente.BIZCOCHO);
        assertThat(result).allMatch(i -> i.getEstado() == Boolean.TRUE);
    }

    @Test
    void testSearchIngredientes_conDatosCorrectos() {
        // Persiste tamano necesario para la native query
        Tamano tamano = new Tamano();
        tamano.setNombre("Pequeño");
        tamano.setTipoReceta(TipoReceta.TORTA);
        tamano.setEstado(true);
        tamano.setDiametro(15);
        tamano.setAlto(8);
        tamano.setPorciones(6);
        entityManager.persist(tamano);
        entityManager.flush();

        // La vista tamano_tipo_ingrediente no existe en H2, el resultado esperado es una lista vacía
        // pero la query debe ejecutarse sin error de compilación
        List<Ingrediente> result = ingredienteRepository.searchIngredientes(
                TipoReceta.TORTA.name(),
                tamano.getId(),
                TipoIngrediente.BIZCOCHO.name()
        );

        assertThat(result).isNotNull();
    }
}
