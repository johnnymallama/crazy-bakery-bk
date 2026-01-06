package uan.edu.co.crazy_bakery.application.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TamanoDTO {

    private Long id;
    private String nombre;
    private int alto;
    private int diametro;
    private int porciones;
    private TipoReceta tipoReceta;
    private boolean estado;
}
