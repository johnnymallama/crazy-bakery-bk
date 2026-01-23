package uan.edu.co.crazy_bakery.application.dto.torta;

import lombok.Data;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.TamanoDTO;

@Data
public class TortaDTO {

    private Long id;
    private IngredienteDTO bizcocho;
    private IngredienteDTO relleno;
    private IngredienteDTO cubertura;
    private TamanoDTO tamano;
    private float valor;
    private boolean estado;

}
