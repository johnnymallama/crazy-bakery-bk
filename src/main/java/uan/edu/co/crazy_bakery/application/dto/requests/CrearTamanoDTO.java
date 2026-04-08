package uan.edu.co.crazy_bakery.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

@Schema(description = "Datos para registrar un nuevo tamaño de torta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearTamanoDTO {

    @Schema(description = "Nombre descriptivo del tamaño", example = "Mediana")
    private String nombre;

    @Schema(description = "Altura del molde en centímetros", example = "10")
    private int alto;

    @Schema(description = "Diámetro del molde en centímetros", example = "20")
    private int diametro;

    @Schema(description = "Número de porciones que rinde", example = "12")
    private int porciones;

    @Schema(description = "Tipo de receta al que aplica este tamaño", example = "TORTA")
    @JsonProperty("tipo_receta")
    private TipoReceta tipoReceta;

    @Schema(description = "Tiempo estimado de preparación en horas", example = "2.5")
    private float tiempo;
}
