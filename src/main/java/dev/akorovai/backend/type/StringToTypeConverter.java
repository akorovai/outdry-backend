package dev.akorovai.backend.type;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToTypeConverter implements Converter<String, Type> {

    private final TypeRepository typeRepository;

    public StringToTypeConverter(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    @Override
    public Type convert(String source) {
        // Fetch the Type entity from the database by name
        return typeRepository.findByName(source)
                       .orElseThrow(() -> new IllegalArgumentException("Invalid type name: " + source));
    }
}