package com.example.jobportal.data.pojo;

import com.example.jobportal.data.entity.Company;
import com.example.jobportal.data.entity.CompanyAddress;
import com.example.jobportal.data.entity.CompanySocial;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CompanyFullDTO {
    private Company company;
    private CompanyAddress addresse;
    private List<CompanySocial> socials;
}

