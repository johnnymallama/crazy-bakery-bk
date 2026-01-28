
package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uan.edu.co.crazy_bakery.domain.model.IngredienteCosto;
import uan.edu.co.crazy_bakery.domain.model.IngredienteCostoId;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

import java.util.List;

@Repository
public interface IngredienteCostoRepository extends JpaRepository<IngredienteCosto, IngredienteCostoId> {

    List<IngredienteCosto> findByTipoRecetaAndTamanoIdAndIngredienteIdIn(
            TipoReceta tipoReceta,
            Long tamanoId,
            List<Long> ingredienteIds
    );
}
