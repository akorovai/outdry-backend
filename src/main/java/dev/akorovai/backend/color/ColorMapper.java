package dev.akorovai.backend.color;

import dev.akorovai.backend.color.response.ColorResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ColorMapper {
	ColorMapper INSTANCE = Mappers.getMapper(ColorMapper.class);

	ColorResponse toColorResponse( Color color );
	@Mapping(target = "products", ignore = true)
	Color toColor( ColorResponse colorResponse);
}