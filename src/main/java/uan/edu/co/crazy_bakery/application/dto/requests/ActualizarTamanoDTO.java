package uan.edu.co.crazy_bakery.application.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarTamanoDTO {

    private int alto;
    private int diametro;
    private int porciones;
    private float tiempo;
}
