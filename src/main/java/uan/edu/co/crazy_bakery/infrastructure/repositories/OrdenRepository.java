package uan.edu.co.crazy_bakery.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;
import uan.edu.co.crazy_bakery.domain.model.Orden;

import java.util.Date;
import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {

    List<Orden> findByUsuarioId(String usuarioId);

    List<Orden> findByEstado(EstadoOrden estado);

    List<Orden> findByFechaGreaterThanEqualAndFechaLessThan(Date fechaInicio, Date fechaFin);

}
