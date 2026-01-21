package uan.edu.co.crazy_bakery.application.dto.responses;

import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

public class IngredienteDTO {
    private Long id;
    private String nombre;
    private String composicion;
    private TipoIngrediente tipoIngrediente;
    private float costoPorGramo;
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
