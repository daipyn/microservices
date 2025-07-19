package com.embarkx.jobms.job.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FacetDTO {
    private String term;
    private long count;
}
