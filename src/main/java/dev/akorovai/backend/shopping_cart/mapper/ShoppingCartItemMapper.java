package dev.akorovai.backend.shopping_cart.mapper;

import dev.akorovai.backend.review.mapper.ReviewMapper;
import dev.akorovai.backend.shopping_cart.ShoppingCartItem;
import dev.akorovai.backend.shopping_cart.response.ShoppingCartItemResponse;
import dev.akorovai.backend.product.mapper.ProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface ShoppingCartItemMapper {
    ShoppingCartItemMapper INSTANCE = Mappers.getMapper(ShoppingCartItemMapper.class);
    @Mapping(source = "id", target = "id")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "product", target = "product")
    @Mapping(source = "user.id", target = "userId")
    ShoppingCartItemResponse toShoppingCartItemResponse(ShoppingCartItem shoppingCartItem);
}