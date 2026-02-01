package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uan.edu.co.crazy_bakery.domain.model.Receta;

import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    @Query(value = "SELECT imagen_url FROM ultimas_propuestas", nativeQuery = true)
    List<String> findUltimasImagenes();
}
