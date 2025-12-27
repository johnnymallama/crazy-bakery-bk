package uan.edu.co.crazy_bakery.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum TipoUsuario {
    CONSUMIDOR("consumidor", "Consumidor"),
    ADMINISTRADOR("administrador", "Administrador");

    private final String codigo;
    private final String nombre;

    TipoUsuario(String codigo, String nombre) {
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
    public static TipoUsuario fromCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        return Stream.of(TipoUsuario.values())
                .filter(c -> c.getCodigo().equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Código no válido para TipoUsuario: " + codigo));
    }
}
