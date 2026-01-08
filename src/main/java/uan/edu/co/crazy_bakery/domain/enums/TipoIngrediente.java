package uan.edu.co.crazy_bakery.domain.enums;

public enum TipoIngrediente {
    BIZCOCHO("BIZ"),
    COBERTURA("COB"),
    RELLENO("REL");

    private final String prefijo;

    TipoIngrediente(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getPrefijo() {
        return prefijo;
    }
}
