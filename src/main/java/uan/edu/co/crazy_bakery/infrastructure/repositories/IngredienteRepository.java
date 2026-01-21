package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;


import java.util.List;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    List<Ingrediente> findAllByEstado(boolean estado);

    List<Ingrediente> findByTipoIngredienteAndEstado(TipoIngrediente tipoIngrediente, boolean estado);

    @Query(value = "select a.* " +
            "from ingrediente as a " +
            "inner join tamano_tipo_ingrediente as b on a.tipo_ingrediente = b.tipo_ingrediente " +
            "inner join tamano as c on c.id = b.tamano_id " +
            "where c.tipo_receta = :tipoReceta " +
            "and c.id = :tamanoId " +
            "and a.tipo_ingrediente = :tipoIngrediente " +
            "and a.estado = true " +
            "and c.estado = true",
            nativeQuery = true)
    List<Ingrediente> searchIngredientes(@Param("tipoReceta") String tipoReceta,
                                       @Param("tamanoId") Long tamanoId,
                                       @Param("tipoIngrediente") String tipoIngrediente);

}
