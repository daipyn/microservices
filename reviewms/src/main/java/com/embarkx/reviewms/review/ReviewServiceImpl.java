package com.embarkx.reviewms.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
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
        Review reviewWithCompanyId = Review.builder()
                .title(review.getTitle())
                .description(review.getDescription())
                .rating(review.getRating())
                .companyId(companyId)
                .build();
        return reviewRepository.save(reviewWithCompanyId);
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
} 