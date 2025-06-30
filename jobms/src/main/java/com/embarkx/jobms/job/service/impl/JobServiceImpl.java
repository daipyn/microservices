package com.embarkx.jobms.job.service.impl;

import com.embarkx.jobms.job.dto.JobDTO;
import com.embarkx.jobms.job.external.Company;
import com.embarkx.jobms.job.external.Review;
import com.embarkx.jobms.job.mapper.JobMapper;
import com.embarkx.jobms.job.model.Job;
import com.embarkx.jobms.job.repository.JobRepository;
import com.embarkx.jobms.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
    RestTemplate restTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> findAll() {
        List<Job> jobs = jobRepository.findAll();
        List<JobDTO> jobDTOS = new ArrayList<>();
        return jobs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private JobDTO convertToDTO(Job job){
        Company company = restTemplate.getForObject("http://companyms:8082/companies/" + job.getCompanyId(), Company.class);

        //Review is a list of objects so cannot use getForObject
        ResponseEntity<List<Review>> reviewResponse = restTemplate.exchange("http://reviewms:8083/reviews?companyId=" + job.getCompanyId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Review>>() {
        });

        List<Review> reviews = reviewResponse.getBody();

        JobDTO jobDTO = JobMapper.mapToJobWithCompanyDto(job, company, reviews);
        return jobDTO;
    }

    @Override
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JobDTO> findById(Long id) {
        Optional<Job> job = jobRepository.findById(id);
        return job.map(this::convertToDTO);
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
