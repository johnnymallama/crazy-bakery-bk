package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import uan.edu.co.crazy_bakery.domain.model.Receta;
import uan.edu.co.crazy_bakery.domain.model.Torta;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {"spring.flyway.enabled=false"})
class RecetaRepositoryTest {

    @Autowired
    private RecetaRepository recetaRepository;

    @Autowired
    private TortaRepository tortaRepository; // Se necesita para crear la torta

    @Autowired
    private IngredienteRepository ingredienteRepository; // Se necesita para crear los ingredientes

    @Autowired
    private TamanoRepository tamanoRepository; // Se necesita para crear el tamaño


    private Torta torta;

    @BeforeEach
    void setUp() {
        // Es necesario guardar las dependencias de Torta primero
        // para evitar errores de llave foránea
        uan.edu.co.crazy_bakery.domain.model.Ingrediente bizcocho = new uan.edu.co.crazy_bakery.domain.model.Ingrediente();
        bizcocho.setNombre("Bizcocho de Chocolate");
        bizcocho.setComposicion("Chocolate");
        bizcocho.setTipoIngrediente(uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente.BIZCOCHO);
        bizcocho.setCostoPorGramo(10000);
        bizcocho.setEstado(true);
        ingredienteRepository.save(bizcocho);

        uan.edu.co.crazy_bakery.domain.model.Ingrediente relleno = new uan.edu.co.crazy_bakery.domain.model.Ingrediente();
        relleno.setNombre("Relleno de Fresa");
        relleno.setComposicion("Fresa");
        relleno.setTipoIngrediente(uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente.RELLENO);
        relleno.setCostoPorGramo(5000);
        relleno.setEstado(true);
        ingredienteRepository.save(relleno);

        uan.edu.co.crazy_bakery.domain.model.Ingrediente cobertura = new uan.edu.co.crazy_bakery.domain.model.Ingrediente();
        cobertura.setNombre("Cobertura de Vainilla");
        cobertura.setComposicion("Vainilla");
        cobertura.setTipoIngrediente(uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente.COBERTURA);
        cobertura.setCostoPorGramo(8000);
        cobertura.setEstado(true);
        ingredienteRepository.save(cobertura);

        uan.edu.co.crazy_bakery.domain.model.Tamano tamano = new uan.edu.co.crazy_bakery.domain.model.Tamano();
        tamano.setNombre("Mediana");
        tamano.setPorciones(12);
        tamano.setTipoReceta(TipoReceta.TORTA);
        tamano.setEstado(true);
        tamano.setDiametro(20);
        tamano.setAlto(10);
        tamanoRepository.save(tamano);

        torta = new Torta();
        torta.setBizcocho(bizcocho);
        torta.setRelleno(relleno);
        torta.setCubertura(cobertura);
        torta.setTamano(tamano);
        torta.setValor(100000f); // Valor de ejemplo
        torta.setEstado(true);
        tortaRepository.save(torta);
    }

    @Test
    void saveRecetaAndFindById() {
        Receta receta = new Receta();
        receta.setTorta(torta);
        receta.setCantidad(2);
        receta.setCostoManoObra(100f);
        receta.setCostoOperativo(200f);
        receta.setEstado(true);
        receta.setTipoReceta(TipoReceta.TORTA);

        Receta savedReceta = recetaRepository.save(receta);
        assertNotNull(savedReceta.getId());

        Optional<Receta> foundRecetaOpt = recetaRepository.findById(savedReceta.getId());
        assertTrue(foundRecetaOpt.isPresent());

        Receta foundReceta = foundRecetaOpt.get();
        assertEquals(torta.getId(), foundReceta.getTorta().getId());
        assertEquals(2, foundReceta.getCantidad());
        assertEquals(100f, foundReceta.getCostoManoObra());
        assertEquals(200f, foundReceta.getCostoOperativo());
        assertTrue(foundReceta.isEstado());
    }
}
