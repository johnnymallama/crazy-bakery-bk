package uan.edu.co.crazy_bakery.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Datos para actualizar las dimensiones de un tamaño de torta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarTamanoDTO {

    @Schema(description = "Altura del molde en centímetros", example = "10")
    private int alto;

    @Schema(description = "Diámetro del molde en centímetros", example = "20")
    private int diametro;

    @Schema(description = "Número de porciones que rinde el tamaño", example = "12")
    private int porciones;

    @Schema(description = "Tiempo de preparación en horas", example = "2.5")
    private float tiempo;
}
