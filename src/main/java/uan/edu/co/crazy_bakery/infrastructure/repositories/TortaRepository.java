package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uan.edu.co.crazy_bakery.domain.model.Torta;

public interface TortaRepository extends JpaRepository<Torta, Long> {
}
