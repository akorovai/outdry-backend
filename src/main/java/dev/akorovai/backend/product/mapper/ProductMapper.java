package dev.akorovai.backend.product.mapper;

import dev.akorovai.backend.color.ColorMapper;
import dev.akorovai.backend.product.Product;
import dev.akorovai.backend.product.request.ProductRequest;
import dev.akorovai.backend.product.response.ProductResponse;
import dev.akorovai.backend.product.response.ProductWithSizeAvailabilityResponse;
import dev.akorovai.backend.type.TypeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ColorMapper.class, TypeMapper.class})
public interface ProductMapper {

	ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

	@Mapping(source = "id", target = "id")
	@Mapping(source = "name", target = "name")
	@Mapping(source = "description", target = "description")
	@Mapping(source = "price", target = "price")
	@Mapping(source = "color", target = "color")
	@Mapping(source = "type", target = "type")
	@Mapping(source = "links", target = "links")
	@Mapping(source = "gender", target = "gender")
	@Mapping(source = "amount", target = "amount")
	@Mapping(source = "discount", target = "discount")
	@Mapping(source = "size", target = "size")
	ProductResponse toProductResponse( Product product );

	@Mapping(source = "id", target = "id")
	@Mapping(source = "name", target = "name")
	@Mapping(source = "description", target = "description")
	@Mapping(source = "price", target = "price")
	@Mapping(source = "color", target = "color")
	@Mapping(source = "type", target = "type")
	@Mapping(source = "links", target = "links")
	@Mapping(source = "gender", target = "gender")
	@Mapping(source = "amount", target = "amount")
	@Mapping(source = "discount", target = "discount")
	@Mapping(source = "size", target = "size")
	ProductWithSizeAvailabilityResponse toProductWithSizeAvailabilityResponse( Product product );

	@Mapping(source = "id", target = "id")
	@Mapping(source = "name", target = "name")
	@Mapping(source = "description", target = "description")
	@Mapping(source = "links", target = "links")
	@Mapping(source = "amount", target = "amount")
	@Mapping(source = "discount", target = "discount")
	@Mapping(source = "size", target = "size")
	@Mapping(source = "type", target = "type")
	@Mapping(source = "color", target = "color")
	@Mapping(source = "price", target = "price")
	@Mapping(source = "gender", target = "gender")
	Product toProduct( ProductRequest productRequest );

	@Mapping(source = "name", target = "name")
	@Mapping(source = "description", target = "description")
	@Mapping(source = "links", target = "links")
	@Mapping(source = "amount", target = "amount")
	@Mapping(source = "discount", target = "discount")
	@Mapping(source = "size", target = "size")
	@Mapping(source = "type", target = "type")
	@Mapping(source = "color", target = "color")
	@Mapping(source = "price", target = "price")
	@Mapping(source = "gender", target = "gender")
	void updateProductFromRequest( ProductRequest productRequest, @MappingTarget Product product );
}
