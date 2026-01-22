package uan.edu.co.crazy_bakery.infrastructure.web.config.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

@Component
public class StringToTipoRecetaConverter implements Converter<String, TipoReceta> {

    @Override
    public TipoReceta convert(String source) {
        try {
            return TipoReceta.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
