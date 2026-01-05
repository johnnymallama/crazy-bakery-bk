package uan.edu.co.crazy_bakery.application.dto.requests;

import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

@Data
public class CrearIngredienteDTO {
    private String codigo;
    private String nombre;
    private String composicion;
    private TipoIngrediente tipoIngrediente;
    private float valor;
}
