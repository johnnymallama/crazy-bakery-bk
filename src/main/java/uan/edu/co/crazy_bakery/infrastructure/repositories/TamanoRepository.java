package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;
import uan.edu.co.crazy_bakery.domain.model.Tamano;

import java.util.List;
import java.util.Optional;

@Repository
public interface TamanoRepository extends JpaRepository<Tamano, Long> {

    Optional<Tamano> findByIdAndEstadoTrue(Long id);

    List<Tamano> findAllByEstadoTrue();

    List<Tamano> findAllByTipoRecetaAndEstadoTrue(TipoReceta tipoReceta);
}
