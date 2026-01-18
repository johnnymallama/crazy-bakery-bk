package uan.edu.co.crazy_bakery.application.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearOrdenDTO {

    private String usuarioId;
    private List<Long> recetaIds;
    private List<String> notas;

}
