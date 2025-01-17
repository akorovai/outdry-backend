package dev.akorovai.backend.review.mapper;

import dev.akorovai.backend.product.Product;
import dev.akorovai.backend.review.Review;
import dev.akorovai.backend.review.request.AddReviewRequest;
import dev.akorovai.backend.review.response.ReviewResponse;
import dev.akorovai.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReviewMapper Tests")
class ReviewMapperTest {

	private ReviewMapper reviewMapper;

	@BeforeEach
	void setUp() {
		reviewMapper = ReviewMapper.INSTANCE;
	}

	@Test
	@DisplayName("Should map Review to ReviewResponse correctly")
	void shouldMapReviewToReviewResponse() {
		// Arrange
		User user = User.builder()
				            .id(1L)
				            .nickname("testUser")
				            .build();

		Product product = Product.builder()
				                  .id(2L)
				                  .name("Test Product")
				                  .build();

		Review review = Review.builder()
				                .id(3L)
				                .rating(5)
				                .comment("Great product!")
				                .createdAt(LocalDateTime.now())
				                .subject("Quality")
				                .user(user)
				                .product(product)
				                .build();

		// Act
		ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);

		// Assert
		assertThat(reviewResponse).isNotNull();
		assertThat(reviewResponse.getId()).isEqualTo(review.getId());
		assertThat(reviewResponse.getRating()).isEqualTo(review.getRating());
		assertThat(reviewResponse.getComment()).isEqualTo(review.getComment());
		assertThat(reviewResponse.getCreatedAt()).isEqualTo(review.getCreatedAt());
		assertThat(reviewResponse.getSubject()).isEqualTo(review.getSubject());
		assertThat(reviewResponse.getUserId()).isEqualTo(user.getId());
		assertThat(reviewResponse.getProductId()).isEqualTo(product.getId());
	}

	@Test
	@DisplayName("Should map AddReviewRequest to Review correctly")
	void shouldMapAddReviewRequestToReview() {
		// Arrange
		AddReviewRequest request = AddReviewRequest.builder()
				                           .productId(2L)
				                           .comment("Good product!")
				                           .rating(4)
				                           .build();

		User user = User.builder()
				            .id(1L)
				            .nickname("testUser")
				            .build();

		Product product = Product.builder()
				                  .id(2L)
				                  .name("Test Product")
				                  .build();

		// Act
		Review review = reviewMapper.toReview(request, user, product);

		// Assert
		assertThat(review).isNotNull();
		assertThat(review.getId()).isNull(); // ID is ignored in the mapping
		assertThat(review.getRating()).isEqualTo(request.getRating());
		assertThat(review.getComment()).isEqualTo(request.getComment());
		assertThat(review.getSubject()).isNull(); // Subject is not mapped from AddReviewRequest
		assertThat(review.getUser()).isEqualTo(user);
		assertThat(review.getProduct()).isEqualTo(product);
		assertThat(review.getCreatedAt()).isNull(); // CreatedAt is ignored in the mapping
	}
}