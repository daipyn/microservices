package com.embarkx.jobms.job.repository;

import com.embarkx.jobms.job.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

}
