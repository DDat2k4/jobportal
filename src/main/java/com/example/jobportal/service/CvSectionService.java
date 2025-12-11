package com.example.jobportal.service;

import com.example.jobportal.data.entity.CvSection;
import com.example.jobportal.extension.paging.Page;
import com.example.jobportal.extension.paging.Pageable;
import com.example.jobportal.repository.CvSectionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CvSectionService {

    private final CvSectionRepository repo;

    public CvSectionService(CvSectionRepository repo) {
        this.repo = repo;
    }

    public Optional<CvSection> getById(Long id) {
        return repo.findById(id);
    }

    public CvSection create(CvSection section) {
        return repo.create(section);
    }

    public Optional<CvSection> update(Long id, CvSection section) {
        return repo.update(id, section);
    }

    public int delete(Long id) {
        return repo.delete(id);
    }

    public Page<CvSection> getAll(CvSection filter, Pageable pageable) {
        return repo.findAll(filter, pageable);
    }

    /**
     * Lấy tất cả section theo CV ID, sắp xếp theo thứ tự hiển thị
     */
    public List<CvSection> getByCvId(Long cvId) {
        return repo.findByCvId(cvId);
    }

    /**
     * Xóa tất cả section theo CV ID (khi xóa CV hoặc reset CV)
     */
    public int deleteByCvId(Long cvId) {
        return repo.deleteByCvId(cvId);
    }

    /**
     * Lấy section theo filter (vd: type + cvId)
     */
    public Optional<CvSection> getByFilter(CvSection filter) {
        return repo.findByFilter(filter);
    }

    public List<String> getSkills(Long cvId) {
        List<CvSection> sections = repo.findByCvIdAndType(cvId, "SKILL");
        List<String> result = new ArrayList<>();

        for (CvSection sec : sections) {
            Map<String, Object> content = sec.getContent();
            List<Map<String, Object>> items = (List<Map<String, Object>>) content.get("items");
            if (items != null) {
                for (Map<String, Object> item : items) {
                    result.add((String) item.get("name"));
                }
            }
        }
        return result;
    }

    public List<Map<String, Object>> getExperience(Long cvId) {
        List<CvSection> sections = repo.findByCvIdAndType(cvId, "EXPERIENCE");
        List<Map<String, Object>> result = new ArrayList<>();

        for (CvSection sec : sections) {
            Map<String, Object> content = sec.getContent();
            List<Map<String, Object>> items = (List<Map<String, Object>>) content.get("items");
            if (items != null) result.addAll(items);
        }
        return result;
    }

    public List<Map<String, Object>> getEducation(Long cvId) {
        List<CvSection> sections = repo.findByCvIdAndType(cvId, "EDUCATION");
        List<Map<String, Object>> result = new ArrayList<>();

        for (CvSection sec : sections) {
            Map<String, Object> content = sec.getContent();
            List<Map<String, Object>> items = (List<Map<String, Object>>) content.get("items");
            if (items != null) result.addAll(items);
        }
        return result;
    }
}
