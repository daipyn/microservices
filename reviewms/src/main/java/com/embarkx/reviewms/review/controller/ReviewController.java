package com.embarkx.reviewms.review.controller;

import ch.qos.logback.core.util.StringUtil;
import com.embarkx.reviewms.review.messaging.ReviewMessageProducer;
import com.embarkx.reviewms.review.model.Review;
import com.embarkx.reviewms.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMessageProducer reviewMessageProducer;

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(@RequestParam Long companyId) {
        return ResponseEntity.ok(reviewService.findAllByCompanyId(companyId));
    }

    @PostMapping
    public ResponseEntity<String> createReview(@RequestParam Long companyId,  @RequestBody Review review) {
        Review savedReview = reviewService.createReview(companyId, review);
        if(savedReview!=null){
            log.info("Review DTO sending to RabbitMQ: "+ savedReview);
            reviewMessageProducer.sendMessage(savedReview);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Added entity successfully");
        }
        else{
            return new ResponseEntity<>("Review not created",HttpStatus.NOT_FOUND);
        }

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

    @GetMapping("/averageRating")
    public Double getAverageReview(@RequestParam("companyId") Long companyId){
        List<Review> reviews = reviewService.getallReviews(companyId);
        Double average = reviews.stream().mapToDouble(review -> (double) review.getRating()).average().orElse(0.0);
        log.info("Average calculated "+average);
        return average;
    }

}
