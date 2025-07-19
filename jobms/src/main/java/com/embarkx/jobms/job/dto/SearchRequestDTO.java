package com.embarkx.jobms.job.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchRequestDTO {
    private String query;
    private String locationFilter;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
}
