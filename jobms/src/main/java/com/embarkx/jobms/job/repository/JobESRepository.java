package com.embarkx.jobms.job.repository;

import com.embarkx.jobms.job.model.Job;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface JobESRepository extends ElasticsearchRepository<Job, Long> {
    List<Job> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

}
