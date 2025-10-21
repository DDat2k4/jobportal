package com.example.jobportal.service;

import com.example.jobportal.data.entity.Company;
import com.example.jobportal.data.entity.CompanyAddress;
import com.example.jobportal.data.entity.CompanySocial;
import com.example.jobportal.data.pojo.CompanyFullDTO;
import com.example.jobportal.repository.CompanyRepository;
import com.example.jobportal.repository.CompanyAddressRepository;
import com.example.jobportal.repository.CompanySocialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyInfoService {

    private final CompanyRepository companyRepository;
    private final CompanyAddressRepository addressRepository;
    private final CompanySocialRepository socialRepository;

    public Optional<CompanyFullDTO> getFullCompanyInfo(Long companyId) {
        Optional<Company> companyOpt = companyRepository.findById(companyId);
        if (companyOpt.isEmpty()) {
            return Optional.empty();
        }

        Company company = companyOpt.get();

        // Lấy địa chỉ
        List<CompanyAddress> addresses = addressRepository.findByCompanyId(companyId);
        CompanyAddress address = addresses.isEmpty() ? null : addresses.get(0);

        // Lấy danh sách social
        List<CompanySocial> socials = socialRepository.findByCompanyId(companyId);

        CompanyFullDTO dto = new CompanyFullDTO()
                .setCompany(company)
                .setAddresse(address)
                .setSocials(socials);

        return Optional.of(dto);
    }
}
