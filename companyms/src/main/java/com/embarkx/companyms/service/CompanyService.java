package com.embarkx.companyms.service;


import com.embarkx.companyms.model.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    List<Company> findAll();

    Company createCompany(Company company);

    Optional<Company> findById(Long id);

    boolean deleteCompany(Long id);

    Optional<Company> updateCompany(Long id, Company company);
} 