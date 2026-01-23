package uan.edu.co.crazy_bakery.config;

import org.springframework.ai.image.ImageModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@Configuration
public class TestAiConfiguration {

    @Bean
    @Primary
    public ImageModel imageModel() {
        // Provide a mock ImageModel bean for tests to avoid real API calls
        // and to allow the application context to load successfully.
        return mock(ImageModel.class);
    }
}
