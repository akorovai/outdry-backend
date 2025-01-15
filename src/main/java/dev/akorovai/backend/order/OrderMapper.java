package dev.akorovai.backend.order;


import dev.akorovai.backend.order.response.OrderResponse;
import dev.akorovai.backend.orderItem.OrderItemMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",uses = OrderItemMapper.class)
public interface OrderMapper {

	OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

	@Mapping(source = "orderItems", target = "orderItems")
	OrderResponse toResponse( Order order);

	List<OrderResponse> toResponseList( List<Order> orders);
}