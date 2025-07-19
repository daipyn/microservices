package com.embarkx.jobms.job.service;

import com.embarkx.jobms.job.dto.JobDTO;
import com.embarkx.jobms.job.dto.SearchRequestDTO;
import com.embarkx.jobms.job.dto.SearchResponseDTO;
import com.embarkx.jobms.job.model.Job;

import java.util.List;
import java.util.Optional;

public interface JobService {
    List<JobDTO> findAll();
    Job createJob(Job job);
    Optional<JobDTO> findById(Long id);
    boolean deleteJob(Long id);
    Optional<Job> updateJob(Long id, Job job);
    SearchResponseDTO search(SearchRequestDTO searchRequestDTO);
}
