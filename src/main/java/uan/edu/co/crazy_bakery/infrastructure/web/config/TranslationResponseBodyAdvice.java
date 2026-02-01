package uan.edu.co.crazy_bakery.infrastructure.web.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import uan.edu.co.crazy_bakery.application.services.OpenAITranslationService;

@RestControllerAdvice
public class TranslationResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final OpenAITranslationService translationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public TranslationResponseBodyAdvice(OpenAITranslationService translationService, ObjectMapper objectMapper) {
        this.translationService = translationService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Queremos que este interceptor se aplique a todos los tipos de respuesta
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // Obtener el header 'language' de la solicitud
        String targetLanguage = request.getHeaders().getFirst("language");

        // Si el header es 'en', procedemos a traducir.
        // También nos aseguramos de no traducir la respuesta de error de Swagger o la propia traducción.
        if (targetLanguage != null && targetLanguage.equalsIgnoreCase("en") && body != null) {
            // Evitar traducir respuestas que no son JSON o que ya están procesadas
            if (!selectedContentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                return body;
            }

            try {
                // 1. Convertir el cuerpo de la respuesta (el DTO) a un String JSON
                String originalJson = objectMapper.writeValueAsString(body);

                // 2. Usar el servicio de OpenAI para traducir el JSON
                String translatedJson = translationService.translateJsonToEnglish(originalJson);

                // 3. Si la traducción fue exitosa, convertir el JSON traducido de nuevo a un objeto.
                // Si no, devolvemos el cuerpo original.
                if (translatedJson != null && !translatedJson.isEmpty()) {
                    // OpenAI a veces devuelve un String JSON puro, necesitamos deserializarlo.
                    // El tipo de 'body' es el DTO original, así que podemos usar su clase.
                    return objectMapper.readValue(translatedJson, body.getClass());
                }
            } catch (JsonProcessingException e) {
                // Loguear el error, pero no interrumpir el flujo.
                // Devolvemos el cuerpo original en caso de error.
                System.err.println("Error processing JSON for translation: " + e.getMessage());
                return body;
            }
        }

        // Si no se requiere traducción, devolver el cuerpo original sin modificar.
        return body;
    }
}
