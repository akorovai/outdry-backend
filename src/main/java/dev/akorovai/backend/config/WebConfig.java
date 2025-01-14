package dev.akorovai.backend.config;

import dev.akorovai.backend.product.converter.StringToColorConverter;
import dev.akorovai.backend.product.converter.StringToGenderConverter;
import dev.akorovai.backend.type.StringToTypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final StringToTypeConverter stringToTypeConverter;
    private final StringToGenderConverter stringToGenderConverter;
    private final StringToColorConverter stringToColorConverter;



    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToTypeConverter);
        registry.addConverter(stringToGenderConverter);
        registry.addConverter(stringToColorConverter);
    }
}