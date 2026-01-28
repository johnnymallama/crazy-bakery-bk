package uan.edu.co.crazy_bakery.application.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uan.edu.co.crazy_bakery.application.dto.torta.TortaDTO;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecetaDTO {

    private Long id;
    private TipoReceta tipoReceta;
    private TortaDTO torta;
    private int cantidad;
    private boolean estado;
    private String prompt;
    private String imagenUrl;

}
