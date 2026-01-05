package uan.edu.co.crazy_bakery.application.dto.responses;

import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

@Data
public class IngredienteDTO {
    private Long codigo;
    private String nombre;
    private String composicion;
    private TipoIngrediente tipoIngrediente;
    private float valor;
}
