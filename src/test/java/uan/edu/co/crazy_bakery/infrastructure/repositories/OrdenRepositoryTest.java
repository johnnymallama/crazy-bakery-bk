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
import java.util.List;

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

    @Test
    void testFindByUsuarioId() {
        // given
        Usuario usuario = new Usuario();
        usuario.setId("busqueda-usuario");
        usuario.setNombre("Busqueda");
        usuario.setApellido("Test");
        usuario.setEmail("busqueda@example.com");
        usuario.setTipo(TipoUsuario.CONSUMIDOR);
        usuario.setEstado(true);
        entityManager.persist(usuario);

        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setFecha(new Date());
        orden.setEstado(EstadoOrden.CREADO);
        orden.setValorTotal(200.0f);
        entityManager.persist(orden);
        entityManager.flush();

        // when
        List<Orden> ordenes = ordenRepository.findByUsuarioId("busqueda-usuario");

        // then
        assertThat(ordenes).hasSize(1);
        assertThat(ordenes.get(0).getUsuario().getId()).isEqualTo("busqueda-usuario");
    }

    @Test
    void testFindByEstado() {
        // given
        Usuario usuario = new Usuario();
        usuario.setId("estado-usuario");
        usuario.setNombre("Estado");
        usuario.setApellido("Test");
        usuario.setEmail("estado@example.com");
        usuario.setTipo(TipoUsuario.CONSUMIDOR);
        usuario.setEstado(true);
        entityManager.persist(usuario);

        Orden ordenCreado = new Orden();
        ordenCreado.setUsuario(usuario);
        ordenCreado.setFecha(new Date());
        ordenCreado.setEstado(EstadoOrden.CREADO);
        ordenCreado.setValorTotal(100.0f);
        entityManager.persist(ordenCreado);

        Orden ordenEntregado = new Orden();
        ordenEntregado.setUsuario(usuario);
        ordenEntregado.setFecha(new Date());
        ordenEntregado.setEstado(EstadoOrden.ENTREGADO);
        ordenEntregado.setValorTotal(150.0f);
        entityManager.persist(ordenEntregado);
        entityManager.flush();

        // when
        List<Orden> creadas = ordenRepository.findByEstado(EstadoOrden.CREADO);

        // then
        assertThat(creadas).isNotEmpty();
        assertThat(creadas).allMatch(o -> o.getEstado() == EstadoOrden.CREADO);
    }

    @Test
    void testFindByFechaGreaterThanEqualAndFechaLessThan() {
        // given
        Usuario usuario = new Usuario();
        usuario.setId("fecha-usuario");
        usuario.setNombre("Fecha");
        usuario.setApellido("Test");
        usuario.setEmail("fecha@example.com");
        usuario.setTipo(TipoUsuario.CONSUMIDOR);
        usuario.setEstado(true);
        entityManager.persist(usuario);

        Date ahora = new Date();

        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setFecha(ahora);
        orden.setEstado(EstadoOrden.CREADO);
        orden.setValorTotal(100.0f);
        entityManager.persist(orden);
        entityManager.flush();

        Date inicio = new Date(ahora.getTime() - 1000);
        Date fin = new Date(ahora.getTime() + 1000);

        // when
        List<Orden> result = ordenRepository.findByFechaGreaterThanEqualAndFechaLessThan(inicio, fin);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void testFindByUsuarioIdAndFechaAfterOrderByFechaDesc() {
        // given
        Usuario usuario = new Usuario();
        usuario.setId("hist-usuario");
        usuario.setNombre("Hist");
        usuario.setApellido("Test");
        usuario.setEmail("hist@example.com");
        usuario.setTipo(TipoUsuario.CONSUMIDOR);
        usuario.setEstado(true);
        entityManager.persist(usuario);

        Date ahora = new Date();

        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setFecha(ahora);
        orden.setEstado(EstadoOrden.CREADO);
        orden.setValorTotal(100.0f);
        entityManager.persist(orden);
        entityManager.flush();

        Date hace1Hora = new Date(ahora.getTime() - 3600_000);

        // when
        List<Orden> result = ordenRepository.findByUsuarioIdAndFechaAfterOrderByFechaDesc("hist-usuario", hace1Hora);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsuario().getId()).isEqualTo("hist-usuario");
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
