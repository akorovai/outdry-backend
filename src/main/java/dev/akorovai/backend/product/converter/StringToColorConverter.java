package dev.akorovai.backend.product.converter;

import dev.akorovai.backend.color.Color;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToColorConverter implements Converter<String, Color> {
    @Override
    public Color convert(String source) {
        return Color.builder().name(source).build();
    }
}