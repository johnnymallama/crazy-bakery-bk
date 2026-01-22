package uan.edu.co.crazy_bakery.infrastructure.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uan.edu.co.crazy_bakery.infrastructure.web.config.converters.StringToTipoIngredienteConverter;
import uan.edu.co.crazy_bakery.infrastructure.web.config.converters.StringToTipoRecetaConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToTipoIngredienteConverter());
        registry.addConverter(new StringToTipoRecetaConverter());
    }
}
