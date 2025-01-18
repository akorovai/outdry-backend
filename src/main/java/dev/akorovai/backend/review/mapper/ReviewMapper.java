package dev.akorovai.backend.review.mapper;

import dev.akorovai.backend.product.Product;
import dev.akorovai.backend.review.Review;
import dev.akorovai.backend.review.response.ReviewResponse;
import dev.akorovai.backend.review.request.AddReviewRequest;
import dev.akorovai.backend.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "product.id", target = "productId")
    ReviewResponse toReviewResponse(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "product", source = "product")
    Review toReview(AddReviewRequest request, User user, Product product);
}