package uan.edu.co.crazy_bakery.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

@Schema(description = "Datos para crear o actualizar un ingrediente")
@Data
public class CrearIngredienteDTO {

    @Schema(description = "Nombre comercial del ingrediente", example = "Harina de trigo")
    private String nombre;

    @Schema(description = "Descripción de la composición o características del ingrediente", example = "Harina de trigo 000 sin gluten")
    private String composicion;

    @Schema(description = "Clasificación del ingrediente según su uso en la torta", example = "BIZCOCHO")
    private TipoIngrediente tipoIngrediente;

    @Schema(description = "Costo del ingrediente por gramo en pesos colombianos", example = "0.05")
    private float costoPorGramo;
}
