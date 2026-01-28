package uan.edu.co.crazy_bakery.application.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenDTO {

    private Long id;
    private Date fecha;
    private String usuarioId;
    private List<RecetaDTO> recetas;
    private List<String> notas;
    private EstadoOrden estado;
    private float valorTotal;
    private float ganancia;
}
