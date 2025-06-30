package com.embarkx.reviewms.review;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(@RequestParam Long companyId) {
        return ResponseEntity.ok(reviewService.findAllByCompanyId(companyId));
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestParam Long companyId,  @RequestBody Review review) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(companyId, review));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReviewById(@RequestParam Long companyId, @PathVariable Long reviewId) {
        return reviewService.findById(companyId, reviewId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(@RequestParam Long companyId, @PathVariable Long reviewId,  @RequestBody Review review) {
        return reviewService.updateReview(companyId, reviewId, review)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@RequestParam Long companyId, @PathVariable Long reviewId) {
        if (reviewService.deleteReview(companyId, reviewId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
