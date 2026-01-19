package uan.edu.co.crazy_bakery.infrastructure.web.config.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

@Component
public class StringToTipoIngredienteConverter implements Converter<String, TipoIngrediente> {

    @Override
    public TipoIngrediente convert(String source) {
        try {
            return TipoIngrediente.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // O manejar el error como prefieras
        }
    }
}
