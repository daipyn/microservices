package com.embarkx.reviewms.review.messaging;

import com.embarkx.reviewms.review.dto.ReviewMessage;
import com.embarkx.reviewms.review.model.Review;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewMessageProducer {
    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(Review review){
        log.info("Original Review object - ID: {}, CompanyId: {}, Title: {}", 
                review.getId(), review.getCompanyId(), review.getTitle());
        
        ReviewMessage reviewMessage = new ReviewMessage();
        reviewMessage.setId(review.getId());
        reviewMessage.setTitle(review.getTitle());
        reviewMessage.setCompanyId(review.getCompanyId());
        reviewMessage.setDescription(review.getDescription());
        reviewMessage.setRating(review.getRating());
        
        log.info("ReviewMessage before sending - ID: {}, CompanyId: {}, Title: {}", 
                reviewMessage.getId(), reviewMessage.getCompanyId(), reviewMessage.getTitle());
        
        rabbitTemplate.convertAndSend("companyRatingQueue", reviewMessage);
    }

}
