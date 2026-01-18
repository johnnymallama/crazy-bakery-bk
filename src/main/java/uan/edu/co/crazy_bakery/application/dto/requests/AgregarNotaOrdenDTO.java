package uan.edu.co.crazy_bakery.application.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AgregarNotaOrdenDTO {

    @NotBlank(message = "La nota no puede estar vacía")
    private String nota;
}
