package uan.edu.co.crazy_bakery.infrastructure.web.config;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class ImageIAConfig {
    
    @Bean
    public ImageModel imageModel() {
        OpenAiImageApi openAiImageApi = OpenAiImageApi.builder()
        .apiKey(System.getenv("OPENAI_API_KEY"))
        .build();
        return new OpenAiImageModel(openAiImageApi);
    }
}
