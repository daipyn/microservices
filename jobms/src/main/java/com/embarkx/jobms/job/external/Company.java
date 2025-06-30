package com.embarkx.jobms.job.external;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
public class Company {
    private Long id;

    private String name;

    private String description;

    private String address;
}
