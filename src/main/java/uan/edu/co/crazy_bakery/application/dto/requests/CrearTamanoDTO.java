package uan.edu.co.crazy_bakery.application.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearTamanoDTO {

    private String nombre;
    private int alto;
    private int diametro;
    private int porciones;
    private TipoReceta tipoReceta;
}
