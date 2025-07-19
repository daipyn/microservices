package com.embarkx.companyms.service.impl;


import com.embarkx.companyms.client.ReviewClient;
import com.embarkx.companyms.messaging.ReviewMessage;
import com.embarkx.companyms.model.Company;
import com.embarkx.companyms.repository.CompanyRepository;
import com.embarkx.companyms.service.CompanyService;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final ReviewClient reviewClient;

    @Override
    @Transactional(readOnly = true)
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    @Override
    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    @Override
    public boolean deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            return false;
        }
        companyRepository.deleteById(id);
        return true;
    }

    @Override
    public Optional<Company> updateCompany(Long id, Company company) {
        return companyRepository.findById(id)
                .map(existingCompany -> {
                    existingCompany.setName(company.getName());
                    existingCompany.setDescription(company.getDescription());
                    existingCompany.setAddress(company.getAddress());
                    return companyRepository.save(existingCompany);
                });
    }

    @Override
    public void updateCompanyRating(ReviewMessage reviewMessage) {
        log.info("Processing ReviewMessage - ID: {}, CompanyId: {}, Title: {}", 
                reviewMessage.getId(), reviewMessage.getCompanyId(), reviewMessage.getTitle());
        
        System.out.println("Review Message : "+reviewMessage.getDescription());
        
        if (reviewMessage.getCompanyId() == null) {
            log.error("CompanyId is null in ReviewMessage");
            return;
        }
        
        try {
            Company company = companyRepository.findById(reviewMessage.getCompanyId())
                    .orElseThrow(() -> new NotFoundException("Company not found "+reviewMessage.getCompanyId()));

            double averageRating = reviewClient.getAverageRatingForCompany(reviewMessage.getCompanyId());
            company.setRating(averageRating);
            companyRepository.save(company);
            log.info("Successfully updated company rating to: {}", averageRating);
        } catch (Exception e) {
            log.error("Error updating company rating", e);
            throw e;
        }
    }
} 