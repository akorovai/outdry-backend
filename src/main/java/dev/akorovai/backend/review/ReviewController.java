package dev.akorovai.backend.review;

import dev.akorovai.backend.review.request.AddReviewRequest;
import dev.akorovai.backend.review.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public List<ReviewResponse> getReviewsByProductId(@PathVariable Long productId) {
        return reviewService.getReviewsByProductId(productId);
    }

    @PostMapping
    public void addReviewToProduct(@RequestBody AddReviewRequest request) {
        reviewService.addReviewToProduct(request);
    }
}