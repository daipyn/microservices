package com.embarkx.jobms.job.dto;

import com.embarkx.jobms.job.model.Job;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchResponseDTO {
    private List<Job> jobs;
    private Map<String, List<FacetDTO>> facets; // e.g., "location" -> [ {term: "London", count: 15} ]
}
