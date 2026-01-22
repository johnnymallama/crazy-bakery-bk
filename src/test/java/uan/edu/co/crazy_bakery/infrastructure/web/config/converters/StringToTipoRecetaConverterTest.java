package uan.edu.co.crazy_bakery.infrastructure.web.config.converters;

import org.junit.jupiter.api.Test;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

import static org.junit.jupiter.api.Assertions.*;

class StringToTipoRecetaConverterTest {

    private final StringToTipoRecetaConverter converter = new StringToTipoRecetaConverter();

    @Test
    void testConvert() {
        assertEquals(TipoReceta.TORTA, converter.convert("torta"));
        assertEquals(TipoReceta.CUPCAKE, converter.convert("cupcake"));
    }

    @Test
    void testConvert_Invalid() {
        assertNull(converter.convert("invalido"));
    }

    @Test
    void testConvert_Null() {
        assertThrows(NullPointerException.class, () -> converter.convert(null));
    }
}
