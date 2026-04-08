package uan.edu.co.crazy_bakery.application.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

@Schema(description = "Representación de un ingrediente")

public class IngredienteDTO {

    @Schema(description = "ID único del ingrediente", example = "1")
    private Long id;

    @Schema(description = "Nombre comercial del ingrediente", example = "Harina de trigo")
    private String nombre;

    @Schema(description = "Descripción de la composición o características", example = "Harina de trigo 000 sin gluten")
    private String composicion;

    @Schema(description = "Clasificación del ingrediente según su uso (BIZCOCHO, RELLENO, CUBERTURA)", example = "BIZCOCHO")
    private TipoIngrediente tipoIngrediente;

    @Schema(description = "Costo por gramo en pesos colombianos", example = "0.05")
    private float costoPorGramo;

    @Schema(description = "Indica si el ingrediente está activo en el catálogo", example = "true")
    private boolean estado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getComposicion() {
        return composicion;
    }

    public void setComposicion(String composicion) {
        this.composicion = composicion;
    }

    public TipoIngrediente getTipoIngrediente() {
        return tipoIngrediente;
    }

    public void setTipoIngrediente(TipoIngrediente tipoIngrediente) {
        this.tipoIngrediente = tipoIngrediente;
    }

    public float getCostoPorGramo() {
        return this.costoPorGramo;
    }

    public void setCostoPorGramo(float costoPorGramo) {
        this.costoPorGramo = costoPorGramo;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "IngredienteDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", composicion='" + composicion + '\'' +
                ", tipoIngrediente=" + tipoIngrediente +
                ", costoPorGramo=" + costoPorGramo +
                ", estado=" + estado +
                '}';
    }
}
