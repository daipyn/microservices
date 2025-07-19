package com.embarkx.jobms.job.service.impl;

import com.embarkx.jobms.job.clients.CompanyClient;
import com.embarkx.jobms.job.clients.ReviewClient;
import com.embarkx.jobms.job.dto.FacetDTO;
import com.embarkx.jobms.job.dto.JobDTO;
import com.embarkx.jobms.job.dto.SearchRequestDTO;
import com.embarkx.jobms.job.dto.SearchResponseDTO;
import com.embarkx.jobms.job.external.Company;
import com.embarkx.jobms.job.external.Review;
import com.embarkx.jobms.job.mapper.JobMapper;
import com.embarkx.jobms.job.model.Job;
import com.embarkx.jobms.job.repository.JobESRepository;
import com.embarkx.jobms.job.repository.JobRepository;
import com.embarkx.jobms.job.service.JobService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.elasticsearch.core.AggregationsContainer;

@Service
@RequiredArgsConstructor
@Transactional
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobESRepository jobEsRepository;
    private final ElasticsearchOperations elasticsearchOperations;

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
    @RateLimiter(name = "companyBreaker", fallbackMethod = "companyFallback")
    public Company getCompanyWithBreaker(Long companyId) {
        return companyClient.getCompany(companyId);
    }
    public Company companyFallback(Long companyId, Exception e) {
        return new Company(-1L, "Unknown", "Company service unavailable", "N/A");
    }

    @CircuitBreaker(name = "reviewBreaker", fallbackMethod = "reviewFallback")
    @Retry(name = "reviewBreaker", fallbackMethod = "reviewFallback")
    @RateLimiter(name = "reviewBreaker", fallbackMethod = "reviewFallback")
    public List<Review> getReviewsWithBreaker(Long companyId) {
        return reviewClient.getReviewsByCompanyId(companyId);
    }
    public List<Review> reviewFallback(Long companyId, Exception e) {
        return new ArrayList<>();
    }

    @Override
    public Job createJob(Job job) {
        Job savedJobInDb = jobRepository.save(job);
        jobEsRepository.save(job);
        return savedJobInDb;
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
        jobEsRepository.deleteById(id);
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
                    jobEsRepository.save(existingJob);
                    return jobRepository.save(existingJob);
                });
    }

    @Override
    public SearchResponseDTO search(SearchRequestDTO request) {

        // 1. Build the main search query (the "what")
        Query multiMatchQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(mm -> mm
                                .query(request.getQuery())
                                .fields("title", "description")
                        )
                )
                .build().getQuery();

        // 2. Build the filter query (the "where")
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder().must(multiMatchQuery);

        if (request.getLocationFilter() != null && !request.getLocationFilter().isBlank()) {
            boolQueryBuilder.filter(f -> f
                    .term(t -> t
                            .field("location")
                            .value(request.getLocationFilter())
                    )
            );
        }

        // 3. Define the aggregation
        Aggregation locationAggregation = Aggregation.of(a -> a
                .terms(ta -> ta
                        .field("location")
                        .size(10) // Limit to top 10 locations
                )
        );

        // 4. Combine everything into the final native query
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(boolQueryBuilder.build()))
                .withAggregation("location", locationAggregation)
                .build();

        // 5. Execute the search
        SearchHits<Job> searchHits = elasticsearchOperations.search(searchQuery, Job.class);

        // 6. Parse the results 
        List<Job> jobs = searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());

        // 7. Parse aggregations using Spring's clean API
        Map<String, List<FacetDTO>> facets = new HashMap<>();
        
        if (searchHits.hasAggregations()) {
            AggregationsContainer<?> aggregationsContainer = searchHits.getAggregations();
            
            try {
                // Use reflection to get the aggregations map safely
                java.lang.reflect.Method aggregationsMethod = aggregationsContainer.getClass().getMethod("aggregations");
                Object aggregationsObj = aggregationsMethod.invoke(aggregationsContainer);
                
                if (aggregationsObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> locationAggMap = (Map<String, Object>) aggregationsObj;
                    
                    if (locationAggMap.containsKey("location")) {
                        Object locationAggObj = locationAggMap.get("location");
                        
                        // Parse using reflection to get buckets safely
                        java.lang.reflect.Method getBucketsMethod = locationAggObj.getClass().getMethod("getBuckets");
                        @SuppressWarnings("unchecked")
                        List<Object> buckets = (List<Object>) getBucketsMethod.invoke(locationAggObj);
                        
                        List<FacetDTO> locationFacets = new ArrayList<>();
                        for (Object bucket : buckets) {
                            java.lang.reflect.Method getKeyMethod = bucket.getClass().getMethod("getKey");
                            java.lang.reflect.Method getDocCountMethod = bucket.getClass().getMethod("getDocCount");
                            
                            String key = String.valueOf(getKeyMethod.invoke(bucket));
                            Long docCount = (Long) getDocCountMethod.invoke(bucket);
                            
                            locationFacets.add(new FacetDTO(key, docCount));
                        }
                        facets.put("location", locationFacets);
                    }
                }
            } catch (Exception e) {
                // If reflection fails, just log and continue with empty facets
                System.err.println("Failed to parse aggregations: " + e.getMessage());
            }
        }

        SearchResponseDTO response = new SearchResponseDTO();
        response.setJobs(jobs);
        response.setFacets(facets);

        return response;
    }
}
