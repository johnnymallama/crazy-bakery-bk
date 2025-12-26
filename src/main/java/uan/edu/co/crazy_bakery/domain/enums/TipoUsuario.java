package uan.edu.co.crazy_bakery.domain.enums;

public enum TipoUsuario {
    CONSUMIDOR("consumidor", "Consumidor"),
    ADMINISTRADOR("administrador", "Administrador");

    private final String codigo;
    private final String nombre;

    TipoUsuario(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }
}
