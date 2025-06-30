package com.embarkx.jobms.job.dto;

import com.embarkx.jobms.job.external.Company;
import com.embarkx.jobms.job.external.Review;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class JobDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String location;
    private Company company;
    private List<Review> review;
}
