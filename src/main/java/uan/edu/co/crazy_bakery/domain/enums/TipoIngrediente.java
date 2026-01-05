package uan.edu.co.crazy_bakery.domain.enums;

public enum TipoIngrediente {
    MASA("MAS"),
    COBERTURA("COB"),
    RELLENO("REL"),
    DECORACION("DEC");

    private final String prefijo;

    TipoIngrediente(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getPrefijo() {
        return prefijo;
    }
}
