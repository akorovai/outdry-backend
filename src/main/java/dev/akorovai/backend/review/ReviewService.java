package dev.akorovai.backend.review;

import dev.akorovai.backend.handler.product.ProductNotFoundException;
import dev.akorovai.backend.product.Product;
import dev.akorovai.backend.product.ProductRepository;
import dev.akorovai.backend.review.mapper.ReviewMapper;
import dev.akorovai.backend.review.request.AddReviewRequest;
import dev.akorovai.backend.review.response.ReviewResponse;
import dev.akorovai.backend.security.JwtService;
import dev.akorovai.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ProductRepository productRepository;
	private final JwtService jwtService;

	public List<ReviewResponse> getReviewsByProductId(Long productId) {
		return reviewRepository.findByProductId(productId).stream()
				       .map(ReviewMapper.INSTANCE::toReviewResponse)
				       .toList();
	}

	@Transactional
	public void addReviewToProduct(AddReviewRequest request) {

		Product product = productRepository.findById(request.getProductId())
				                  .orElseThrow(() -> new ProductNotFoundException("Product not found"));

		if (request.getRating() < 1 || request.getRating() > 5) {
			throw new RuntimeException("Rating must be between 1 and 5");
		}


		User user = jwtService.getAuthenticatedUser();

		Review review = ReviewMapper.INSTANCE.toReview(request, user, product);
		reviewRepository.save(review);
	}
}