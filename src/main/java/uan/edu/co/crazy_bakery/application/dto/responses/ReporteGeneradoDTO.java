package uan.edu.co.crazy_bakery.application.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteGeneradoDTO {

    private String nombre_reporte;
    private String fecha_reporte;
    private String url;

}
