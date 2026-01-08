package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uan.edu.co.crazy_bakery.domain.model.IngredienteTamano;

import java.util.List;

@Repository
public interface IngredienteTamanoRepository extends JpaRepository<IngredienteTamano, Long> {

    List<IngredienteTamano> findByTamanoIdAndEstadoTrue(Long tamanoId);
}
