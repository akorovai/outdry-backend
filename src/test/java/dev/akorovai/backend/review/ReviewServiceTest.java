package dev.akorovai.backend.review;

import com.github.javafaker.Faker;
import dev.akorovai.backend.handler.product.ProductNotFoundException;
import dev.akorovai.backend.product.Product;
import dev.akorovai.backend.product.ProductRepository;
import dev.akorovai.backend.review.request.AddReviewRequest;
import dev.akorovai.backend.review.response.ReviewResponse;
import dev.akorovai.backend.security.JwtService;
import dev.akorovai.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private JwtService jwtService;

	@InjectMocks
	private ReviewService reviewService;

	private Faker faker;

	@BeforeEach
	void setUp() {
		faker = new Faker();
	}

	@Nested
	class GetReviewsByProductIdTests {

		@Test
		void getReviewsByProductId_ShouldReturnListOfReviewResponses() {
			// Arrange
			Long productId = faker.number().randomNumber();
			Review review1 = Review.builder()
					                 .id(faker.number().randomNumber())
					                 .comment(faker.lorem().sentence())
					                 .rating(faker.number().numberBetween(1, 5))
					                 .build();
			Review review2 = Review.builder()
					                 .id(faker.number().randomNumber())
					                 .comment(faker.lorem().sentence())
					                 .rating(faker.number().numberBetween(1, 5))
					                 .build();
			List<Review> reviews = List.of(review1, review2);

			when(reviewRepository.findByProductId(productId)).thenReturn(reviews);

			// Act
			List<ReviewResponse> reviewResponses = reviewService.getReviewsByProductId(productId);

			// Assert
			assertThat(reviewResponses).hasSize(2);
			assertThat(reviewResponses.get(0).getComment()).isEqualTo(review1.getComment());
			assertThat(reviewResponses.get(0).getRating()).isEqualTo(review1.getRating());
			assertThat(reviewResponses.get(1).getComment()).isEqualTo(review2.getComment());
			assertThat(reviewResponses.get(1).getRating()).isEqualTo(review2.getRating());

			verify(reviewRepository, times(1)).findByProductId(productId);
		}

		@Test
		void getReviewsByProductId_ShouldReturnEmptyListWhenNoReviewsExist() {
			// Arrange
			Long productId = faker.number().randomNumber();
			when(reviewRepository.findByProductId(productId)).thenReturn(List.of());

			// Act
			List<ReviewResponse> reviewResponses = reviewService.getReviewsByProductId(productId);

			// Assert
			assertThat(reviewResponses).isEmpty();
			verify(reviewRepository, times(1)).findByProductId(productId);
		}
	}

	@Nested
	class AddReviewToProductTests {

		@Test
		void addReviewToProduct_ShouldAddReviewWhenProductExists() {
			// Arrange
			Long productId = faker.number().randomNumber();
			AddReviewRequest request = AddReviewRequest.builder()
					                           .productId(productId)
					                           .comment(faker.lorem().sentence())
					                           .rating(faker.number().numberBetween(1, 5))
					                           .build();

			Product product = Product.builder()
					                  .id(productId)
					                  .name(faker.commerce().productName())
					                  .build();

			User user = User.builder()
					            .id(faker.number().randomNumber())
					            .email(faker.internet().emailAddress())
					            .build();

			when(productRepository.findById(productId)).thenReturn(Optional.of(product));
			when(jwtService.getAuthenticatedUser()).thenReturn(user);

			// Act
			reviewService.addReviewToProduct(request);

			// Assert
			verify(productRepository, times(1)).findById(productId);
			verify(jwtService, times(1)).getAuthenticatedUser();
			verify(reviewRepository, times(1)).save(argThat(review ->
					                                                review.getProduct().getId().equals(productId) &&
							                                                review.getUser().getId().equals(user.getId()) &&
							                                                review.getComment().equals(request.getComment()) &&
							                                                review.getRating() == request.getRating()
			));
		}

		@Test
		void addReviewToProduct_ShouldThrowProductNotFoundExceptionWhenProductDoesNotExist() {
			// Arrange
			Long productId = faker.number().randomNumber();
			AddReviewRequest request = AddReviewRequest.builder()
					                           .productId(productId)
					                           .comment(faker.lorem().sentence())
					                           .rating(faker.number().numberBetween(1, 5))
					                           .build();

			when(productRepository.findById(productId)).thenReturn(Optional.empty());

			// Act & Assert
			assertThatThrownBy(() -> reviewService.addReviewToProduct(request))
					.isInstanceOf(ProductNotFoundException.class)
					.hasMessage("Product not found");

			verify(productRepository, times(1)).findById(productId);
			verify(jwtService, never()).getAuthenticatedUser();
			verify(reviewRepository, never()).save(any(Review.class));
		}

		@Test
		void addReviewToProduct_ShouldThrowExceptionWhenRatingIsInvalid() {
			// Arrange
			Long productId = faker.number().randomNumber();
			AddReviewRequest request = AddReviewRequest.builder()
					                           .productId(productId)
					                           .comment(faker.lorem().sentence())
					                           .rating(6) // Invalid rating (should be between 1 and 5)
					                           .build();

			Product product = Product.builder()
					                  .id(productId)
					                  .name(faker.commerce().productName())
					                  .build();

			User user = User.builder()
					            .id(faker.number().randomNumber())
					            .email(faker.internet().emailAddress())
					            .build();

			when(productRepository.findById(productId)).thenReturn(Optional.of(product));
			lenient().when(jwtService.getAuthenticatedUser()).thenReturn(user); // Mark as lenient

			// Act & Assert
			assertThatThrownBy(() -> reviewService.addReviewToProduct(request))
					.isInstanceOf(RuntimeException.class)
					.hasMessage("Rating must be between 1 and 5");

			verify(productRepository, times(1)).findById(productId);
			verify(jwtService, never()).getAuthenticatedUser();
			verify(reviewRepository, never()).save(any(Review.class));
		}
	}


}