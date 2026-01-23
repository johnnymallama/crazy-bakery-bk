package uan.edu.co.crazy_bakery.application.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDetailDTO {

    @NotBlank(message = "El tipo de ingrediente no puede estar vacío")
    private String tipoIngrediente;

    @NotBlank(message = "El nombre del ingrediente no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La composición del ingrediente no puede estar vacía")
    private String composicion;
}
