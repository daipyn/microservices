package com.embarkx.reviewms.review.service;

import com.embarkx.reviewms.review.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<Review> findAllByCompanyId(Long companyId);
    Review createReview(Long companyId, Review review);
    Optional<Review> findById(Long companyId, Long reviewId);
    boolean deleteReview(Long companyId, Long reviewId);
    Optional<Review> updateReview(Long companyId, Long reviewId, Review review);
    List<Review> getallReviews(Long companyId);
} 