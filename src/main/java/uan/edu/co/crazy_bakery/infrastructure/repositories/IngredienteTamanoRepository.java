package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uan.edu.co.crazy_bakery.domain.model.IngredienteTamano;
import uan.edu.co.crazy_bakery.domain.model.Tamano;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredienteTamanoRepository extends JpaRepository<IngredienteTamano, Long> {

    List<IngredienteTamano> findByTamanoIdAndEstadoTrue(Long tamanoId);

    // Spring Data JPA will automatically generate the query from the method name.
    // No @Query needed because the field names in IngredienteTamano match the method signature.
    Optional<IngredienteTamano> findByTamanoAndTipoIngredienteAndEstado(Tamano tamano, TipoIngrediente tipoIngrediente, boolean estado);
}
