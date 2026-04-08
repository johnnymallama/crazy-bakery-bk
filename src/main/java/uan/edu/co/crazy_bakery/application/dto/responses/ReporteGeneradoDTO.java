package uan.edu.co.crazy_bakery.application.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Metadata del reporte PDF generado y almacenado en Firebase Storage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteGeneradoDTO {

    @Schema(description = "Nombre descriptivo del reporte generado", example = "Análisis de Ingredientes")
    private String nombre_reporte;

    @Schema(description = "Fecha y hora de generación del reporte (formato yyyy-MM-dd HH:mm:ss)", example = "2024-03-15 10:30:00")
    private String fecha_reporte;

    @Schema(description = "URL pública del PDF almacenado en Firebase Storage", example = "https://storage.googleapis.com/crazy-bakery/reporte_001.pdf")
    private String url;

}
