package com.embarkx.companyms.messaging;

import com.embarkx.companyms.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewMessageConsumer {
    private final CompanyService companyService;

    @RabbitListener(queues = "companyRatingQueue")
    public void consumeMessage(ReviewMessage reviewMessage){
        log.info("Received ReviewMessage - ID: {}, CompanyId: {}, Title: {}", 
                reviewMessage.getId(), reviewMessage.getCompanyId(), reviewMessage.getTitle());
        companyService.updateCompanyRating(reviewMessage);
    }
}
