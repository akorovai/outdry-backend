package dev.akorovai.backend.type;


import dev.akorovai.backend.type.response.TypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TypeMapper {
    TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);

    TypeResponse toTypeResponse(Type type);

    @Mapping(target = "products", ignore = true)
    Type toType(TypeResponse typeResponse);
}