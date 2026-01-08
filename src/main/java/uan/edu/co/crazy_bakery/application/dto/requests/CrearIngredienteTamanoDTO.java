package uan.edu.co.crazy_bakery.application.dto.requests;

import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

@Data
public class CrearIngredienteTamanoDTO {
    private Long tamanoId;
    private TipoIngrediente tipoIngrediente;
    private float gramos;
}
