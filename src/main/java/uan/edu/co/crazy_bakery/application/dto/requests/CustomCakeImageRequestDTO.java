package uan.edu.co.crazy_bakery.application.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomCakeImageRequestDTO {

    @NotBlank(message = "El tipo de receta no puede estar vacío")
    private String tipoReceta;

    @NotBlank(message = "El tamaño no puede estar vacío")
    private String tamano;

    @NotEmpty(message = "La lista de ingredientes no puede estar vacía")
    @NotNull(message = "La lista de ingredientes no puede ser nula")
    private List<IngredientDetailDTO> ingredientes;

    private String detalle;
}
