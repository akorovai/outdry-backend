package dev.akorovai.backend.review;

import dev.akorovai.backend.review.request.AddReviewRequest;
import dev.akorovai.backend.review.response.ReviewResponse;
import dev.akorovai.backend.security.ResponseRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseRecord getReviewsByProductId(@PathVariable Long productId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseRecord.builder()
                       .code(HttpStatus.OK.value())
                       .message(reviews)
                       .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseRecord addReviewToProduct(@RequestBody AddReviewRequest request) {
        reviewService.addReviewToProduct(request);
        return ResponseRecord.builder()
                       .code(HttpStatus.CREATED.value())
                       .message("Review added successfully")
                       .build();
    }
}