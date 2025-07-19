package com.embarkx.reviewms.review.service.impl;

import com.embarkx.reviewms.review.model.Review;
import com.embarkx.reviewms.review.repository.ReviewRepository;
import com.embarkx.reviewms.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Review> findAllByCompanyId(Long companyId) {
        return reviewRepository.findAll();
    }

    @Override
    public Review createReview(Long companyId, Review review) {
        // TODO: Set companyId on review if field exists
        try {
        Review reviewWithCompanyId = Review.builder()
                .title(review.getTitle())
                .description(review.getDescription())
                .rating(review.getRating())
                .companyId(companyId)
                .build();
        reviewRepository.flush();
        Review saved = reviewRepository.save(reviewWithCompanyId);

        return saved;
    } catch (Exception e) {
            log.error("Failed to save the Data");
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> findById(Long companyId, Long reviewId) {
        // TODO: Implement actual filtering by companyId
        return reviewRepository.findById(reviewId);
    }

    @Override
    public boolean deleteReview(Long companyId, Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            return false;
        }
        reviewRepository.deleteById(reviewId);
        return true;
    }

    @Override
    public Optional<Review> updateReview(Long companyId, Long reviewId, Review review) {
        return reviewRepository.findById(reviewId)
                .map(existingReview -> {
                    existingReview.setTitle(review.getTitle());
                    existingReview.setDescription(review.getDescription());
                    existingReview.setRating(review.getRating());
                    // TODO: Set companyId if field exists
                    return reviewRepository.save(existingReview);
                });
    }

    @Override
    public List<Review> getallReviews(Long companyId) {
        return reviewRepository.findByCompanyId(companyId);
    }
} 