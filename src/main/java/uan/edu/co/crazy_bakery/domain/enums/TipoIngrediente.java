package uan.edu.co.crazy_bakery.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;


public enum TipoIngrediente {

    MASA("01", "Masa"),
    RELLENO("02", "Relleno"),
    COBERTURA("03", "Cobertura");

    private final String codigo;
    private final String nombre;

    TipoIngrediente(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    @JsonValue
    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    @JsonCreator
    public static TipoIngrediente fromCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        return Stream.of(TipoIngrediente.values())
                .filter(c -> c.getCodigo().equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Código no válido para TipoIngrediente: " + codigo));
    }
}
