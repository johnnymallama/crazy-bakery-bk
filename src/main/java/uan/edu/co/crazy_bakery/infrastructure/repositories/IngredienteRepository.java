package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uan.edu.co.crazy_bakery.domain.model.Ingredientes.Ingrediente;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, String> {
}
