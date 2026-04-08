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
import uan.edu.co.crazy_bakery.domain.model.Receta;
import uan.edu.co.crazy_bakery.domain.model.Tamano;
import uan.edu.co.crazy_bakery.domain.model.Torta;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class RecetaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RecetaRepository recetaRepository;

    private Torta torta;

    @BeforeEach
    void setUp() {
        Ingrediente bizcocho = new Ingrediente();
        bizcocho.setNombre("Bizcocho de Chocolate");
        bizcocho.setComposicion("Chocolate");
        bizcocho.setTipoIngrediente(TipoIngrediente.BIZCOCHO);
        bizcocho.setCostoPorGramo(10000);
        bizcocho.setEstado(true);
        entityManager.persist(bizcocho);

        Ingrediente relleno = new Ingrediente();
        relleno.setNombre("Relleno de Fresa");
        relleno.setComposicion("Fresa");
        relleno.setTipoIngrediente(TipoIngrediente.RELLENO);
        relleno.setCostoPorGramo(5000);
        relleno.setEstado(true);
        entityManager.persist(relleno);

        Ingrediente cobertura = new Ingrediente();
        cobertura.setNombre("Cobertura de Vainilla");
        cobertura.setComposicion("Vainilla");
        cobertura.setTipoIngrediente(TipoIngrediente.COBERTURA);
        cobertura.setCostoPorGramo(8000);
        cobertura.setEstado(true);
        entityManager.persist(cobertura);

        Tamano tamano = new Tamano();
        tamano.setNombre("Mediana");
        tamano.setPorciones(12);
        tamano.setTipoReceta(TipoReceta.TORTA);
        tamano.setEstado(true);
        tamano.setDiametro(20);
        tamano.setAlto(10);
        entityManager.persist(tamano);

        torta = new Torta();
        torta.setBizcocho(bizcocho);
        torta.setRelleno(relleno);
        torta.setCubertura(cobertura);
        torta.setTamano(tamano);
        torta.setValor(100000f);
        torta.setEstado(true);
        entityManager.persist(torta);

        entityManager.flush();
    }

    @Test
    void save_ShouldPersistRecetaYAsignarId() {
        Receta receta = new Receta();
        receta.setTorta(torta);
        receta.setCantidad(2);
        receta.setCostoManoObra(100f);
        receta.setCostoOperativo(200f);
        receta.setEstado(true);
        receta.setTipoReceta(TipoReceta.TORTA);

        Receta savedReceta = recetaRepository.save(receta);
        entityManager.flush();

        assertThat(savedReceta.getId()).isNotNull();
    }

    @Test
    void findById_ShouldReturnRecetaCuandoExiste() {
        Receta receta = new Receta();
        receta.setTorta(torta);
        receta.setCantidad(2);
        receta.setCostoManoObra(100f);
        receta.setCostoOperativo(200f);
        receta.setEstado(true);
        receta.setTipoReceta(TipoReceta.TORTA);
        entityManager.persistAndFlush(receta);

        Optional<Receta> result = recetaRepository.findById(receta.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTorta().getId()).isEqualTo(torta.getId());
        assertThat(result.get().getCantidad()).isEqualTo(2);
        assertThat(result.get().getCostoManoObra()).isEqualTo(100f);
        assertThat(result.get().getCostoOperativo()).isEqualTo(200f);
        assertThat(result.get().isEstado()).isTrue();
    }

    @Test
    void findById_ShouldReturnEmptyOptionalCuandoNoExiste() {
        Optional<Receta> result = recetaRepository.findById(999L);

        assertThat(result).isNotPresent();
    }

    @Test
    void findUltimasImagenes_ShouldLanzarExcepcionEnH2() {
        // La vista 'ultimas_propuestas' solo existe en MySQL (producción).
        // En H2 se espera una excepción al intentar ejecutar la native query.
        assertThatThrownBy(() -> recetaRepository.findUltimasImagenes())
                .isInstanceOf(Exception.class);
    }
}
