package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;

import java.util.List;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    List<Ingrediente> findAllByEstado(boolean estado);

    List<Ingrediente> findByTipoIngredienteAndEstado(TipoIngrediente tipoIngrediente, boolean estado);

}
