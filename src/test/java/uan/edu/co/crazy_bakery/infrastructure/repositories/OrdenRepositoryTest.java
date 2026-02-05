package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import uan.edu.co.crazy_bakery.domain.model.*;
import uan.edu.co.crazy_bakery.domain.enums.*;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class OrdenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrdenRepository ordenRepository;

    @Test
    void testSaveAndFindById() {
        // given
        Usuario usuario = new Usuario();
        usuario.setId("testuser");
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setEmail("test.user@example.com");
        usuario.setTipo(TipoUsuario.CONSUMIDOR);
        usuario.setEstado(true);
        entityManager.persist(usuario);

        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setFecha(new Date());
        orden.setEstado(EstadoOrden.CREADO);
        orden.setValorTotal(100.0f);

        // when
        Orden savedOrden = ordenRepository.save(orden);

        // then
        Orden foundOrden = entityManager.find(Orden.class, savedOrden.getId());
        assertThat(foundOrden).isNotNull();
        assertThat(foundOrden.getUsuario().getId()).isEqualTo(usuario.getId());
    }

    @Test
    void testUpdateOrden_addNota() {
        // given
        Usuario usuario = new Usuario();
        usuario.setId("testuser2");
        usuario.setNombre("Test");
        usuario.setApellido("User 2");
        usuario.setEmail("test.user2@example.com");
        usuario.setTipo(TipoUsuario.CONSUMIDOR);
        usuario.setEstado(true);
        entityManager.persist(usuario);

        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setFecha(new Date());
        orden.setEstado(EstadoOrden.CREADO);
        orden.setValorTotal(100.0f);
        orden.setNotas(new ArrayList<>());
        Orden savedOrden = entityManager.persistFlushFind(orden);

        // when
        Nota nuevaNota = new Nota();
        nuevaNota.setNota("Nueva nota de prueba");
        nuevaNota.setUsuario(usuario);
        nuevaNota.setOrden(savedOrden);

        savedOrden.getNotas().add(nuevaNota);
        ordenRepository.save(savedOrden);

        // then
        Orden foundOrden = entityManager.find(Orden.class, savedOrden.getId());
        assertThat(foundOrden.getNotas()).hasSize(1);
        assertThat(foundOrden.getNotas().get(0).getNota()).isEqualTo("Nueva nota de prueba");
    }

    @Test
    void testUpdateOrden_addReceta() {
        // --- Given: Create all dependent entities ---
        // 1. Ingredients
        Ingrediente bizcocho = createAndPersistIngrediente(TipoIngrediente.BIZCOCHO, "Bizcocho de Vainilla");
        Ingrediente relleno = createAndPersistIngrediente(TipoIngrediente.RELLENO, "Crema de Fresa");
        Ingrediente cobertura = createAndPersistIngrediente(TipoIngrediente.COBERTURA, "Ganache de Chocolate");

        // 2. Tamano
        Tamano tamano = new Tamano();
        tamano.setNombre("Pequeño");
        tamano.setPorciones(8);
        tamano.setTipoReceta(TipoReceta.TORTA);
        tamano.setEstado(true);
        // Missing diametro and alto for Tamano entity
        tamano.setDiametro(20);
        tamano.setAlto(10);
        entityManager.persist(tamano);

        // 3. Torta
        Torta torta = new Torta();
        torta.setBizcocho(bizcocho);
        torta.setRelleno(relleno);
        torta.setCubertura(cobertura);
        torta.setTamano(tamano);
        torta.setValor(150.0f);
        torta.setEstado(true);
        entityManager.persist(torta);

        // 4. Receta
        Receta receta = new Receta();
        receta.setTorta(torta);
        receta.setCantidad(1);
        receta.setCostoManoObra(100.0f);
        receta.setCostoOperativo(200.0f);
        receta.setEstado(true);
        receta.setTipoReceta(TipoReceta.TORTA);
        entityManager.persist(receta);

        // 5. Usuario
        Usuario usuario = new Usuario();
        usuario.setId("testuser3");
        usuario.setEmail("test.user3@example.com");
        usuario.setTipo(TipoUsuario.CONSUMIDOR);
        usuario.setEstado(true);
        entityManager.persist(usuario);

        // 6. Orden
        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setFecha(new Date());
        orden.setEstado(EstadoOrden.CREADO);
        orden.setValorTotal(100.0f);
        orden.setRecetas(new ArrayList<>());
        Orden savedOrden = entityManager.persistFlushFind(orden);

        // --- When: Add the receta to the orden ---
        savedOrden.getRecetas().add(receta);
        ordenRepository.save(savedOrden);

        // --- Then: Verify the relationship ---
        Orden foundOrden = entityManager.find(Orden.class, savedOrden.getId());
        assertThat(foundOrden.getRecetas()).hasSize(1);
        assertThat(foundOrden.getRecetas().get(0).getId()).isEqualTo(receta.getId());
    }

    private Ingrediente createAndPersistIngrediente(TipoIngrediente tipo, String nombre) {
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setTipoIngrediente(tipo);
        ingrediente.setNombre(nombre);
        ingrediente.setCostoPorGramo(50.0f);
        ingrediente.setEstado(true);
        entityManager.persist(ingrediente);
        return ingrediente;
    }
}
