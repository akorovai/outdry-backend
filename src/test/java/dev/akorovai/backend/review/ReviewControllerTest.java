package dev.akorovai.backend.review;

import com.github.javafaker.Faker;
import dev.akorovai.backend.review.request.AddReviewRequest;
import dev.akorovai.backend.review.response.ReviewResponse;
import dev.akorovai.backend.security.ResponseRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewController Tests")
class ReviewControllerTest {

	@Mock
	private ReviewService reviewService;

	@InjectMocks
	private ReviewController reviewController;

	private Faker faker;

	@BeforeEach
	void setUp() {
		faker = new Faker();
	}

	@Nested
	@DisplayName("GET /api/reviews/product/{productId}")
	class GetReviewsByProductIdTests {

		@Test
		@DisplayName("Should return reviews for a valid product ID")
		void shouldReturnReviewsForValidProductId() {
			// Arrange
			Long productId = faker.number().randomNumber();
			List<ReviewResponse> reviews = List.of(ReviewResponse.builder().id(1L).rating(5).comment("Great product!").build(), ReviewResponse.builder().id(2L).rating(3).comment("Average product.").build());

			when(reviewService.getReviewsByProductId(productId)).thenReturn(reviews);

			// Act
			ResponseRecord response = reviewController.getReviewsByProductId(productId);

			// Assert
			assertThat(response.code()).isEqualTo(HttpStatus.OK.value());
			assertThat(response.message()).isEqualTo(reviews);

			// Verify Mockito interaction
			verify(reviewService, times(1)).getReviewsByProductId(productId);
		}

		@Test
		@DisplayName("Should return empty list for a product with no reviews")
		void shouldReturnEmptyListForProductWithNoReviews() {
			// Arrange
			Long productId = faker.number().randomNumber();
			when(reviewService.getReviewsByProductId(productId)).thenReturn(List.of());

			// Act
			ResponseRecord response = reviewController.getReviewsByProductId(productId);

			// Assert
			assertThat(response.code()).isEqualTo(HttpStatus.OK.value());
			assertThat((List<?>) response.message()).isEmpty();

			// Verify Mockito interaction
			verify(reviewService, times(1)).getReviewsByProductId(productId);
		}
	}

	@Nested
	@DisplayName("POST /api/reviews")
	class AddReviewToProductTests {

		@Test
		@DisplayName("Should add a review and return success response")
		void shouldAddReviewAndReturnSuccessResponse() {
			// Arrange
			AddReviewRequest request = AddReviewRequest.builder().productId(faker.number().randomNumber()).comment(faker.lorem().sentence()).rating(faker.number().numberBetween(1, 5)).build();

			doNothing().when(reviewService).addReviewToProduct(request);

			// Act
			ResponseRecord response = reviewController.addReviewToProduct(request);

			// Assert
			assertThat(response.code()).isEqualTo(HttpStatus.CREATED.value());
			assertThat(response.message()).isEqualTo("Review added successfully");

			// Verify Mockito interaction
			verify(reviewService, times(1)).addReviewToProduct(request);
		}
	}
}