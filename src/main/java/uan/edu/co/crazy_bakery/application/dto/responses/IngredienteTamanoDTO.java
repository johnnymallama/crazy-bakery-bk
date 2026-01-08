package uan.edu.co.crazy_bakery.application.dto.responses;

import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

@Data
public class IngredienteTamanoDTO {
    private Long id;
    private Long tamanoId;
    private String tamanoNombre;
    private TipoIngrediente tipoIngrediente;
    private float gramos;
}
