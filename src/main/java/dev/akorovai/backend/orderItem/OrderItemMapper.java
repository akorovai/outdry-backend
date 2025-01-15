package dev.akorovai.backend.orderItem;

import dev.akorovai.backend.orderItem.OrderItem;
import dev.akorovai.backend.orderItem.dto.OrderItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

	OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

	@Mapping(source = "product.id", target = "productId")
	@Mapping(source = "product.name", target = "productName")
	@Mapping(source = "product.size.displayName", target = "size")
	@Mapping(source = "product.color.name", target = "color")
//	@Mapping(source = "product.links", target = "imageLink")
	OrderItemResponse toResponse( OrderItem orderItem);

	default Set<OrderItemResponse> toResponseSet( Set<OrderItem> orderItems) {
		return orderItems.stream()
				       .map(this::toResponse)
				       .collect(Collectors.toSet());
	}

	default String mapFirstLink( List<String> links) {
		return links != null && !links.isEmpty() ? links.get(0) : null;
	}
}