package com.embarkx.jobms.job.service.impl;

import com.embarkx.jobms.job.clients.CompanyClient;
import com.embarkx.jobms.job.clients.ReviewClient;
import com.embarkx.jobms.job.dto.JobDTO;
import com.embarkx.jobms.job.external.Company;
import com.embarkx.jobms.job.external.Review;
import com.embarkx.jobms.job.mapper.JobMapper;
import com.embarkx.jobms.job.model.Job;
import com.embarkx.jobms.job.repository.JobRepository;
import com.embarkx.jobms.job.service.JobService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobServiceImpl implements JobService {
    
    private final JobRepository jobRepository;

    @Autowired
    private CompanyClient companyClient;
    int attempt=0;
    @Autowired
    private ReviewClient reviewClient;

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "companyBreaker", fallbackMethod = "jobListFallback")
    @Retry(name = "companyBreaker", fallbackMethod = "jobListFallback")
    public List<JobDTO> findAll() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<JobDTO> jobListFallback(Exception e) {
        // Return a list with a single JobDTO with safe defaults
        JobDTO fallbackJob = new JobDTO();
        fallbackJob.setId(-1L);
        fallbackJob.setTitle("Service Unavailable");
        fallbackJob.setDescription("Job data not available");
        fallbackJob.setMinSalary(null);
        fallbackJob.setMaxSalary(null);
        fallbackJob.setLocation("N/A");
        fallbackJob.setCompany(new Company(-1L, "Unknown", "Company service unavailable", "N/A"));
        fallbackJob.setReview(new ArrayList<>());
        List<JobDTO> fallbackList = new ArrayList<>();
        fallbackList.add(fallbackJob);
        return fallbackList;
    }

    private JobDTO convertToDTO(Job job){
        Company company;
        List<Review> reviews;
        try {
            company = getCompanyWithBreaker(job.getCompanyId());
        } catch (Exception e) {
            company = new Company(-1L, "Unknown", "Company service unavailable", "N/A");
        }
        try {
            reviews = getReviewsWithBreaker(job.getCompanyId());
        } catch (Exception e) {
            reviews = new ArrayList<>();
        }
        JobDTO jobDTO = JobMapper.mapToJobWithCompanyDto(job, company, reviews);
        return jobDTO;
    }

    @CircuitBreaker(name = "companyBreaker", fallbackMethod = "companyFallback")
    @Retry(name = "companyBreaker", fallbackMethod = "companyFallback")
    public Company getCompanyWithBreaker(Long companyId) {
        return companyClient.getCompany(companyId);
    }
    public Company companyFallback(Long companyId, Exception e) {
        return new Company(-1L, "Unknown", "Company service unavailable", "N/A");
    }

    @CircuitBreaker(name = "reviewBreaker", fallbackMethod = "reviewFallback")
    @Retry(name = "reviewBreaker", fallbackMethod = "reviewFallback")
    public List<Review> getReviewsWithBreaker(Long companyId) {
        return reviewClient.getReviewsByCompanyId(companyId);
    }
    public List<Review> reviewFallback(Long companyId, Exception e) {
        return new ArrayList<>();
    }

    @Override
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "companyBreaker", fallbackMethod = "jobOptionalFallback")
    @Retry(name = "companyBreaker", fallbackMethod = "jobOptionalFallback")
    public Optional<JobDTO> findById(Long id) {
        System.out.println("Attempt : "+ ++attempt);
        Optional<Job> job = jobRepository.findById(id);
        return job.map(this::convertToDTO);
    }

    public Optional<JobDTO> jobOptionalFallback(Long id, Exception e) {
        JobDTO fallbackJob = new JobDTO();
        fallbackJob.setId(id);
        fallbackJob.setTitle("Service Unavailable");
        fallbackJob.setDescription("Job data not available");
        fallbackJob.setMinSalary(null);
        fallbackJob.setMaxSalary(null);
        fallbackJob.setLocation("N/A");
        fallbackJob.setCompany(new Company(-1L, "Unknown", "Company service unavailable", "N/A"));
        fallbackJob.setReview(new ArrayList<>());
        return Optional.of(fallbackJob);
    }

    @Override
    public boolean deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            return false;
        }
        jobRepository.deleteById(id);
        return true;
    }

    @Override
    public Optional<Job> updateJob(Long id, Job job) {
        return jobRepository.findById(id)
                .map(existingJob -> {
                    existingJob.setTitle(job.getTitle());
                    existingJob.setDescription(job.getDescription());
                    existingJob.setMinSalary(job.getMinSalary());
                    existingJob.setMaxSalary(job.getMaxSalary());
                    existingJob.setLocation(job.getLocation());
                    return jobRepository.save(existingJob);
                });
    }
}
